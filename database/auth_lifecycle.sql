-- ITech Academy Phase 2 — Supabase Auth lifecycle
-- Run after schema.sql and rls.sql.
-- Auth users are the identity source. Profiles hold Academy authorization data.

create or replace function public.handle_new_academy_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
  requested_role text;
begin
  requested_role := coalesce(new.raw_user_meta_data->>'requested_role', 'student');

  if requested_role not in ('student','teacher') then
    requested_role := 'student';
  end if;

  insert into public.profiles (id, full_name, email, role, status)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'full_name', split_part(new.email, '@', 1)),
    new.email,
    requested_role,
    case when requested_role = 'teacher' then 'pending' else 'active' end
  )
  on conflict (id) do update
    set full_name = excluded.full_name,
        email = excluded.email,
        updated_at = now();

  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;

create trigger on_auth_user_created
after insert on auth.users
for each row execute procedure public.handle_new_academy_user();

-- Only an administrator should activate a pending teacher.
create or replace function public.approve_teacher(target_user uuid)
returns boolean
language plpgsql
security definer
set search_path = public
as $$
begin
  if public.current_academy_role() <> 'admin' then
    raise exception 'Administrator authorization required';
  end if;

  update public.profiles
  set status = 'active', role = 'teacher', updated_at = now()
  where id = target_user and role = 'teacher';

  return found;
end;
$$;
