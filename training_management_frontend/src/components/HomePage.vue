<template>
  <div class="home">
    <div class="hero">
      <h1>Welcome to Training Management System</h1>
      <p>Manage trainers, trainees, batches, and attendance efficiently</p>
    </div>

    <div class="features">
      <div class="feature-card">
        <div class="icon">👨‍🏫</div>
        <h3>Trainer Management</h3>
        <p>Add availability and view assigned batches</p>
        <router-link to="/trainer-availability" class="btn">Manage</router-link>
      </div>

      <div class="feature-card">
        <div class="icon">👨‍🎓</div>
        <h3>Trainee Enrollment</h3>
        <p>Enroll trainees into batches</p>
        <router-link to="/trainee-enrollment" class="btn">Enroll</router-link>
      </div>

      <div class="feature-card">
        <div class="icon">✅</div>
        <h3>Attendance</h3>
        <p>Mark and track attendance</p>
        <router-link to="/attendance" class="btn">Mark</router-link>
      </div>

      <div class="feature-card">
        <div class="icon">📊</div>
        <h3>Dashboard</h3>
        <p>View reports and analytics</p>
        <router-link to="/dashboard" class="btn">View</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<style scoped>
.home {
  text-align: center;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 3rem 2rem;
  border-radius: 10px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  padding: 1rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #667eea;
  margin-bottom: 0.5rem;
}

.feature-card p {
  color: #666;
  margin-bottom: 1rem;
}

.btn {
  display: inline-block;
  background: #667eea;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  text-decoration: none;
  transition: background 0.3s;
}

.btn:hover {
  background: #5568d3;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 1.8rem;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}
</style>