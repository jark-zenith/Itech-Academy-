const express = require("express");
const { generate, configured } = require("../lib/providers");

function chatRoutes({ db, requireRoles }) {
  const router = express.Router();

  router.post("/chat", requireRoles("student", "teacher", "admin"), async (req, res, next) => {
    const started = Date.now();
    try {
      const {
        provider = process.env.DEFAULT_AI_PROVIDER || "openai",
        model,
        course_id = null,
        lesson_id = null,
        messages = []
      } = req.body || {};

      if (!Array.isArray(messages) || messages.length === 0) {
        return res.status(400).json({ error: "messages must contain at least one message." });
      }

      if (!["openai", "anthropic", "google"].includes(provider)) {
        return res.status(400).json({ error: "Unsupported provider." });
      }

      if (!configured(provider)) {
        return res.status(503).json({ error: `Provider ${provider} is not configured.` });
      }

      // Context is fetched server-side so the browser cannot manufacture Academy context.
      let context = "";
      if (lesson_id) {
        const { data } = await db.from("lessons")
          .select("title,content,modules(title,courses(id,title,description,status))")
          .eq("id", lesson_id).maybeSingle();
        if (data) context += `Lesson: ${data.title}\nModule: ${data.modules?.title || ""}\nCourse: ${data.modules?.courses?.title || ""}\nMaterial:\n${String(data.content || "").slice(0, 12000)}`;
      } else if (course_id) {
        const { data } = await db.from("courses")
          .select("title,description,status").eq("id", course_id).maybeSingle();
        if (data) context += `Course: ${data.title}\nDescription: ${data.description || ""}\n`;
      }

      const system = [
        "You are J.A.R.K, the ITech Academy learning orchestrator.",
        "Teach through Explain → Ask → Guide → Practice → Review → Build.",
        "Do not invent Academy course material. If context is insufficient, say so.",
        "Prefer guidance and reasoning over simply completing graded work for the learner.",
        context ? `Academy context:\n${context}` : ""
      ].filter(Boolean).join("\n\n");

      const result = await generate(provider, {
        model,
        system,
        messages: messages.slice(-20),
        maxTokens: Math.min(2000, Math.max(200, Number(req.body.max_tokens || 1200)))
      });

      await db.from("ai_usage_logs").insert({
        id: crypto.randomUUID(),
        student_id: req.profile.role === "student" ? req.profile.id : null,
        course_id,
        action: "chat",
        input_tokens: result.usage.input_tokens,
        output_tokens: result.usage.output_tokens,
        latency_ms: Date.now() - started,
        status: "success",
        metadata: { provider, model: result.model, lesson_id }
      });

      res.json({
        message: { role: "assistant", content: result.text },
        provider,
        model: result.model,
        usage: result.usage
      });
    } catch (error) {
      try {
        await db.from("ai_usage_logs").insert({
          id: crypto.randomUUID(),
          student_id: req.profile?.role === "student" ? req.profile.id : null,
          action: "chat",
          latency_ms: Date.now() - started,
          status: "error",
          metadata: { error: error.message }
        });
      } catch (_) {}
      next(error);
    }
  });

  return router;
}

module.exports = { chatRoutes };
