const express = require("express");
const helmet = require("helmet");
const morgan = require("morgan");
const { createClient } = require("@supabase/supabase-js");

const app = express();
const PORT = Number(process.env.PORT || 3000);
const isProduction = process.env.NODE_ENV === "production";

app.use(helmet());
app.use(express.json({ limit: "1mb" }));
app.use(morgan(isProduction ? "combined" : "dev"));

const supabaseUrl = process.env.SUPABASE_URL;
const serviceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY;

const adminClient =
  supabaseUrl && serviceRoleKey
    ? createClient(supabaseUrl, serviceRoleKey, {
        auth: { autoRefreshToken: false, persistSession: false }
      })
    : null;

function requireBackend() {
  if (!adminClient) {
    const error = new Error("Supabase backend is not configured.");
    error.status = 503;
    throw error;
  }
}

async function requireUser(req, res, next) {
  try {
    requireBackend();

    const header = req.headers.authorization || "";
    const token = header.startsWith("Bearer ") ? header.slice(7) : null;

    if (!token) {
      return res.status(401).json({ error: "Missing Bearer token." });
    }

    const { data, error } = await adminClient.auth.getUser(token);

    if (error || !data.user) {
      return res.status(401).json({ error: "Invalid or expired session." });
    }

    req.authUser = data.user;
    next();
  } catch (error) {
    next(error);
  }
}

async function getProfile(userId) {
  const { data, error } = await adminClient
    .from("profiles")
    .select("id, full_name, email, role, status, avatar_url")
    .eq("id", userId)
    .maybeSingle();

  if (error) throw error;
  return data;
}

async function requireRole(req, res, next) {
  try {
    const profile = await getProfile(req.authUser.id);

    if (!profile || profile.status !== "active") {
      return res.status(403).json({ error: "Active Academy profile required." });
    }

    req.profile = profile;
    next();
  } catch (error) {
    next(error);
  }
}

function requireRoles(...roles) {
  return [
    requireUser,
    requireRole,
    (req, res, next) => {
      if (!roles.includes(req.profile.role)) {
        return res.status(403).json({ error: "Insufficient permissions." });
      }
      next();
    }
  ];
}

app.get("/api/health", async (req, res, next) => {
  try {
    const configured = Boolean(adminClient);
    let database = "not-configured";

    if (adminClient) {
      const { error } = await adminClient.from("profiles").select("id").limit(1);
      database = error ? "error" : "connected";
    }

    res.json({
      ok: database !== "error",
      service: "ITech Academy API",
      phase: "Phase 2 — Production Backend",
      authentication: "Supabase Auth",
      database
    });
  } catch (error) {
    next(error);
  }
});

app.get("/api/me", requireUser, requireRole, (req, res) => {
  res.json({ user: req.authUser, profile: req.profile });
});

app.get("/api/courses", requireUser, requireRole, async (req, res, next) => {
  try {
    let query = adminClient
      .from("courses")
      .select("id,title,slug,description,level,status,teacher_id,created_at,updated_at")
      .order("created_at", { ascending: false });

    if (req.profile.role === "student") {
      query = query.eq("status", "published");
    }

    if (req.profile.role === "teacher") {
      query = query.eq("teacher_id", req.profile.id);
    }

    const { data, error } = await query;
    if (error) throw error;

    res.json({ courses: data || [] });
  } catch (error) {
    next(error);
  }
});

app.post("/api/courses", ...requireRoles("admin", "teacher"), async (req, res, next) => {
  try {
    const { title, slug, description = "", level = "Foundation" } = req.body || {};

    if (!title || !slug) {
      return res.status(400).json({ error: "title and slug are required." });
    }

    const teacherId = req.profile.role === "teacher" ? req.profile.id : req.body.teacher_id;

    if (!teacherId) {
      return res.status(400).json({ error: "teacher_id is required for an admin-created course." });
    }

    const { data, error } = await adminClient
      .from("courses")
      .insert({
        id: crypto.randomUUID(),
        title,
        slug,
        description,
        level,
        teacher_id: teacherId,
        status: "draft"
      })
      .select()
      .single();

    if (error) throw error;
    res.status(201).json({ course: data });
  } catch (error) {
    next(error);
  }
});

app.get("/api/enrollments/me", requireRoles("student"), async (req, res, next) => {
  try {
    const { data, error } = await adminClient
      .from("enrollments")
      .select("id,course_id,status,enrolled_at,completed_at,courses(id,title,slug,description,level,status)")
      .eq("student_id", req.profile.id)
      .order("enrolled_at", { ascending: false });

    if (error) throw error;
    res.json({ enrollments: data || [] });
  } catch (error) {
    next(error);
  }
});

app.get("/api/teacher/students", requireRoles("teacher", "admin"), async (req, res, next) => {
  try {
    let query = adminClient
      .from("enrollments")
      .select("student_id,status,enrolled_at,courses!inner(id,title,teacher_id),profiles!inner(id,full_name,email,status)")
      .order("enrolled_at", { ascending: false });

    if (req.profile.role === "teacher") {
      query = query.eq("courses.teacher_id", req.profile.id);
    }

    const { data, error } = await query;
    if (error) throw error;

    res.json({ enrollments: data || [] });
  } catch (error) {
    next(error);
  }
});

app.use((error, req, res, next) => {
  console.error(error);

  const status = Number(error.status) || 500;
  res.status(status).json({
    error: status === 500 ? "Internal server error." : error.message
  });
});

app.listen(PORT, () => {
  console.log(`ITech Academy API listening on http://localhost:${PORT}`);
});
