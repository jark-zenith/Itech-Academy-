const express = require("express");
const { getPublicClient } = require("../lib/supabase");

const router = express.Router();

router.post("/signup", async (req, res, next) => {
  try {
    const client = getPublicClient();
    if (!client) return res.status(503).json({ error: "Authentication is not configured." });

    const { email, password, full_name, role = "student" } = req.body || {};
    if (!email || !password || !full_name) {
      return res.status(400).json({ error: "email, password and full_name are required." });
    }
    if (!["student", "teacher"].includes(role)) {
      return res.status(400).json({ error: "Self-registration is limited to student or teacher." });
    }

    const { data, error } = await client.auth.signUp({
      email,
      password,
      options: { data: { full_name, requested_role: role } }
    });
    if (error) throw error;

    res.status(201).json({
      user: data.user,
      session: data.session,
      message: data.session
        ? "Account created."
        : "Account created. Check your email if verification is enabled."
    });
  } catch (error) {
    next(error);
  }
});

router.post("/login", async (req, res, next) => {
  try {
    const client = getPublicClient();
    if (!client) return res.status(503).json({ error: "Authentication is not configured." });

    const { email, password } = req.body || {};
    if (!email || !password) return res.status(400).json({ error: "email and password are required." });

    const { data, error } = await client.auth.signInWithPassword({ email, password });
    if (error) throw error;

    res.json({ user: data.user, session: data.session });
  } catch (error) {
    next(error);
  }
});

router.post("/logout", async (req, res) => {
  // Supabase access tokens are stateless; the client should discard its session.
  res.json({ ok: true });
});

module.exports = router;
