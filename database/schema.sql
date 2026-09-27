-- ITech Academy production data model
-- PostgreSQL/Supabase-compatible blueprint.
-- This file is a schema foundation, not a live database connection.

create table if not exists profiles (
  id uuid primary key,
  full_name text not null,
  email text unique not null,
  role text not null check (role in ('admin','teacher','student')),
  status text not null default 'active' check (status in ('active','disabled','pending')),
  avatar_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists courses (
  id uuid primary key,
  title text not null,
  slug text unique not null,
  description text,
  level text,
  status text not null default 'draft' check (status in ('draft','published','archived')),
  teacher_id uuid references profiles(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists modules (
  id uuid primary key,
  course_id uuid not null references courses(id) on delete cascade,
  title text not null,
  position integer not null default 0
);

create table if not exists lessons (
  id uuid primary key,
  module_id uuid not null references modules(id) on delete cascade,
  title text not null,
  slug text not null,
  type text not null check (type in ('lesson','challenge','project','quiz')),
  content text,
  duration_minutes integer,
  position integer not null default 0,
  published boolean not null default false
);

create table if not exists enrollments (
  id uuid primary key,
  student_id uuid not null references profiles(id) on delete cascade,
  course_id uuid not null references courses(id) on delete cascade,
  status text not null default 'active' check (status in ('active','completed','paused')),
  enrolled_at timestamptz not null default now(),
  completed_at timestamptz,
  unique(student_id,course_id)
);

create table if not exists lesson_progress (
  id uuid primary key,
  student_id uuid not null references profiles(id) on delete cascade,
  lesson_id uuid not null references lessons(id) on delete cascade,
  status text not null default 'not_started' check (status in ('not_started','in_progress','completed')),
  progress integer not null default 0 check (progress between 0 and 100),
  started_at timestamptz,
  completed_at timestamptz,
  unique(student_id,lesson_id)
);

create table if not exists assignments (
  id uuid primary key,
  course_id uuid not null references courses(id) on delete cascade,
  lesson_id uuid references lessons(id) on delete set null,
  title text not null,
  instructions text,
  due_at timestamptz,
  max_score integer
);

create table if not exists submissions (
  id uuid primary key,
  assignment_id uuid not null references assignments(id) on delete cascade,
  student_id uuid not null references profiles(id) on delete cascade,
  content text,
  artifact_url text,
  status text not null default 'submitted' check (status in ('draft','submitted','reviewed','returned')),
  score integer,
  feedback text,
  submitted_at timestamptz,
  reviewed_at timestamptz
);

create table if not exists certificates (
  id uuid primary key,
  student_id uuid not null references profiles(id) on delete cascade,
  course_id uuid not null references courses(id) on delete cascade,
  certificate_number text unique not null,
  issued_at timestamptz not null default now(),
  verification_hash text
);

create table if not exists tutor_conversations (
  id uuid primary key,
  student_id uuid not null references profiles(id) on delete cascade,
  course_id uuid references courses(id) on delete set null,
  lesson_id uuid references lessons(id) on delete set null,
  title text,
  created_at timestamptz not null default now()
);

create table if not exists tutor_messages (
  id uuid primary key,
  conversation_id uuid not null references tutor_conversations(id) on delete cascade,
  sender text not null check (sender in ('student','tutor','system')),
  content text not null,
  created_at timestamptz not null default now()
);

create table if not exists audit_logs (
  id uuid primary key,
  actor_id uuid references profiles(id) on delete set null,
  action text not null,
  target_type text,
  target_id uuid,
  metadata jsonb,
  created_at timestamptz not null default now()
);

-- Production security notes:
-- 1. Identity/passwords should be handled by a trusted auth provider.
-- 2. Never store raw passwords in this schema.
-- 3. Enforce role authorization server-side/RLS; never trust browser role values.
-- 4. Keep model/API keys on the server, never in client JavaScript.
-- 5. Add RLS policies before exposing these tables to a browser client.

-- AI Faculty / multi-agent learning layer
create table if not exists ai_agents (
  id uuid primary key,
  name text not null,
  provider text not null,
  model text not null,
  role text not null,
  description text,
  status text not null default 'active' check (status in ('active','disabled')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create table if not exists ai_agent_course_access (
  agent_id uuid not null references ai_agents(id) on delete cascade,
  course_id uuid not null references courses(id) on delete cascade,
  enabled boolean not null default true,
  primary key (agent_id, course_id)
);
create table if not exists ai_routing_rules (
  id uuid primary key,
  name text not null,
  trigger_keywords text[] not null default '{}',
  agent_id uuid not null references ai_agents(id) on delete cascade,
  priority integer not null default 100,
  enabled boolean not null default true,
  created_at timestamptz not null default now()
);
create table if not exists ai_usage_logs (
  id uuid primary key,
  student_id uuid references profiles(id) on delete set null,
  agent_id uuid references ai_agents(id) on delete set null,
  course_id uuid references courses(id) on delete set null,
  action text not null,
  input_tokens integer,
  output_tokens integer,
  latency_ms integer,
  status text not null default 'success' check (status in ('success','blocked','error')),
  metadata jsonb,
  created_at timestamptz not null default now()
);
-- AI Faculty production security: provider credentials/API keys are server-side secrets only;
-- students invoke only active agents assigned to enrolled courses; apply RLS before browser access.
