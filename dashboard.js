const KEY="itech_academy_v2";
const state={user:null,role:null,data:null,view:"overview"};

function seedData(){
  return {
    users:[
      {id:"admin-1",name:"Academy Admin",email:"admin@itech.academy",role:"admin",status:"active"},
      {id:"teacher-1",name:"Demo Teacher",email:"teacher@itech.academy",role:"teacher",status:"active"},
      {id:"student-1",name:"Demo Student",email:"student@itech.academy",role:"student",status:"active"}
    ],
    courses:[
      {id:"web-1",title:"Web Development",description:"Build responsive websites and complete web applications.",teacherId:"teacher-1",status:"published",level:"Builder",modules:[
        {id:"web-m1",title:"HTML & CSS Foundations",lessons:[
          {id:"web-l1",title:"How the Web Works",type:"lesson",duration:"12 min"},
          {id:"web-l2",title:"Build Your First Page",type:"challenge",duration:"25 min"}]},
        {id:"web-m2",title:"JavaScript Essentials",lessons:[
          {id:"web-l3",title:"Variables and Logic",type:"lesson",duration:"18 min"},
          {id:"web-l4",title:"Interactive Web Challenge",type:"project",duration:"40 min"}]}
      ]},
      {id:"ai-1",title:"AI & Intelligent Systems",description:"Understand AI systems and build useful assistants.",teacherId:"teacher-1",status:"published",level:"Engineer",modules:[
        {id:"ai-m1",title:"AI Foundations",lessons:[
          {id:"ai-l1",title:"What Makes a System Intelligent?",type:"lesson",duration:"15 min"},
          {id:"ai-l2",title:"Prompting as System Design",type:"challenge",duration:"20 min"}]}
      ]},
      {id:"cyber-1",title:"Cybersecurity Foundations",description:"Learn defensive security, Linux and safe system practices.",teacherId:"teacher-1",status:"draft",level:"Foundation",modules:[
        {id:"cy-m1",title:"Security Basics",lessons:[
          {id:"cy-l1",title:"Accounts, Permissions and Threats",type:"lesson",duration:"20 min"}]}
      ]}
    ],
    enrollments:[
      {id:"enr-1",studentId:"student-1",courseId:"web-1",progress:38,status:"active"},
      {id:"enr-2",studentId:"student-1",courseId:"ai-1",progress:12,status:"active"}
    ],
    submissions:[],
    aiAgents:[
      {id:"jark",name:"J.A.R.K",provider:"ITech Academy",model:"Orchestrator",role:"Lead AI Tutor",description:"Coordinates the Academy AI Faculty, teaches concepts, tracks learning context and routes tasks.",status:"active",courses:["*"]},
      {id:"claude-code",name:"Claude Code Mentor",provider:"Anthropic",model:"Claude",role:"Code Mentor",description:"Programming, architecture, debugging, code review and software engineering guidance.",status:"active",courses:["web-1","ai-1"]},
      {id:"chatgpt-research",name:"ChatGPT Research Mentor",provider:"OpenAI",model:"ChatGPT",role:"Research Mentor",description:"Concept explanations, structured research, problem solving and source-aware learning support.",status:"active",courses:["*"]},
      {id:"gemini-innovation",name:"Gemini Innovation Mentor",provider:"Google",model:"Gemini",role:"Innovation Mentor",description:"Multimodal learning, AI experiments, brainstorming and creative technical exploration.",status:"active",courses:["ai-1"]},
      {id:"cyber",name:"Cyber Mentor",provider:"ITech Academy",model:"Security Engine",role:"Cybersecurity Mentor",description:"Defensive cybersecurity, Linux, networking, permissions and safe security labs.",status:"active",courses:["cyber-1"]},
      {id:"lab",name:"Project Lab Coach",provider:"ITech Academy",model:"Project Coach",role:"Project Coach",description:"Turns lessons into practical builds, challenges, milestones and portfolio projects.",status:"active",courses:["*"]},
      {id:"exam",name:"Exam Coach",provider:"ITech Academy",model:"Study Engine",role:"Study Coach",description:"Creates revision plans, quizzes and practice questions from Academy learning material.",status:"active",courses:["*"]}
    ],
    aiUsage:[],
    audit:[{id:"log-1",action:"SYSTEM_BOOT",actor:"system",detail:"Academy OS initialized",time:new Date().toISOString()}]
  };
}
function load(){
  var raw=localStorage.getItem(KEY);
  if(raw){try{state.data=JSON.parse(raw)}catch(e){state.data=seedData()}}else state.data=seedData();
  save();
}
function save(){localStorage.setItem(KEY,JSON.stringify(state.data))}
function esc(v){return String(v==null?"":v).replace(/[&<>"']/g,function(m){return {"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#039;"}[m]})}
function log(action,detail){
  state.data.audit.unshift({id:crypto.randomUUID(),action:action,actor:state.user?state.user.email:"system",detail:detail,time:new Date().toISOString()});
  state.data.audit=state.data.audit.slice(0,100);save();
}
function login(email,role){
  var u=state.data.users.find(function(x){return x.email.toLowerCase()===email.trim().toLowerCase()&&x.role===role&&x.status==="active"});
  if(!u){alert("Active account not found. Use a demo account or ask an admin to create one.");return}
  state.user=u;state.role=role;state.view="overview";log("LOGIN","Signed in as "+role);render();
}
function logout(){log("LOGOUT","User signed out");state.user=null;state.role=null;render()}
function render(){document.body.innerHTML=state.user?dashboard():loginScreen()}
function loginScreen(){
  return '<div class="auth"><div class="auth-card"><div class="brand"><span class="brand-mark">I</span><span>ITech <b>Academy</b></span></div><span class="eyebrow">SECURE ACADEMY ACCESS</span><h1>Welcome, <span>builder.</span></h1><p>Choose your role and enter the Academy OS.</p><label>Email</label><input id="email" value="admin@itech.academy" autocomplete="email"><label>Role</label><select id="role"><option value="admin">Admin</option><option value="teacher">Teacher</option><option value="student">Student</option></select><button class="btn primary" onclick="login(document.getElementById(\\'email\\').value,document.getElementById(\\'role\\').value)">Sign in →</button><div class="demo"><b>Demo accounts</b><br>admin@itech.academy · teacher@itech.academy · student@itech.academy<br><br>This prototype intentionally does not collect passwords.</div></div></div>';
}
function dashboard(){
  var nav=state.role==="admin"?["overview","users","courses","ai-agents","security","audit"]:state.role==="teacher"?["overview","courses","students","assignments"]:["overview","learning","ai-faculty","tutor","projects"];
  var buttons=nav.map(function(x){return '<button class="nav '+(state.view===x?"active":"")+'" onclick="panel(\\''+x+'\\')">'+label(x)+'</button>'}).join("");
  return '<header class="topbar"><a class="brand"><span class="brand-mark">I</span><span>ITech <b>Academy</b></span></a><div class="account"><span>'+esc(state.user.name)+' · '+state.role+'</span><button class="btn ghost" onclick="logout()">Sign out</button></div></header><main class="dash"><aside><div class="side-title">ACADEMY OS</div>'+buttons+'</aside><section id="content">'+view()+'</section></main>';
}
function label(x){return ({overview:"Overview",users:"Users & Accounts",courses:state.role==="teacher"?"My Courses":"Courses","ai-agents":"AI Agents",security:"Security Center",audit:"Audit Log",students:"Students",assignments:"Assignments",learning:"My Learning","ai-faculty":"AI Faculty",tutor:"J.A.R.K AI Tutor",projects:"Project Lab"})[x]||x}
function panel(type){state.view=type;render()}
function course(id){return state.data.courses.find(function(c){return c.id===id})}
function lessons(c){return c.modules.reduce(function(a,m){return a.concat(m.lessons)},[])}
function courseCount(c){return state.data.enrollments.filter(function(e){return e.courseId===c.id}).length}
function teacherCourses(){return state.data.courses.filter(function(c){return c.teacherId===state.user.id})}
function myEnrollments(){return state.data.enrollments.filter(function(e){return e.studentId===state.user.id})}
function publishedLessons(){return teacherCourses().reduce(function(n,c){return n+lessons(c).length},0)}

function activeAgents(){return state.data.aiAgents.filter(function(a){return a.status==="active"})}
function agent(id){return state.data.aiAgents.find(function(a){return a.id===id})}
function agentAllowed(a,cid){return a.status==="active"&&(a.courses.indexOf("*")>=0||a.courses.indexOf(cid)>=0)}
function recommendAgent(q,cid){
  var s=q.toLowerCase();
  var id=s.match(/\b(debug|bug|javascript|python|code|program|api|database|sql|flutter|laravel|react)\b/)?"claude-code":
    s.match(/\b(research|source|paper|explain|theory|concept|why)\b/)?"chatgpt-research":
    s.match(/\b(image|vision|multimodal|experiment|brainstorm|innovation|ai)\b/)?"gemini-innovation":
    s.match(/\b(cyber|security|linux|network|permission|vulnerability)\b/)?"cyber":
    s.match(/\b(project|build|portfolio|challenge|prototype)\b/)?"lab":
    s.match(/\b(exam|quiz|revision|test|study)\b/)?"exam":"jark";
  var a=agent(id)||agent("jark");if(cid&&!agentAllowed(a,cid))a=agent("jark");return a;
}
function aiAgentsPanel(){
  var rows=state.data.aiAgents.map(function(a){
    var access=a.courses.indexOf("*")>=0?"All courses":a.courses.map(function(id){var c=course(id);return c?c.title:id}).join(", ");
    return '<div class="agent-card"><div class="agent-icon">AI</div><div class="agent-main"><div class="agent-top"><span class="pill">'+esc(a.provider)+'</span><span class="agent-status '+(a.status==="active"?"live":"off")+'">'+esc(a.status)+'</span></div><h3>'+esc(a.name)+'</h3><p>'+esc(a.description)+'</p><small><b>'+esc(a.role)+'</b> · '+esc(a.model)+' · Access: '+esc(access)+'</small></div><button class="btn ghost" onclick="toggleAgent(\''+a.id+'\')">'+(a.status==="active"?"Disable":"Enable")+'</button></div>';
  }).join("");
  return '<div class="eyebrow">ADMIN · AI FACULTY CONTROL</div><div class="title-row"><div><h1>AI <span>Agents.</span></h1><p class="muted">Control the learning agents available to students. J.A.R.K remains the orchestration layer.</p></div><div class="ai-count">'+activeAgents().length+' active</div></div><div class="panel"><h2>Provider policy</h2><p>Provider/model credentials are server-side configuration only. This prototype stores agent metadata locally; it never stores API keys in the browser.</p></div><div class="agent-list">'+rows+'</div>';
}
function toggleAgent(id){
  var a=agent(id);if(!a)return;
  if(a.id==="jark"&&a.status==="active"){alert("J.A.R.K is the Academy orchestrator and cannot be disabled in this prototype.");return}
  a.status=a.status==="active"?"disabled":"active";save();log("AI_AGENT_STATUS",a.name+" → "+a.status);panel("ai-agents");
}
function aiFacultyPanel(){
  var enrolled=myEnrollments(),cid=enrolled.length?enrolled[0].courseId:null;
  var cards=activeAgents().filter(function(a){return agentAllowed(a,cid)}).map(function(a){
    return '<button class="agent-card student-agent" onclick="selectAgent(\''+a.id+'\')"><div class="agent-icon">AI</div><div class="agent-main"><div class="agent-top"><span class="pill">'+esc(a.provider)+'</span><span class="agent-status live">READY</span></div><h3>'+esc(a.name)+'</h3><p>'+esc(a.description)+'</p><small>'+esc(a.role)+'</small></div><span class="agent-arrow">→</span></button>';
  }).join("");
  return '<div class="eyebrow">STUDENT · AI FACULTY</div><h1>Your <span>AI Faculty.</span></h1><p class="muted">Different specialists, one Academy experience. Ask a question and J.A.R.K can route it to the appropriate faculty member.</p><div class="panel route-panel"><span class="eyebrow">SMART ROUTING</span><h2>What are you working on?</h2><input id="facultyQuestion" placeholder="e.g. Debug my JavaScript login system…"><select id="facultyCourse">'+(enrolled.length?enrolled.map(function(e){var c=course(e.courseId);return '<option value="'+esc(c.id)+'">'+esc(c.title)+'</option>'}).join(""):'<option value="">General Academy learning</option>')+'</select><button class="btn primary" onclick="routeFaculty()">Find my AI mentor →</button><div id="routeResult"></div></div><h2>Available faculty</h2><div class="agent-list">'+(cards||'<div class="panel"><p>No active AI agents are available for your enrolled courses.</p></div>')+'</div>';
}
function routeFaculty(){
  var q=document.getElementById("facultyQuestion"),sel=document.getElementById("facultyCourse");if(!q||!q.value.trim())return;
  var a=recommendAgent(q.value,sel?sel.value:null);
  state.data.aiUsage.push({id:crypto.randomUUID(),agentId:a.id,studentId:state.user.id,action:"route",query:q.value.slice(0,240),time:new Date().toISOString()});
  state.data.aiUsage=state.data.aiUsage.slice(-200);save();log("AI_ROUTE",a.name+" selected for student task");
  document.getElementById("routeResult").innerHTML='<div class="route-result"><span class="agent-icon">AI</span><div><b>Recommended mentor: '+esc(a.name)+'</b><p>'+esc(a.description)+'</p><button class="btn primary" onclick="openAgentTutor(\''+a.id+'\')">Open mentor →</button></div></div>';
}
function selectAgent(id){openAgentTutor(id)}
function openAgentTutor(id){
  var a=agent(id);if(!a)return;state.view="tutor";render();
  setTimeout(function(){var chat=document.getElementById("chat"),q=document.getElementById("q");if(chat)chat.innerHTML='<div class="bubble ai"><b>'+esc(a.name)+'</b><br>I am your '+esc(a.role)+'. I will work with your Academy context and guide you step by step. Secure provider integration is required before live model responses are enabled.</div>';if(q)q.placeholder="Ask "+a.name+"…"},0);
}
function overview(){
  if(state.role==="admin")return '<div class="eyebrow">ADMIN CONTROL CENTER</div><h1>Academy <span>Dashboard.</span></h1><p class="muted">Manage people, learning and platform operations.</p><div class="cards"><div><b>'+state.data.users.length+'</b><small>Accounts</small></div><div><b>'+state.data.users.filter(function(x){return x.role==="teacher"}).length+'</b><small>Teachers</small></div><div><b>'+state.data.users.filter(function(x){return x.role==="student"}).length+'</b><small>Students</small></div><div><b>'+state.data.courses.length+'</b><small>Courses</small></div></div><div class="panel"><h2>Academy health</h2><div class="health-grid"><span>Account directory <b>READY</b></span><span>Course catalog <b>READY</b></span><span>Learning progress <b>READY</b></span><span>Audit logging <b>READY</b></span><span>Real authentication <b class="warn">NOT CONNECTED</b></span><span>AI backend <b class="warn">NOT CONNECTED</b></span></div></div>';
  if(state.role==="teacher")return '<div class="eyebrow">TEACHER WORKSPACE</div><h1>Teach. <span>Build.</span> Inspire.</h1><div class="cards"><div><b>'+teacherCourses().length+'</b><small>My courses</small></div><div><b>'+state.data.enrollments.length+'</b><small>Active enrollments</small></div><div><b>'+state.data.submissions.length+'</b><small>Submissions</small></div><div><b>'+publishedLessons()+'</b><small>Lessons</small></div></div><div class="panel"><h2>Teaching workflow</h2><p>Create structured courses, add lessons and challenges, review learner progress and prepare assignments.</p><button class="btn primary" onclick="panel(\\'courses\\')">Open course manager →</button></div>';
  var avg=state.data.enrollments.length?Math.round(state.data.enrollments.reduce(function(a,x){return a+x.progress},0)/state.data.enrollments.length):0;
  return '<div class="eyebrow">STUDENT PORTAL</div><h1>Keep <span>building.</span></h1><div class="cards"><div><b>'+myEnrollments().length+'</b><small>Courses</small></div><div><b>'+avg+'%</b><small>Average progress</small></div><div><b>'+state.data.submissions.filter(function(x){return x.studentId===state.user.id}).length+'</b><small>Projects submitted</small></div><div><b>0</b><small>Certificates</small></div></div><div class="panel"><h2>J.A.R.K AI Tutor</h2><p>Your tutor interface is connected to course context in the Academy UI and ready for a secure model endpoint.</p><button class="btn primary" onclick="panel(\\'tutor\\')">Open AI Tutor →</button></div>';
}
function coursesPanel(){
  var list=state.role==="teacher"?teacherCourses():state.data.courses;
  var create=(state.role==="admin"||state.role==="teacher")?'<button class="btn primary" onclick="createCourse()">+ New course</button>':"";
  var rows=list.map(function(c){return '<div class="course-row"><div><span class="pill">'+esc(c.status)+'</span><h3>'+esc(c.title)+'</h3><p>'+esc(c.description)+'</p><small>'+c.modules.length+' modules · '+lessons(c).length+' lessons · '+courseCount(c)+' learners · '+esc(c.level)+'</small></div><button class="btn ghost" onclick="manageCourse(\\''+c.id+'\\')">Manage →</button></div>'}).join("");
  return '<div class="eyebrow">'+(state.role==="admin"?"ACADEMY CATALOG":"TEACHING")+' MODULE</div><div class="title-row"><div><h1>Course <span>Manager.</span></h1><p class="muted">Build learning paths from modules, lessons and practical challenges.</p></div>'+create+'</div><div class="course-list">'+rows+'</div>';
}
function manageCourse(id){
  var c=course(id);if(!c)return;
  var mods=c.modules.map(function(m){
    var ls=m.lessons.map(function(l){return '<div class="lesson"><span>◈</span><div><b>'+esc(l.title)+'</b><small>'+esc(l.type)+' · '+esc(l.duration)+'</small></div><button class="text-btn" onclick="openLesson(\\''+c.id+'\\',\\''+m.id+'\\',\\''+l.id+'\\')">Open →</button></div>'}).join("");
    return '<div class="module panel"><div class="module-head"><div><span class="eyebrow">MODULE</span><h2>'+esc(m.title)+'</h2></div><button class="text-btn" onclick="addLesson(\\''+c.id+'\\',\\''+m.id+'\\')">+ Add lesson</button></div>'+ls+'</div>';
  }).join("");
  document.getElementById("content").innerHTML='<div class="eyebrow">COURSE BUILDER</div><div class="title-row"><div><button class="back" onclick="panel(\\'courses\\')">← Back</button><h1>'+esc(c.title)+'</h1><p class="muted">'+esc(c.description)+'</p></div><button class="btn primary" onclick="addModule(\\''+c.id+'\\')">+ Add module</button></div><div class="module-list">'+mods+'</div>';
}
function createCourse(){
  var title=prompt("Course title");if(!title)return;
  var description=prompt("Short course description")||"Practical project-first course.";
  state.data.courses.push({id:crypto.randomUUID(),title:title,description:description,teacherId:state.role==="teacher"?state.user.id:"teacher-1",status:"draft",level:"Foundation",modules:[]});
  save();log("COURSE_CREATE",title);panel("courses");
}
function addModule(cid){
  var title=prompt("Module title");if(!title)return;
  course(cid).modules.push({id:crypto.randomUUID(),title:title,lessons:[]});save();log("MODULE_CREATE",title);manageCourse(cid);
}
function addLesson(cid,mid){
  var title=prompt("Lesson / challenge title");if(!title)return;
  var type=prompt("Type: lesson, challenge or project","lesson");
  if(["lesson","challenge","project"].indexOf(type)===-1){alert("Use lesson, challenge or project.");return}
  course(cid).modules.find(function(x){return x.id===mid}).lessons.push({id:crypto.randomUUID(),title:title,type:type,duration:"20 min"});
  save();log("LESSON_CREATE",title);manageCourse(cid);
}
function openLesson(cid,mid,lid){
  var c=course(cid),m=c.modules.find(function(x){return x.id===mid}),l=m.lessons.find(function(x){return x.id===lid});
  document.getElementById("content").innerHTML='<button class="back" onclick="manageCourse(\\''+cid+'\\')">← Back to course</button><div class="eyebrow">LESSON EDITOR</div><h1>'+esc(l.title)+'</h1><div class="panel"><p>Lesson workspace foundation. A production editor can add rich text, video, files, code labs, quizzes and grading rules.</p><label>Lesson type</label><select id="lessonType"><option '+(l.type==="lesson"?"selected":"")+'>lesson</option><option '+(l.type==="challenge"?"selected":"")+'>challenge</option><option '+(l.type==="project"?"selected":"")+'>project</option></select><label>Duration</label><input id="lessonDuration" value="'+esc(l.duration)+'"><button class="btn primary save-lesson" onclick="saveLesson(\\''+cid+'\\',\\''+mid+'\\',\\''+lid+'\\')">Save lesson</button></div>';
}
function saveLesson(cid,mid,lid){
  var l=course(cid).modules.find(function(m){return m.id===mid}).lessons.find(function(x){return x.id===lid});
  l.type=document.getElementById("lessonType").value;l.duration=document.getElementById("lessonDuration").value||"20 min";
  save();log("LESSON_UPDATE",l.title);manageCourse(cid);
}
function usersPanel(){
  var rows=state.data.users.map(function(u){return '<tr><td><b>'+esc(u.name)+'</b></td><td>'+esc(u.email)+'</td><td>'+esc(u.role)+'</td><td><span class="pill">'+esc(u.status)+'</span></td><td>'+(u.role!=="admin"?'<button class="text-btn" onclick="toggleUser(\\''+u.id+'\\')">'+(u.status==="active"?"Disable":"Activate")+'</button>':"—")+'</td></tr>'}).join("");
  return '<div class="eyebrow">ADMIN</div><div class="title-row"><div><h1>Users & <span>Accounts.</span></h1><p class="muted">Directory and role management.</p></div><button class="btn primary" onclick="createUser()">+ Create account</button></div><div class="panel table-wrap"><table><tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th></tr>'+rows+'</table></div>';
}
function createUser(){
  var name=prompt("Full name");if(!name)return;var email=prompt("Email");if(!email)return;var role=prompt("Role: teacher or student","student");
  if(["teacher","student"].indexOf(role)===-1){alert("Role must be teacher or student.");return}
  if(state.data.users.some(function(u){return u.email.toLowerCase()===email.toLowerCase()})){alert("That email already exists.");return}
  state.data.users.push({id:crypto.randomUUID(),name:name,email:email,role:role,status:"active"});save();log("ACCOUNT_CREATE",role+": "+email);panel("users");
}
function toggleUser(id){var u=state.data.users.find(function(x){return x.id===id});if(!u||u.role==="admin")return;u.status=u.status==="active"?"disabled":"active";save();log("ACCOUNT_STATUS",u.email+" → "+u.status);panel("users")}
function studentsPanel(){
  var students=state.data.users.filter(function(u){return u.role==="student"});
  var rows=students.map(function(s){var e=state.data.enrollments.filter(function(x){return x.studentId===s.id});var avg=e.length?Math.round(e.reduce(function(a,x){return a+x.progress},0)/e.length):0;return '<tr><td><b>'+esc(s.name)+'</b></td><td>'+esc(s.email)+'</td><td>'+e.length+'</td><td>'+avg+'%</td></tr>'}).join("");
  return '<div class="eyebrow">TEACHER</div><h1>Student <span>Roster.</span></h1><div class="panel table-wrap"><table><tr><th>Student</th><th>Email</th><th>Courses</th><th>Average</th></tr>'+rows+'</table></div>';
}
function assignmentsPanel(){return '<div class="eyebrow">TEACHER</div><h1>Assignments <span>& Submissions.</span></h1><div class="panel"><p>No submissions yet in the demo dataset.</p><button class="btn primary" onclick="createAssignment()">+ Create assignment</button></div>'}
function createAssignment(){var title=prompt("Assignment title");if(!title)return;state.data.submissions.push({id:crypto.randomUUID(),studentId:"student-1",title:title,status:"pending",createdAt:new Date().toISOString()});save();log("ASSIGNMENT_CREATE",title);panel("assignments")}
function learningPanel(){
  var cards=myEnrollments().map(function(e){var c=course(e.courseId);return '<div class="panel"><span class="pill">'+esc(c.level)+'</span><h2>'+esc(c.title)+'</h2><p>'+esc(c.description)+'</p><div class="progress"><span style="width:'+e.progress+'%"></span></div><small>'+e.progress+'% complete · '+lessons(c).length+' learning activities</small><button class="btn primary full" onclick="startCourse(\\''+c.id+'\\')">Continue →</button></div>'}).join("");
  return '<div class="eyebrow">STUDENT LEARNING</div><h1>My <span>Learning.</span></h1><div class="learning-grid">'+cards+'</div>';
}
function startCourse(cid){
  var c=course(cid);
  var modules=c.modules.map(function(m){var ls=m.lessons.map(function(l){return '<button class="lesson clickable" onclick="completeLesson(\\''+cid+'\\',\\''+l.id+'\\')"><span>◈</span><div><b>'+esc(l.title)+'</b><small>'+esc(l.type)+' · '+esc(l.duration)+'</small></div><span>→</span></button>'}).join("");return '<div class="module"><h2>'+esc(m.title)+'</h2>'+ls+'</div>'}).join("");
  document.getElementById("content").innerHTML='<button class="back" onclick="panel(\\'learning\\')">← My Learning</button><div class="eyebrow">LEARNING PATH</div><h1>'+esc(c.title)+'</h1><div class="panel"><p>'+esc(c.description)+'</p>'+modules+'</div>';
}
function completeLesson(cid,lid){
  var e=state.data.enrollments.find(function(x){return x.studentId===state.user.id&&x.courseId===cid}),c=course(cid),total=Math.max(1,lessons(c).length);
  e.progress=Math.min(100,e.progress+Math.max(5,Math.round(62/total)));save();log("LESSON_COMPLETE",c.title+": "+lid);alert("Progress saved. Keep building!");startCourse(cid);
}
function tutorPanel(){var selected=state.data.aiAgents.find(function(a){return a.id==="jark"})||state.data.aiAgents[0];return '<div class="eyebrow">AI FACULTY · '+esc(selected.role.toUpperCase())+'</div><h1>Build with your <span>'+esc(selected.name)+'.</span></h1><div class="panel chat"><div id="chat"><div class="bubble ai">Hello, builder. I can explain a concept, guide a project, review your approach or help you debug. The secure model endpoint will be connected here.</div></div><div class="composer"><input id="q" placeholder="Ask your tutor…" onkeydown="if(event.key===\\'Enter\\')askTutor()"><button class="btn primary" onclick="askTutor()">Ask →</button></div></div>'}
function askTutor(){var q=document.getElementById("q");if(!q||!q.value.trim())return;var text=q.value.trim();document.getElementById("chat").innerHTML+='<div class="bubble user">'+esc(text)+'</div><div class="bubble ai">Tutor mode is ready in the UI. In production, J.A.R.K will receive your course, lesson and progress context through a protected server endpoint and return a guided teaching response.</div>';q.value="";log("TUTOR_QUERY",text.slice(0,120))}
function projectsPanel(){
  var mine=state.data.submissions.filter(function(x){return x.studentId===state.user.id});
  var rows=mine.length?mine.map(function(x){return '<div class="row"><div><b>'+esc(x.title)+'</b><small>'+esc(x.status)+'</small></div></div>'}).join(""):"<p>No projects submitted yet. Complete a project lesson to start your portfolio.</p>";
  return '<div class="eyebrow">PROJECT LAB</div><h1>Build <span>something.</span></h1><div class="panel"><p>Project submissions become part of your learning portfolio.</p>'+rows+'</div>';
}
function securityPanel(){return '<div class="eyebrow">SECURITY CENTER</div><h1>Platform <span>Security.</span></h1><div class="security-grid"><div class="security-item ok"><b>✓</b><div><h3>Role-aware UI</h3><p>Admin, teacher and student interfaces are separated in this prototype.</p></div></div><div class="security-item ok"><b>✓</b><div><h3>Audit events</h3><p>Important demo actions are recorded locally for inspection.</p></div></div><div class="security-item warn"><b>!</b><div><h3>Real authentication</h3><p>Not connected. Production accounts require server-side identity, password hashing or OAuth, secure sessions and authorization checks.</p></div></div><div class="security-item warn"><b>!</b><div><h3>Database/API</h3><p>Not connected. Do not use localStorage as the source of truth for real users or permissions.</p></div></div><div class="security-item warn"><b>!</b><div><h3>AI provider gateway</h3><p>Not connected. AI agent metadata is available in the prototype, but live provider calls must go through a protected server gateway with secret storage, rate limits, access checks and usage logging.</p></div></div><div class="security-item ok"><b>✓</b><div><h3>AI Faculty policy</h3><p>Agents are role-aware in the UI, can be assigned to courses, and route events are recorded for future server-side audit controls.</p></div></div></div>'}
function view(){
  var map={overview:overview,users:usersPanel,courses:coursesPanel,security:securityPanel,audit:auditPanel,students:studentsPanel,assignments:assignmentsPanel,learning:learningPanel,tutor:tutorPanel,projects:projectsPanel,"ai-agents":aiAgentsPanel,"ai-faculty":aiFacultyPanel};
  return (map[state.view]||overview)();
}
function auditPanel(){
  var rows=state.data.audit.map(function(x){return '<tr><td>'+new Date(x.time).toLocaleString()+'</td><td>'+esc(x.actor)+'</td><td>'+esc(x.action)+'</td><td>'+esc(x.detail)+'</td></tr>'}).join("");
  return '<div class="eyebrow">ADMIN</div><h1>Audit <span>Log.</span></h1><div class="panel table-wrap"><table><tr><th>Time</th><th>Actor</th><th>Action</th><th>Detail</th></tr>'+rows+'</table></div>';
}
load();render();
