-- ITech Academy Phase 2 — Row Level Security
-- Run this AFTER database/schema.sql in the Supabase SQL editor.
-- Supabase Auth user IDs must match profiles.id.

alter table profiles enable row level security;
alter table courses enable row level security;
alter table modules enable row level security;
alter table lessons enable row level security;
alter table enrollments enable row level security;
alter table lesson_progress enable row level security;
alter table assignments enable row level security;
alter table submissions enable row level security;
alter table certificates enable row level security;
alter table tutor_conversations enable row level security;
alter table tutor_messages enable row level security;
alter table audit_logs enable row level security;
alter table ai_agents enable row level security;
alter table ai_agent_course_access enable row level security;
alter table ai_routing_rules enable row level security;
alter table ai_usage_logs enable row level security;

create or replace function public.current_academy_role()
returns text
language sql
stable
security definer
set search_path = public
as $$
  select role from public.profiles
  where id = auth.uid() and status = 'active'
  limit 1;
$$;

create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select public.current_academy_role() = 'admin';
$$;

create or replace function public.is_teacher()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select public.current_academy_role() = 'teacher';
$$;

-- Profiles
create policy profiles_self_read on profiles
for select using (id = auth.uid() or public.is_admin());

create policy profiles_self_update on profiles
for update using (id = auth.uid() or public.is_admin())
with check (id = auth.uid() or public.is_admin());

create policy profiles_admin_insert on profiles
for insert with check (public.is_admin());

-- Published course visibility for learners; owners/admins can manage courses.
create policy courses_student_read_published on courses
for select using (status = 'published' and public.current_academy_role() = 'student');

create policy courses_teacher_read_own on courses
for select using (teacher_id = auth.uid() or public.is_admin());

create policy courses_teacher_insert on courses
for insert with check (
  public.is_admin()
  or (public.is_teacher() and teacher_id = auth.uid())
);

create policy courses_teacher_update_own on courses
for update using (teacher_id = auth.uid() or public.is_admin())
with check (teacher_id = auth.uid() or public.is_admin());

create policy courses_admin_delete on courses
for delete using (public.is_admin());

-- Modules and lessons follow the course access boundary.
create policy modules_read_enrolled_or_owner on modules
for select using (
  exists (
    select 1 from courses c
    where c.id = modules.course_id
      and (
        c.status = 'published'
        or c.teacher_id = auth.uid()
        or public.is_admin()
      )
  )
);

create policy modules_teacher_insert on modules
for insert with check (
  public.is_admin()
  or exists (select 1 from courses c where c.id = course_id and c.teacher_id = auth.uid())
);

create policy modules_teacher_update on modules
for update using (
  public.is_admin()
  or exists (select 1 from courses c where c.id = course_id and c.teacher_id = auth.uid())
) with check (
  public.is_admin()
  or exists (select 1 from courses c where c.id = course_id and c.teacher_id = auth.uid())
);

create policy modules_teacher_delete on modules
for delete using (
  public.is_admin()
  or exists (select 1 from courses c where c.id = course_id and c.teacher_id = auth.uid())
);

create policy lessons_read_available on lessons
for select using (
  exists (
    select 1 from modules m
    join courses c on c.id = m.course_id
    where m.id = lessons.module_id
      and (
        (c.status = 'published' and lessons.published = true)
        or c.teacher_id = auth.uid()
        or public.is_admin()
      )
  )
);

create policy lessons_teacher_insert on lessons
for insert with check (
  public.is_admin()
  or exists (
    select 1 from modules m
    join courses c on c.id = m.course_id
    where m.id = module_id and c.teacher_id = auth.uid()
  )
);

create policy lessons_teacher_update on lessons
for update using (
  public.is_admin()
  or exists (
    select 1 from modules m
    join courses c on c.id = m.course_id
    where m.id = module_id and c.teacher_id = auth.uid()
  )
) with check (
  public.is_admin()
  or exists (
    select 1 from modules m
    join courses c on c.id = m.course_id
    where m.id = module_id and c.teacher_id = auth.uid()
  )
);

-- Students can only see and manage their own enrollments.
create policy enrollments_student_read on enrollments
for select using (student_id = auth.uid() or public.is_admin());

create policy enrollments_teacher_read on enrollments
for select using (
  public.is_admin()
  or exists (
    select 1 from courses c
    where c.id = course_id and c.teacher_id = auth.uid()
  )
);

create policy enrollments_student_insert on enrollments
for insert with check (student_id = auth.uid());

-- Progress is strictly owned by the learner; teachers can read progress for their courses.
create policy progress_student_all on lesson_progress
for all using (student_id = auth.uid() or public.is_admin())
with check (student_id = auth.uid() or public.is_admin());

create policy progress_teacher_read on lesson_progress
for select using (
  public.is_admin()
  or exists (
    select 1
    from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id and c.teacher_id = auth.uid()
  )
);

-- Students own their submissions; teachers can read/update submissions for their courses.
create policy submissions_student_all on submissions
for all using (student_id = auth.uid() or public.is_admin())
with check (student_id = auth.uid() or public.is_admin());

create policy submissions_teacher_read_update on submissions
for select using (
  public.is_admin()
  or exists (
    select 1
    from assignments a
    join courses c on c.id = a.course_id
    where a.id = assignment_id and c.teacher_id = auth.uid()
  )
);

-- AI faculty: students can read active agents and course access, while admins manage them.
create policy ai_agents_active_read on ai_agents
for select using (status = 'active' or public.is_admin());

create policy ai_agents_admin_write on ai_agents
for all using (public.is_admin()) with check (public.is_admin());

create policy ai_access_read on ai_agent_course_access
for select using (
  public.is_admin()
  or exists (
    select 1 from enrollments e
    where e.student_id = auth.uid()
      and e.course_id = ai_agent_course_access.course_id
      and e.status = 'active'
  )
);

create policy ai_access_admin_write on ai_agent_course_access
for all using (public.is_admin()) with check (public.is_admin());

create policy ai_usage_student_read on ai_usage_logs
for select using (student_id = auth.uid() or public.is_admin());

create policy ai_usage_admin_write on ai_usage_logs
for all using (public.is_admin()) with check (public.is_admin());

-- Audit logs are not writable directly by students/teachers.
create policy audit_admin_read on audit_logs
for select using (public.is_admin());

-- Security reminder:
-- The service-role key bypasses RLS. The backend must enforce authentication and RBAC
-- before using privileged operations. Never expose SUPABASE_SERVICE_ROLE_KEY to the browser.
