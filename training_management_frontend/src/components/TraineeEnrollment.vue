<template>
  <div class="trainee-enrollment">
    <div class="page-header">
      <h2>🎓 Trainee Enrollment Management</h2>
      <button @click="openAddModal" class="btn btn-add">
        ➕ Enroll Trainee
      </button>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editMode ? 'Edit Trainee' : 'Enroll New Trainee' }}</h3>
          <button @click="closeModal" class="modal-close">×</button>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>Trainee Name <span class="required">*</span></label>
              <input v-model="form.name" type="text" placeholder="Enter trainee name" required />
            </div>

            <div class="form-group">
              <label>Email <span class="required">*</span></label>
              <input 
                v-model="form.email" 
                type="email" 
                placeholder="Enter email (e.g., name@example.com)" 
                pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                title="Please enter a valid email address"
                required 
              />
              <small class="hint">Format: name@example.com</small>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Phone <span class="required">*</span></label>
              <input 
                v-model="form.phone" 
                type="tel" 
                placeholder="Enter 10-digit phone number"
                @input="validatePhone"
                maxlength="10"
                pattern="[0-9]{10}"
                title="Please enter exactly 10 digits"
                required 
              />
              <small class="hint">Only 10 digits allowed (e.g., 9876543210)</small>
            </div>

            <div class="form-group">
              <label>Select Batch <span class="required">*</span></label>
              <select v-model="form.batchId" required>
                <option value="">Select a Batch</option>
                <option v-for="batch in batches" :key="batch.id" :value="batch.id">
                  {{ batch.courseName }} - {{ batch.location }}
                </option>
              </select>
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Processing...' : (editMode ? 'Update' : 'Enroll') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div class="card">
      <h3>Enrolled Trainees</h3>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Batch</th>
              <th>Location</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="trainee in trainees" :key="trainee.id">
              <td>{{ trainee.id }}</td>
              <td>{{ trainee.name }}</td>
              <td>{{ trainee.email }}</td>
              <td>{{ trainee.phone }}</td>
              <td>{{ trainee.batchName }}</td>
              <td>{{ trainee.location }}</td>
              <td>
                <span class="badge badge-success">Enrolled</span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="editTrainee(trainee)" class="btn-icon btn-edit" title="Edit">
                    ✏️
                  </button>
                  <button @click="deleteTrainee(trainee.id)" class="btn-icon btn-delete" title="Delete">
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
  name: 'TraineeEnrollment',
  data() {
    return {
      showModal: false,
      form: {
        id: null,
        name: '',
        email: '',
        phone: '',
        batchId: ''
      },
      trainees: [],
      batches: [],
      loading: false,
      editMode: false
    }
  },
  mounted() {
    this.loadTrainees()
    this.loadBatches()
  },
  methods: {
    async loadTrainees() {
      try {
        const response = await api.getTrainees()
        this.trainees = response.data
      } catch (error) {
        showToast('Error loading trainees', 'error')
      }
    },

    async loadBatches() {
      try {
        const response = await api.getBatches()
        this.batches = response.data
      } catch (error) {
        showToast('Error loading batches', 'error')
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

    validatePhone(event) {
      // Remove any non-digit characters
      let value = event.target.value.replace(/\D/g, '')
      // Limit to 10 digits
      if (value.length > 10) {
        value = value.slice(0, 10)
      }
      this.form.phone = value
    },

    validateEmail(email) {
      const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
      return emailRegex.test(email)
    },

    async submitForm() {
      // Email validation
      if (!this.validateEmail(this.form.email)) {
        showToast('Please enter a valid email address', 'error')
        return
      }

      // Phone validation
      if (this.form.phone.length !== 10) {
        showToast('Phone number must be exactly 10 digits', 'error')
        return
      }

      if (!/^\d{10}$/.test(this.form.phone)) {
        showToast('Phone number must contain only digits', 'error')
        return
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateTrainee(this.form.id, this.form)
          showToast('Trainee updated successfully!', 'success')
        } else {
          await api.enrollTrainee(this.form)
          showToast('Trainee enrolled successfully!', 'success')
        }
        this.closeModal()
        this.loadTrainees()
      } catch (error) {
        showToast('Error: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },

    editTrainee(trainee) {
      this.editMode = true
      this.form = {
        id: trainee.id,
        name: trainee.name,
        email: trainee.email,
        phone: trainee.phone,
        batchId: trainee.batchId
      }
      this.showModal = true
    },

    async deleteTrainee(id) {
      if (!confirm('Are you sure you want to delete this trainee?')) {
        return
      }
      try {
        await api.deleteTrainee(id)
        showToast('Trainee deleted successfully!', 'success')
        this.loadTrainees()
      } catch (error) {
        showToast('Error deleting trainee', 'error')
      }
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        name: '',
        email: '',
        phone: '',
        batchId: ''
      }
    }
  }
}
</script>

<style scoped>
.trainee-enrollment {
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

.hint {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.8rem;
  color: #6c757d;
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