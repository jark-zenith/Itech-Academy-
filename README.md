# ITech Academy

**Don't Just Watch Technology. Build It.**

ITech Academy is the education platform concept of the JARK ecosystem: a practical, project-first technology academy where learners move from foundations to real-world building.

## Academy model

**Learn → Experiment → Build → Share**

Learning progression:

**Student → Course → Module → Lesson → Challenge → Project → Certificate**

Builder progression:

**Foundation → Builder → Engineer → Innovator**

## Learning tracks

- ICT Foundations
- Software Development
- AI & Intelligent Systems
- Cybersecurity
- Robotics & IoT
- Creative Technology
- Future engineering and entrepreneurship tracks

## Academy OS

The repository now contains:

- Public Academy landing page
- Admin Control Center
- Teacher workspace
- Student portal
- Role-aware navigation
- Course catalog and course builder
- Module and lesson creation
- Lesson editor foundation
- Student enrollment/progress workflow
- Assignment/submission foundation
- Project Lab foundation
- J.A.R.K AI Tutor interface
- Security Center
- Audit Log
- Production database schema blueprint

## Current prototype

The dashboard currently uses browser localStorage so the full workflow can be tested without a backend. This is deliberately a development/demo layer.

It is **not production authentication**. Real deployment must replace it with:

1. Trusted identity/authentication provider
2. Server-side authorization and role checks
3. Secure sessions/tokens
4. Real database
5. Row-level access policies
6. Protected API endpoints
7. Server-side J.A.R.K AI integration
8. Rate limiting and abuse protection
9. Backups and audit monitoring

See database/schema.sql for the PostgreSQL/Supabase-compatible data model foundation.

## J.A.R.K intelligent tutor

The tutor is designed to become more than a generic chatbot. With the production backend it should receive only the context required for a learning task, such as:

- enrolled course
- current module and lesson
- learner progress
- project/assignment context
- previous tutor conversation
- relevant Academy teaching material

The intended teaching loop is:

**Explain → Ask → Guide → Practice → Review → Build**

The tutor should help learners understand and build rather than simply dumping answers.

## Suggested production architecture

ITech Academy
├── Public Academy
├── Secure Authentication
├── Student Portal
│   ├── Courses
│   ├── Lessons
│   ├── Progress
│   ├── Projects
│   └── Certificates
├── Teacher Portal
│   ├── Course Builder
│   ├── Lessons
│   ├── Assignments
│   ├── Submissions
│   └── Learner Progress
├── Admin Control Center
│   ├── Users
│   ├── Courses
│   ├── Security
│   └── Audit Logs
├── Project Lab
├── J.A.R.K AI Tutor
└── Database + Secure API

## Local demo accounts

- Admin: admin@itech.academy
- Teacher: teacher@itech.academy
- Student: student@itech.academy

No passwords are stored by the prototype.

## Roadmap

### Phase 1 — Academy OS foundation
- [x] Public Academy interface
- [x] Admin dashboard
- [x] Teacher dashboard
- [x] Student dashboard
- [x] Course/module/lesson workflow
- [x] Progress workflow
- [x] Security center
- [x] Audit log
- [x] Database schema foundation

### Phase 2 — Production backend
- [ ] Real authentication
- [ ] Database connection
- [ ] Server-side RBAC
- [ ] RLS/access policies
- [ ] Real course persistence
- [ ] Teacher/student account lifecycle

### Phase 3 — Learning engine
- [ ] Rich lesson editor
- [ ] Video/resources
- [ ] Quizzes
- [ ] Coding labs
- [ ] Assignment grading
- [ ] Certificates and verification
- [ ] Student portfolio

### Phase 4 — J.A.R.K Tutor
- [ ] Secure model endpoint
- [ ] Course-context retrieval
- [ ] Tutor memory per learner
- [ ] Guided project assistance
- [ ] Code/debug support
- [ ] Teacher/admin controls
- [ ] Usage limits and monitoring

## Philosophy

ITech Academy is not meant to be another place where people only watch tutorials.

The goal is simple:

> Learn the technology. Understand the system. Build something real.

## AI Faculty

ITech Academy now includes a multi-agent learning design so students can access specialist teaching support without leaving the Academy.

### Faculty

- **J.A.R.K** — Lead AI Tutor and orchestrator
- **Claude Code Mentor** — programming, architecture, debugging and code review
- **ChatGPT Research Mentor** — concepts, research and structured problem solving
- **Gemini Innovation Mentor** — multimodal work, AI experiments and innovation
- **Cyber Mentor** — defensive cybersecurity, Linux and networking
- **Project Lab Coach** — practical builds and portfolio projects
- **Exam Coach** — quizzes, revision and study planning

The student portal provides an **AI Faculty** view with simple task routing. The admin portal provides an **AI Agents** control center for agent status and course access metadata.

### Production AI architecture

Student → J.A.R.K Orchestrator → AI Faculty Agent → Secure Provider Gateway → Model

External providers are model infrastructure, while ITech Academy owns the learner experience, curriculum context, permissions, progress and audit layer.

The current UI is a safe prototype: it does **not** make live provider calls and does not store API keys. Production implementation requires server-side provider credentials, authenticated sessions, authorization checks, rate limiting, usage controls, prompt/context boundaries, safety filtering and audit logging.

### Phase 5 — AI Faculty

- [x] Multi-agent registry foundation
- [x] Student AI Faculty interface
- [x] Admin AI Agents control center
- [x] Course-aware agent access metadata
- [x] Prototype task routing
- [ ] Secure provider gateway
- [ ] Real J.A.R.K orchestration
- [ ] Provider adapters for OpenAI, Anthropic and Google
- [ ] AI usage metering and limits
- [ ] Context/RAG retrieval from Academy lessons
- [ ] AI safety and moderation layer
