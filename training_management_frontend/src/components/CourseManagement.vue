<template>
  <div class="course-management">
    <div class="page-header">
      <h2>📖 Course Management</h2>
      <button @click="openAddModal" class="btn btn-add">
        ➕ Add Course
      </button>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editMode ? 'Edit Course' : 'Add New Course' }}</h3>
          <button @click="closeModal" class="modal-close">×</button>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>Course Name <span class="required"> *</span></label>
              <input v-model="form.courseName" type="text" placeholder="Enter course name" required />
            </div>

            <div class="form-group">
              <label>Category <span class="required"> *</span></label>
              <select v-model="form.category" required>
                <option value="">Select Category</option>
                <option value="Programming">Programming</option>
                <option value="Web Development">Web Development</option>
                <option value="Data Science">Data Science</option>
                <option value="DevOps">DevOps</option>
                <option value="Mobile Development">Mobile Development</option>
                <option value="Cloud Computing">Cloud Computing</option>
                <option value="Database">Database</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Duration (Weeks) <span class="required"> *</span></label>
              <input v-model.number="form.durationWeeks" type="number" min="1" placeholder="Enter duration in weeks" required />
            </div>

            <div class="form-group">
              <label>Description <span class="required"> *</span></label>
              <input v-model="form.description" type="text" placeholder="Enter course description" required />
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Processing...' : (editMode ? 'Update' : 'Add') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div class="card">
      <h3>All Courses</h3>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Course Name</th>
              <th>Category</th>
              <th>Duration</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in courses" :key="course.id">
              <td>{{ course.id }}</td>
              <td><strong>{{ course.courseName }}</strong></td>
              <td>
                <span :class="['badge', getCategoryClass(course.category)]">
                  {{ course.category }}
                </span>
              </td>
              <td>{{ course.durationWeeks }} weeks</td>
              <td>{{ course.description }}</td>
              <td>
                <div class="action-buttons">
                  <button @click="editCourse(course)" class="btn-icon btn-edit" title="Edit">
                    ✏️
                  </button>
                  <button @click="deleteCourse(course.id)" class="btn-icon btn-delete" title="Delete">
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
import { showToast } from '../utils/toast'

export default {
  name: 'CourseManagement',
  data() {
    return {
      showModal: false,
      form: {
        id: null,
        courseName: '',
        category: '',
        durationWeeks: null,
        description: ''
      },
      courses: [],
      loading: false,
      editMode: false
    }
  },
  mounted() {
    this.loadCourses()
  },
  methods: {
    async loadCourses() {
      try {
        const response = await api.getCourses()
        this.courses = response.data || []
      } catch (error) {
        showToast('Error loading courses', 'error')
      }
    },

    openAddModal() {
      this.editMode = false
      this.resetForm()
      this.showModal = true
    },

    closeModal() {
      this.showModal = false
      this.resetForm()
    },

    async submitForm() {
      this.loading = true
      try {
        if (this.editMode) {
          await api.updateCourse(this.form.id, this.form)
          showToast('Course updated successfully!', 'success')
        } else {
          await api.createCourse(this.form)
          showToast('Course added successfully!', 'success')
        }
        this.closeModal()
        this.loadCourses()
      } catch (error) {
        showToast('Error: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },

    editCourse(course) {
      this.editMode = true
      this.form = {
        id: course.id,
        courseName: course.courseName,
        category: course.category,
        durationWeeks: course.durationWeeks,
        description: course.description
      }
      this.showModal = true
    },

    async deleteCourse(id) {
      if (!confirm('Are you sure you want to delete this course?')) {
        return
      }
      try {
        await api.deleteCourse(id)
        showToast('Course deleted successfully!', 'success')
        this.loadCourses()
      } catch (error) {
        showToast('Error deleting course', 'error')
      }
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        courseName: '',
        category: '',
        durationWeeks: null,
        description: ''
      }
    },

    getCategoryClass(category) {
      const classes = {
        'Programming': 'badge-primary',
        'Web Development': 'badge-info',
        'Data Science': 'badge-success',
        'DevOps': 'badge-warning',
        'Mobile Development': 'badge-danger',
        'Cloud Computing': 'badge-secondary',
        'Database': 'badge-dark'
      }
      return classes[category] || 'badge-info'
    }
  }
}
</script>

<style scoped>
.course-management {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h2 {
  color: #2c3e50;
  font-size: 2rem;
  margin: 0;
}

.btn-add {
  background: #28a745;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-add:hover {
  background: #218838;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(40, 167, 69, 0.4);
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 0;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e0e0e0;
  background: #667eea;
  border-radius: 12px 12px 0 0;
}

.modal-header h3 {
  margin: 0;
  color: white;
  font-size: 1.5rem;
}

.modal-close {
  background: none;
  border: none;
  font-size: 2rem;
  color: white;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.modal-close:hover {
  opacity: 0.8;
}

.modal-content form {
  padding: 2rem;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e0e0e0;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.required {
  color: red;
}

.table-responsive {
  overflow-x: auto;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: all 0.3s;
}

.btn-edit:hover {
  background: #e3f2fd;
}

.btn-delete:hover {
  background: #ffebee;
}

.badge-primary {
  background: #007bff;
  color: white;
}

.badge-info {
  background: #17a2b8;
  color: white;
}

.badge-success {
  background: #28a745;
  color: white;
}

.badge-warning {
  background: #ffc107;
  color: #212529;
}

.badge-danger {
  background: #dc3545;
  color: white;
}

.badge-secondary {
  background: #6c757d;
  color: white;
}

.badge-dark {
  background: #343a40;
  color: white;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
  }

  .page-header h2 {
    font-size: 1.5rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}
</style>