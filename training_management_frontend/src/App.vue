<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <h1 class="logo">📚 Training Management System</h1>
        <nav class="nav-menu">
          <router-link to="/" class="nav-link">Home</router-link>
          <router-link to="/trainer-availability" class="nav-link">Trainer Availability</router-link>
          <router-link to="/trainee-enrollment" class="nav-link">Trainee Enrollment</router-link>
          <router-link to="/attendance" class="nav-link">Mark Attendance</router-link>
          <router-link to="/batches" class="nav-link">Batches</router-link>
          <router-link to="/courses" class="nav-link">Courses</router-link>
          <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="app-footer">
      <p>&copy; 2025 Training Management System</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem 2rem;
}

.logo {
  color: #2c3e50;
  font-size: 1.8rem;
  margin-bottom: 1rem;
  text-align: center;
}

.nav-menu {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.nav-link {
  color: #2c3e50;
  text-decoration: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  transition: all 0.3s;
  font-weight: 500;
  background: transparent;
}

.nav-link:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.nav-link.router-link-active {
  background: #667eea;
  color: white;
}

.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
}

.app-footer {
  background: rgba(44, 62, 80, 0.9);
  color: white;
  text-align: center;
  padding: 1.5rem;
  margin-top: 2rem;
}

/* Global Card Styles */
.card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.card h3 {
  color: #667eea;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
}

/* Form Styles */
.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 600;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

/* Button Styles */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
}

/* Table Styles */
.data-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

.data-table thead {
  background: #667eea;
  color: white;
}

.data-table th,
.data-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

/* Badge Styles */
.badge {
  display: inline-block;
  padding: 0.35rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
}

.badge-success {
  background: #d4edda;
  color: #155724;
}

.badge-danger {
  background: #f8d7da;
  color: #721c24;
}

.badge-warning {
  background: #fff3cd;
  color: #856404;
}

.badge-info {
  background: #d1ecf1;
  color: #0c5460;
}

/* Alert Styles */
.alert {
  padding: 1rem 1.5rem;
  border-radius: 8px;
  margin-top: 1rem;
  animation: slideIn 0.3s ease-out;
}

.alert.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

@keyframes slideIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .header-content {
    padding: 1rem;
  }

  .logo {
    font-size: 1.3rem;
  }

  .nav-menu {
    flex-direction: column;
    gap: 0.5rem;
  }

  .nav-link {
    text-align: center;
    padding: 0.75rem;
  }

  .main-content {
    padding: 1rem;
  }

  .card {
    padding: 1.5rem;
  }

  .data-table {
    font-size: 0.9rem;
  }

  .data-table th,
  .data-table td {
    padding: 0.5rem;
  }
}

@media (max-width: 480px) {
  .logo {
    font-size: 1.1rem;
  }

  .data-table {
    font-size: 0.8rem;
  }
}
</style>