-- ITech Academy Phase 6 — production operations foundation

create table if not exists api_usage_daily (
  usage_date date not null default current_date,
  user_id uuid references profiles(id) on delete cascade,
  route text not null,
  request_count integer not null default 0,
  primary key (usage_date, user_id, route)
);

create table if not exists system_incidents (
  id uuid primary key,
  severity text not null check (severity in ('info','warning','critical')),
  service text not null,
  message text not null,
  metadata jsonb,
  created_at timestamptz not null default now(),
  resolved_at timestamptz
);

alter table api_usage_daily enable row level security;
alter table system_incidents enable row level security;

create policy api_usage_admin_read on api_usage_daily
for select using (public.is_admin());

create policy incidents_admin_all on system_incidents
for all using (public.is_admin()) with check (public.is_admin());

create index if not exists courses_teacher_idx on courses(teacher_id);
create index if not exists enrollments_student_idx on enrollments(student_id);
create index if not exists enrollments_course_idx on enrollments(course_id);
create index if not exists lesson_progress_student_idx on lesson_progress(student_id);
create index if not exists ai_usage_student_idx on ai_usage_logs(student_id);
create index if not exists audit_logs_created_idx on audit_logs(created_at desc);
