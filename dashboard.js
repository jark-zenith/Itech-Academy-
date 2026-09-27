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
  var nav=state.role==="admin"?["overview","users","courses","security","audit"]:state.role==="teacher"?["overview","courses","students","assignments"]:["overview","learning","tutor","projects"];
  var buttons=nav.map(function(x){return '<button class="nav '+(state.view===x?"active":"")+'" onclick="panel(\\''+x+'\\')">'+label(x)+'</button>'}).join("");
  return '<header class="topbar"><a class="brand"><span class="brand-mark">I</span><span>ITech <b>Academy</b></span></a><div class="account"><span>'+esc(state.user.name)+' · '+state.role+'</span><button class="btn ghost" onclick="logout()">Sign out</button></div></header><main class="dash"><aside><div class="side-title">ACADEMY OS</div>'+buttons+'</aside><section id="content">'+view()+'</section></main>';
}
function label(x){return ({overview:"Overview",users:"Users & Accounts",courses:state.role==="teacher"?"My Courses":"Courses",security:"Security Center",audit:"Audit Log",students:"Students",assignments:"Assignments",learning:"My Learning",tutor:"J.A.R.K AI Tutor",projects:"Project Lab"})[x]||x}
function panel(type){state.view=type;render()}
function course(id){return state.data.courses.find(function(c){return c.id===id})}
function lessons(c){return c.modules.reduce(function(a,m){return a.concat(m.lessons)},[])}
function courseCount(c){return state.data.enrollments.filter(function(e){return e.courseId===c.id}).length}
function teacherCourses(){return state.data.courses.filter(function(c){return c.teacherId===state.user.id})}
function myEnrollments(){return state.data.enrollments.filter(function(e){return e.studentId===state.user.id})}
function publishedLessons(){return teacherCourses().reduce(function(n,c){return n+lessons(c).length},0)}
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
function tutorPanel(){return '<div class="eyebrow">J.A.R.K INTELLIGENT TUTOR</div><h1>Build with your <span>AI teacher.</span></h1><div class="panel chat"><div id="chat"><div class="bubble ai">Hello, builder. I can explain a concept, guide a project, review your approach or help you debug. The secure model endpoint will be connected here.</div></div><div class="composer"><input id="q" placeholder="Ask your tutor…" onkeydown="if(event.key===\\'Enter\\')askTutor()"><button class="btn primary" onclick="askTutor()">Ask →</button></div></div>'}
function askTutor(){var q=document.getElementById("q");if(!q||!q.value.trim())return;var text=q.value.trim();document.getElementById("chat").innerHTML+='<div class="bubble user">'+esc(text)+'</div><div class="bubble ai">Tutor mode is ready in the UI. In production, J.A.R.K will receive your course, lesson and progress context through a protected server endpoint and return a guided teaching response.</div>';q.value="";log("TUTOR_QUERY",text.slice(0,120))}
function projectsPanel(){
  var mine=state.data.submissions.filter(function(x){return x.studentId===state.user.id});
  var rows=mine.length?mine.map(function(x){return '<div class="row"><div><b>'+esc(x.title)+'</b><small>'+esc(x.status)+'</small></div></div>'}).join(""):"<p>No projects submitted yet. Complete a project lesson to start your portfolio.</p>";
  return '<div class="eyebrow">PROJECT LAB</div><h1>Build <span>something.</span></h1><div class="panel"><p>Project submissions become part of your learning portfolio.</p>'+rows+'</div>';
}
function securityPanel(){return '<div class="eyebrow">SECURITY CENTER</div><h1>Platform <span>Security.</span></h1><div class="security-grid"><div class="security-item ok"><b>✓</b><div><h3>Role-aware UI</h3><p>Admin, teacher and student interfaces are separated in this prototype.</p></div></div><div class="security-item ok"><b>✓</b><div><h3>Audit events</h3><p>Important demo actions are recorded locally for inspection.</p></div></div><div class="security-item warn"><b>!</b><div><h3>Real authentication</h3><p>Not connected. Production accounts require server-side identity, password hashing or OAuth, secure sessions and authorization checks.</p></div></div><div class="security-item warn"><b>!</b><div><h3>Database/API</h3><p>Not connected. Do not use localStorage as the source of truth for real users or permissions.</p></div></div><div class="security-item warn"><b>!</b><div><h3>AI endpoint</h3><p>Not connected. API keys must never be shipped to the browser.</p></div></div></div>'}
function auditPanel(){
  var rows=state.data.audit.map(function(x){return '<tr><td>'+new Date(x.time).toLocaleString()+'</td><td>'+esc(x.actor)+'</td><td>'+esc(x.action)+'</td><td>'+esc(x.detail)+'</td></tr>'}).join("");
  return '<div class="eyebrow">ADMIN</div><h1>Audit <span>Log.</span></h1><div class="panel table-wrap"><table><tr><th>Time</th><th>Actor</th><th>Action</th><th>Detail</th></tr>'+rows+'</table></div>';
}
load();render();
