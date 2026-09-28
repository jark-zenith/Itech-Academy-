const express = require("express");
const helmet = require("helmet");
const morgan = require("morgan");
const { getServerClient } = require("./lib/supabase");
const { rateLimit } = require("./lib/rateLimit");
const { authRoutes } = { authRoutes: require("./routes/auth") };
const { learningRoutes } = require("./routes/learning");
const { aiRoutes } = require("./routes/ai");
const { chatRoutes } = require("./routes/chat");
const { opsRoutes } = require("./routes/ops");
const { requestId } = require("./requestId");
const { phase3Routes } = require("./routes/phase3");

const app = express();
const PORT = Number(process.env.PORT || 3000);
const isProduction = process.env.NODE_ENV === "production";
const db = getServerClient();

app.disable("x-powered-by");
app.use(requestId);
app.use(helmet());
app.use(express.json({ limit: "1mb" }));
app.use(morgan(isProduction ? "combined" : "dev"));
app.use(rateLimit({ windowMs: 60_000, max: 120 }));

async function getProfile(userId) {
  if (!db) throw Object.assign(new Error("Database is not configured."), { status: 503 });
  const { data, error } = await db.from("profiles")
    .select("id,full_name,email,role,status,avatar_url")
    .eq("id", userId).maybeSingle();
  if (error) throw error;
  return data;
}

async function requireUser(req, res, next) {
  try {
    if (!db) return res.status(503).json({ error: "Database is not configured." });
    const header = req.headers.authorization || "";
    const token = header.startsWith("Bearer ") ? header.slice(7) : null;
    if (!token) return res.status(401).json({ error: "Missing Bearer token." });

    const { data, error } = await db.auth.getUser(token);
    if (error || !data.user) return res.status(401).json({ error: "Invalid or expired session." });

    req.authUser = data.user;
    next();
  } catch (error) { next(error); }
}

async function requireRole(req, res, next) {
  try {
    const profile = await getProfile(req.authUser.id);
    if (!profile || profile.status !== "active") {
      return res.status(403).json({ error: "Active Academy profile required." });
    }
    req.profile = profile;
    next();
  } catch (error) { next(error); }
}

function requireRoles(...roles) {
  return [
    requireUser,
    requireRole,
    (req, res, next) => roles.includes(req.profile.role)
      ? next()
      : res.status(403).json({ error: "Insufficient permissions." })
  ];
}

app.get("/api/health", async (req, res) => {
  let database = "not-configured";
  if (db) {
    const { error } = await db.from("profiles").select("id").limit(1);
    database = error ? "error" : "connected";
  }
  res.status(database === "error" ? 503 : 200).json({
    ok: database !== "error",
    service: "ITech Academy API",
    phase: "Production Backend",
    database,
    environment: process.env.NODE_ENV || "development",
    timestamp: new Date().toISOString()
  });
});

app.get("/api/ready", async (req, res) => {
  const ready = Boolean(db);
  res.status(ready ? 200 : 503).json({ ready, database: ready ? "configured" : "missing" });
});

app.use("/api/auth", authRoutes);
app.use("/api/learning", learningRoutes({ db, requireRoles }));
app.use("/api/learning", phase3Routes({ db, requireRoles }));
app.use("/api/ai", aiRoutes({ db, requireRoles }));
app.use("/api/ai", chatRoutes({ db, requireRoles }));
app.use("/api/ops", opsRoutes({ db, requireRoles }));

app.get("/api/me", ...requireRoles("student", "teacher", "admin"), (req, res) => {
  res.json({ user: req.authUser, profile: req.profile });
});

app.use((error, req, res, next) => {
  console.error("[ITech Academy]", error);
  const status = Number(error.status) || 500;
  res.status(status).json({
    error: status === 500 ? "Internal server error." : error.message,
    code: error.code || undefined
  });
});

const server = app.listen(PORT, () => {
  console.log(`ITech Academy API listening on http://localhost:${PORT}`);
});

function shutdown(signal) {
  console.log(`${signal}: shutting down ITech Academy API`);
  server.close(() => process.exit(0));
  setTimeout(() => process.exit(1), 10_000).unref();
}

process.on("SIGTERM", () => shutdown("SIGTERM"));
process.on("SIGINT", () => shutdown("SIGINT"));
