<template>
  <div class="course-management">
    <h2>📖 Course Management</h2>

    <div class="card">
      <h3>{{ editMode ? 'Edit Course' : 'Add New Course' }}</h3>
      <form @submit.prevent="submitForm">
        <div class="form-row">
          <div class="form-group">
            <label>Course Name:</label>
            <input v-model="form.courseName" type="text" placeholder="Enter course name" required />
          </div>

          <div class="form-group">
            <label>Category:</label>
            <select v-model="form.category" required>
              <option value="">Select Category</option>
              <option value="Programming">Programming</option>
              <option value="Web Development">Web Development</option>
              <option value="Data Science">Data Science</option>
              <option value="DevOps">DevOps</option>
              <option value="Mobile Development">Mobile Development</option>
              <option value="Cloud Computing">Cloud Computing</option>
              <option value="Database">Database</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Duration (Weeks):</label>
            <input v-model.number="form.durationWeeks" type="number" min="1" placeholder="Enter duration in weeks" required />
          </div>

          <div class="form-group">
            <label>Description:</label>
            <input v-model="form.description" type="text" placeholder="Enter course description" />
          </div>
        </div>

        <div class="button-group">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Processing...' : (editMode ? 'Update Course' : 'Add Course') }}
          </button>
          <button v-if="editMode" type="button" class="btn btn-secondary" @click="cancelEdit">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <div class="card" v-if="courses.length > 0">
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
                <span class="badge badge-info">{{ course.category }}</span>
              </td>
              <td>{{ course.durationWeeks }} weeks</td>
              <td>{{ course.description || 'N/A' }}</td>
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

    <div v-else class="card">
      <div class="no-data">
        <p>No courses available</p>
        <p>Add a new course using the form above</p>
      </div>
    </div>

    <div v-if="message" :class="['alert', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'CourseManagement',
  data() {
    return {
      form: {
        id: null,
        courseName: '',
        category: '',
        durationWeeks: null,
        description: ''
      },
      courses: [],
      message: '',
      messageType: 'success',
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
        console.error('Error loading courses:', error)
        this.showMessage('Error loading courses', 'error')
      }
    },

    async submitForm() {
      this.loading = true
      try {
        if (this.editMode) {
          await api.updateCourse(this.form.id, this.form)
          this.showMessage('✅ Course updated successfully!', 'success')
        } else {
          await api.createCourse(this.form)
          this.showMessage('✅ Course added successfully!', 'success')
        }
        
        this.loadCourses()
        this.resetForm()
      } catch (error) {
        this.showMessage('❌ Error: ' + (error.response?.data?.message || error.message), 'error')
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
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    async deleteCourse(id) {
      if (!confirm('Are you sure you want to delete this course?')) {
        return
      }

      try {
        await api.deleteCourse(id)
        this.showMessage('✅ Course deleted successfully!', 'success')
        this.loadCourses()
      } catch (error) {
        this.showMessage('❌ Error deleting course', 'error')
      }
    },

    cancelEdit() {
      this.resetForm()
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

    showMessage(msg, type) {
      this.message = msg
      this.messageType = type
      setTimeout(() => {
        this.message = ''
      }, 5000)
    }
  }
}
</script>

<style scoped>
.course-management {
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  color: #2c3e50;
  margin-bottom: 2rem;
  font-size: 2rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.table-responsive {
  overflow-x: auto;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
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

.badge-info {
  background: #17a2b8;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
}

.no-data {
  text-align: center;
  padding: 2rem;
  color: #666;
}

@media (max-width: 768px) {
  h2 {
    font-size: 1.5rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>