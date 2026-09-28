const express = require("express");

function learningRoutes({ db, requireRoles }) {
  const router = express.Router();

  router.get("/lessons/:id", requireRoles("student", "teacher", "admin"), async (req, res, next) => {
    try {
      const { data, error } = await db.from("lessons")
        .select("id,title,slug,type,content,duration_minutes,position,published,module_id,modules(id,title,course_id,courses(id,title,slug,status,teacher_id))")
        .eq("id", req.params.id)
        .maybeSingle();
      if (error) throw error;
      if (!data) return res.status(404).json({ error: "Lesson not found." });

      const course = data.modules?.courses;
      if (req.profile.role === "student" && (!course || course.status !== "published" || !data.published)) {
        return res.status(403).json({ error: "Lesson is not available." });
      }

      res.json({ lesson: data });
    } catch (error) { next(error); }
  });

  router.get("/courses/:courseId/resources", requireRoles("student", "teacher", "admin"), async (req, res, next) => {
    try {
      const { data, error } = await db.from("lesson_resources")
        .select("id,lesson_id,title,resource_type,url,position,created_at,lessons!inner(id,title,module_id,modules!inner(course_id))")
        .eq("lessons.modules.course_id", req.params.courseId)
        .order("position");
      if (error) throw error;
      res.json({ resources: data || [] });
    } catch (error) { next(error); }
  });

  router.post("/lessons/:lessonId/progress", requireRoles("student"), async (req, res, next) => {
    try {
      const progress = Math.max(0, Math.min(100, Number(req.body.progress || 0)));
      const status = progress >= 100 ? "completed" : progress > 0 ? "in_progress" : "not_started";
      const payload = {
        id: crypto.randomUUID(),
        student_id: req.profile.id,
        lesson_id: req.params.lessonId,
        status,
        progress,
        started_at: progress > 0 ? new Date().toISOString() : null,
        completed_at: progress >= 100 ? new Date().toISOString() : null
      };

      const { data, error } = await db.from("lesson_progress")
        .upsert(payload, { onConflict: "student_id,lesson_id" })
        .select().single();
      if (error) throw error;

      res.status(201).json({ progress: data });
    } catch (error) { next(error); }
  });

  router.post("/courses/:courseId/enroll", requireRoles("student"), async (req, res, next) => {
    try {
      const { data, error } = await db.from("enrollments")
        .upsert({
          id: crypto.randomUUID(),
          student_id: req.profile.id,
          course_id: req.params.courseId,
          status: "active"
        }, { onConflict: "student_id,course_id" })
        .select().single();
      if (error) throw error;
      res.status(201).json({ enrollment: data });
    } catch (error) { next(error); }
  });

  router.get("/quizzes/:id", requireRoles("student", "teacher", "admin"), async (req, res, next) => {
    try {
      const { data, error } = await db.from("quizzes")
        .select("id,lesson_id,title,passing_score,attempts_allowed,quiz_questions(id,question,position,points,quiz_options(id,option_text,position))")
        .eq("id", req.params.id).maybeSingle();
      if (error) throw error;
      if (!data) return res.status(404).json({ error: "Quiz not found." });
      if (req.profile.role === "student") {
        data.quiz_questions = (data.quiz_questions || []).map(q => ({
          ...q,
          quiz_options: (q.quiz_options || []).map(({ is_correct, ...option }) => option)
        }));
      }
      res.json({ quiz: data });
    } catch (error) { next(error); }
  });

  router.post("/quizzes/:id/attempts", requireRoles("student"), async (req, res, next) => {
    try {
      const { answers = {}, score = 0 } = req.body || {};
      const safeScore = Math.max(0, Math.min(100, Number(score)));
      const { data, error } = await db.from("quiz_attempts")
        .insert({
          id: crypto.randomUUID(),
          quiz_id: req.params.id,
          student_id: req.profile.id,
          score: safeScore,
          passed: safeScore >= 70,
          answers,
          submitted_at: new Date().toISOString()
        }).select().single();
      if (error) throw error;
      res.status(201).json({ attempt: data });
    } catch (error) { next(error); }
  });

  router.get("/portfolio", requireRoles("student"), async (req, res, next) => {
    try {
      const { data, error } = await db.from("portfolio_projects")
        .select("*").eq("student_id", req.profile.id).order("created_at", { ascending: false });
      if (error) throw error;
      res.json({ projects: data || [] });
    } catch (error) { next(error); }
  });

  router.post("/portfolio", requireRoles("student"), async (req, res, next) => {
    try {
      const { title, description = "", course_id = null, repository_url = null, live_url = null } = req.body || {};
      if (!title) return res.status(400).json({ error: "title is required." });
      const { data, error } = await db.from("portfolio_projects")
        .insert({
          id: crypto.randomUUID(),
          student_id: req.profile.id, course_id, title, description,
          repository_url, live_url, status: "draft"
        }).select().single();
      if (error) throw error;
      res.status(201).json({ project: data });
    } catch (error) { next(error); }
  });

  return router;
}

module.exports = { learningRoutes };
