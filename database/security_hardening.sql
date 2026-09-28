-- ITech Academy security hardening
-- Quiz answer keys are server-only. Students must use the protected API.

drop policy if exists quiz_questions_read_available on quiz_questions;
drop policy if exists quiz_options_read_available on quiz_options;

-- Prevent direct browser reads of answer-key tables.
-- The server uses the Supabase service role for grading, after authentication/RBAC.
alter table quiz_questions enable row level security;
alter table quiz_options enable row level security;

create policy quiz_questions_admin_teacher_read on quiz_questions
for select using (
  public.is_admin()
  or exists (
    select 1
    from quizzes q
    join lessons l on l.id = q.lesson_id
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where q.id = quiz_id and c.teacher_id = auth.uid()
  )
);

create policy quiz_options_admin_teacher_read on quiz_options
for select using (
  public.is_admin()
  or exists (
    select 1
    from quiz_questions qq
    join quizzes q on q.id = qq.quiz_id
    join lessons l on l.id = q.lesson_id
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where qq.id = question_id and c.teacher_id = auth.uid()
  )
);
