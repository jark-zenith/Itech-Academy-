const express = require("express");

function opsRoutes({ db, requireRoles }) {
  const router = express.Router();

  router.get("/metrics", requireRoles("admin"), async (req, res, next) => {
    try {
      const [{ count: users }, { count: courses }, { count: enrollments }, { count: aiUsage }] =
        await Promise.all([
          db.from("profiles").select("id", { count: "exact", head: true }),
          db.from("courses").select("id", { count: "exact", head: true }),
          db.from("enrollments").select("id", { count: "exact", head: true }),
          db.from("ai_usage_logs").select("id", { count: "exact", head: true })
        ]);

      res.json({
        users: users || 0,
        courses: courses || 0,
        enrollments: enrollments || 0,
        aiUsageEvents: aiUsage || 0,
        timestamp: new Date().toISOString()
      });
    } catch (error) { next(error); }
  });

  router.get("/audit", requireRoles("admin"), async (req, res, next) => {
    try {
      const limit = Math.min(100, Math.max(1, Number(req.query.limit || 50)));
      const { data, error } = await db.from("audit_logs")
        .select("*").order("created_at", { ascending: false }).limit(limit);
      if (error) throw error;
      res.json({ logs: data || [] });
    } catch (error) { next(error); }
  });

  return router;
}

module.exports = { opsRoutes };
