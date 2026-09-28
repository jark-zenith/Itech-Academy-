-- ITech Academy Phase 3 — Learning Engine foundation
-- Run after database/schema.sql and database/rls.sql.
-- This adds structured lesson content, resources, quizzes, coding labs,
-- portfolio artifacts, and certificate verification data.

create table if not exists lesson_resources (
  id uuid primary key,
  lesson_id uuid not null references lessons(id) on delete cascade,
  title text not null,
  resource_type text not null check (resource_type in ('video','pdf','link','image','file')),
  url text not null,
  position integer not null default 0,
  created_at timestamptz not null default now()
);

create table if not exists quizzes (
  id uuid primary key,
  lesson_id uuid not null references lessons(id) on delete cascade,
  title text not null,
  passing_score integer not null default 70 check (passing_score between 0 and 100),
  attempts_allowed integer not null default 3,
  created_at timestamptz not null default now()
);

create table if not exists quiz_questions (
  id uuid primary key,
  quiz_id uuid not null references quizzes(id) on delete cascade,
  question text not null,
  position integer not null default 0,
  points integer not null default 1
);

create table if not exists quiz_options (
  id uuid primary key,
  question_id uuid not null references quiz_questions(id) on delete cascade,
  option_text text not null,
  is_correct boolean not null default false,
  position integer not null default 0
);

create table if not exists quiz_attempts (
  id uuid primary key,
  quiz_id uuid not null references quizzes(id) on delete cascade,
  student_id uuid not null references profiles(id) on delete cascade,
  score integer not null default 0 check (score between 0 and 100),
  passed boolean not null default false,
  answers jsonb not null default '{}',
  started_at timestamptz not null default now(),
  submitted_at timestamptz
);

create table if not exists coding_labs (
  id uuid primary key,
  lesson_id uuid not null references lessons(id) on delete cascade,
  title text not null,
  instructions text,
  language text not null default 'javascript',
  starter_code text,
  test_code text,
  created_at timestamptz not null default now()
);

create table if not exists portfolio_projects (
  id uuid primary key,
  student_id uuid not null references profiles(id) on delete cascade,
  course_id uuid references courses(id) on delete set null,
  title text not null,
  description text,
  repository_url text,
  live_url text,
  status text not null default 'draft' check (status in ('draft','submitted','published')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists certificate_verifications (
  id uuid primary key,
  certificate_id uuid unique not null references certificates(id) on delete cascade,
  public_code text unique not null,
  verification_hash text not null,
  verified_at timestamptz
);

alter table lesson_resources enable row level security;
alter table quizzes enable row level security;
alter table quiz_questions enable row level security;
alter table quiz_options enable row level security;
alter table quiz_attempts enable row level security;
alter table coding_labs enable row level security;
alter table portfolio_projects enable row level security;
alter table certificate_verifications enable row level security;

create policy resources_read_available on lesson_resources
for select using (
  exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id
      and (
        (c.status = 'published' and l.published = true)
        or c.teacher_id = auth.uid()
        or public.is_admin()
      )
  )
);

create policy resources_teacher_write on lesson_resources
for all using (
  public.is_admin()
  or exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id and c.teacher_id = auth.uid()
  )
) with check (
  public.is_admin()
  or exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id and c.teacher_id = auth.uid()
  )
);

create policy quizzes_read_available on quizzes
for select using (
  exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id
      and (
        (c.status = 'published' and l.published = true)
        or c.teacher_id = auth.uid()
        or public.is_admin()
      )
  )
);

create policy quiz_questions_read_available on quiz_questions
for select using (
  exists (
    select 1 from quizzes q
    where q.id = quiz_id
  )
);

create policy quiz_options_read_available on quiz_options
for select using (
  exists (
    select 1 from quiz_questions qq
    where qq.id = question_id
  )
);

create policy quiz_attempts_student_all on quiz_attempts
for all using (student_id = auth.uid() or public.is_admin())
with check (student_id = auth.uid() or public.is_admin());

create policy coding_labs_read_available on coding_labs
for select using (
  exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id
      and (
        (c.status = 'published' and l.published = true)
        or c.teacher_id = auth.uid()
        or public.is_admin()
      )
  )
);

create policy coding_labs_teacher_write on coding_labs
for all using (
  public.is_admin()
  or exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id and c.teacher_id = auth.uid()
  )
) with check (
  public.is_admin()
  or exists (
    select 1 from lessons l
    join modules m on m.id = l.module_id
    join courses c on c.id = m.course_id
    where l.id = lesson_id and c.teacher_id = auth.uid()
  )
);

create policy portfolio_owner_all on portfolio_projects
for all using (student_id = auth.uid() or public.is_admin())
with check (student_id = auth.uid() or public.is_admin());

create policy certificate_public_verification on certificate_verifications
for select using (true);

create policy certificate_admin_write on certificate_verifications
for all using (public.is_admin()) with check (public.is_admin());
