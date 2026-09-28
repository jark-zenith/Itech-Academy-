const express = require("express");

function aiRoutes({ db, requireRoles }) {
  const router = express.Router();

  const providers = {
    openai: process.env.OPENAI_API_KEY,
    anthropic: process.env.ANTHROPIC_API_KEY,
    google: process.env.GOOGLE_AI_API_KEY
  };

  router.get("/faculty", requireRoles("student", "teacher", "admin"), async (req, res, next) => {
    try {
      let query = db.from("ai_agents").select("id,name,provider,model,role,description,status").eq("status","active");
      const { data, error } = await query.order("name");
      if (error) throw error;
      res.json({ agents: data || [] });
    } catch (error) { next(error); }
  });

  router.post("/route", requireRoles("student", "teacher", "admin"), async (req, res, next) => {
    try {
      const task = String(req.body.task || "").trim();
      const courseId = req.body.course_id || null;
      if (!task) return res.status(400).json({ error: "task is required." });

      const { data: rules, error } = await db.from("ai_routing_rules")
        .select("name,trigger_keywords,agent_id,priority,enabled,ai_agents(id,name,provider,model,role,status)")
        .eq("enabled", true).order("priority");
      if (error) throw error;

      const normalized = task.toLowerCase();
      let selected = null;
      for (const rule of rules || []) {
        if ((rule.trigger_keywords || []).some(k => normalized.includes(String(k).toLowerCase()))) {
          selected = rule.ai_agents;
          break;
        }
      }
      selected = selected || { id: "jark", name: "J.A.R.K", provider: "internal", model: "orchestrator", role: "Lead AI Tutor", status: "active" };

      const configured = Boolean(providers[String(selected.provider || "").toLowerCase()]);
      await db.from("ai_usage_logs").insert({
        id: crypto.randomUUID(),
        student_id: req.profile.role === "student" ? req.profile.id : null,
        agent_id: selected.id,
        course_id: courseId,
        action: "route",
        status: configured ? "success" : "blocked",
        metadata: { configured, task_preview: task.slice(0, 240) }
      });

      res.json({
        agent: selected,
        providerConfigured: configured,
        liveModelCall: false,
        message: configured
          ? "Provider credentials are available; a provider adapter can now execute this route."
          : "Provider credentials are not configured. Routing is ready, but live generation remains disabled."
      });
    } catch (error) { next(error); }
  });

  return router;
}

module.exports = { aiRoutes };
