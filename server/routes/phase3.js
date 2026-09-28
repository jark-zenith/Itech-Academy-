const express = require("express");

function phase3Routes({ db, requireRoles }) {
  const router = express.Router();

  router.post("/quizzes/:id/submit", requireRoles("student"), async (req, res, next) => {
    try {
      const answers = req.body?.answers || {};
      const { data: quiz, error } = await db.from("quizzes")
        .select("id,passing_score,attempts_allowed,quiz_questions(id,points,quiz_options(id,is_correct))")
        .eq("id", req.params.id).maybeSingle();
      if (error) throw error;
      if (!quiz) return res.status(404).json({ error: "Quiz not found." });

      let total = 0;
      let earned = 0;
      for (const q of quiz.quiz_questions || []) {
        const points = Number(q.points || 1);
        total += points;
        const correct = (q.quiz_options || []).find(o => o.is_correct);
        const answer = answers[q.id];
        if (correct && String(answer) === String(correct.id)) earned += points;
      }

      const score = total ? Math.round((earned / total) * 100) : 0;
      const passed = score >= quiz.passing_score;

      const { data: attempt, error: attemptError } = await db.from("quiz_attempts")
        .insert({
          id: crypto.randomUUID(),
          quiz_id: quiz.id,
          student_id: req.profile.id,
          score,
          passed,
          answers,
          submitted_at: new Date().toISOString()
        }).select("id,quiz_id,score,passed,submitted_at").single();
      if (attemptError) throw attemptError;

      res.status(201).json({ attempt });
    } catch (error) { next(error); }
  });

  router.post("/submissions/:id/grade", requireRoles("teacher", "admin"), async (req, res, next) => {
    try {
      const score = Math.max(0, Number(req.body?.score || 0));
      const feedback = String(req.body?.feedback || "");

      const { data: submission, error } = await db.from("submissions")
        .select("id,assignment_id,student_id,assignments!inner(course_id,courses!inner(teacher_id))")
        .eq("id", req.params.id).maybeSingle();
      if (error) throw error;
      if (!submission) return res.status(404).json({ error: "Submission not found." });

      const owner = submission.assignments?.courses?.teacher_id;
      if (req.profile.role === "teacher" && owner !== req.profile.id) {
        return res.status(403).json({ error: "You may only grade submissions for your courses." });
      }

      const { data, error: updateError } = await db.from("submissions")
        .update({
          score,
          feedback,
          status: "reviewed",
          reviewed_at: new Date().toISOString()
        })
        .eq("id", req.params.id).select().single();
      if (updateError) throw updateError;

      res.json({ submission: data });
    } catch (error) { next(error); }
  });

  router.get("/certificates/verify/:code", async (req, res, next) => {
    try {
      const { data, error } = await db.from("certificate_verifications")
        .select("public_code,verification_hash,verified_at,certificates(certificate_number,issued_at,course_id,courses(title),student_id)")
        .eq("public_code", req.params.code).maybeSingle();
      if (error) throw error;
      if (!data) return res.status(404).json({ verified: false });
      res.json({
        verified: true,
        certificate: {
          number: data.certificates?.certificate_number,
          issued_at: data.certificates?.issued_at,
          course: data.certificates?.courses?.title
        }
      });
    } catch (error) { next(error); }
  });

  router.post("/certificates/issue", requireRoles("admin"), async (req, res, next) => {
    try {
      const { student_id, course_id, certificate_number, public_code, verification_hash } = req.body || {};
      if (!student_id || !course_id || !certificate_number || !public_code || !verification_hash) {
        return res.status(400).json({ error: "student_id, course_id, certificate_number, public_code and verification_hash are required." });
      }

      const { data: certificate, error } = await db.from("certificates")
        .insert({
          id: crypto.randomUUID(),
          student_id, course_id, certificate_number, verification_hash
        }).select().single();
      if (error) throw error;

      const { data: verification, error: verifyError } = await db.from("certificate_verifications")
        .insert({
          id: crypto.randomUUID(),
          certificate_id: certificate.id,
          public_code,
          verification_hash
        }).select().single();
      if (verifyError) throw verifyError;

      res.status(201).json({ certificate, verification });
    } catch (error) { next(error); }
  });

  return router;
}

module.exports = { phase3Routes };
