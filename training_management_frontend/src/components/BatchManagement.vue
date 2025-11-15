<template>
  <div class="batch-management">
    <div class="page-header">
      <h2>📚 Batch Management</h2>
      <button @click="openAddModal" class="btn btn-add">
        ➕ Create Batch
      </button>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editMode ? 'Edit Batch' : 'Create New Batch' }}</h3>
          <button @click="closeModal" class="modal-close">×</button>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>Course Name <span class="required"> *</span></label>
              <input v-model="form.courseName" type="text" placeholder="Enter course name" required />
            </div>

            <div class="form-group">
              <label>Location <span class="required"> *</span></label>
              <select v-model="form.location" required>
                <option value="">Select Location</option>
                <option value="Mumbai">Mumbai</option>
                <option value="Delhi">Delhi</option>
                <option value="Bangalore">Bangalore</option>
                <option value="Hyderabad">Hyderabad</option>
                <option value="Chennai">Chennai</option>
                <option value="Pune">Pune</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Start Date <span class="required"> *</span></label>
              <input 
                v-model="form.startDate" 
                type="date" 
                :min="editMode ? '' : minStartDate" 
                required 
              />
            </div>

            <div class="form-group">
              <label>End Date <span class="required"> *</span></label>
              <input 
                v-model="form.endDate" 
                type="date" 
                :min="editMode ? '' : minEndDate" 
                required 
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Select Trainer</label>
              <select v-model="form.trainerId">
                <option value="">Select Trainer (Optional)</option>
                <option v-for="trainer in availableTrainers" :key="trainer.id" :value="trainer.id">
                  {{ trainer.name }} - {{ trainer.location }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Max Capacity <span class="required"> *</span></label>
              <input v-model.number="form.maxCapacity" type="number" min="1" placeholder="Enter max capacity" required />
            </div>
          </div>

          <div class="form-group">
            <label>Status <span class="required"> *</span></label>
            <select v-model="form.status" required>
              <option value="Upcoming">Upcoming</option>
              <option value="Active">Active</option>
              <option v-if="editMode" value="Completed">Completed</option>
            </select>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Processing...' : (editMode ? 'Update' : 'Create') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div class="card">
      <h3>All Batches</h3>
      
      <div v-if="loading && batches.length === 0" class="loading">
        <p>Loading batches...</p>
      </div>

      <div v-else-if="batches.length > 0" class="batches-grid">
        <div v-for="batch in batches" :key="batch.id" class="batch-card">
          <div class="batch-header">
            <h4>{{ batch.courseName }}</h4>
            <span :class="['status-badge', getStatusClass(batch.status)]">
              {{ batch.status }}
            </span>
          </div>
          
          <div class="batch-details">
            <div class="detail-item">
              <span class="icon">📍</span>
              <span><strong>Location:</strong> {{ batch.location }}</span>
            </div>
            
            <div class="detail-item">
              <span class="icon">👨‍🏫</span>
              <span><strong>Trainer:</strong> {{ batch.trainerName || 'TBD' }}</span>
            </div>
            
            <div class="detail-item">
              <span class="icon">📅</span>
              <span><strong>Duration:</strong> {{ formatDate(batch.startDate) }} - {{ formatDate(batch.endDate) }}</span>
            </div>
            
            <div class="detail-item">
              <span class="icon">👥</span>
              <span><strong>Enrolled:</strong> {{ batch.enrolledCount || 0 }} / {{ batch.maxCapacity }}</span>
            </div>
          </div>

          <div class="batch-actions">
            <button @click="editBatch(batch)" class="btn-icon btn-edit" title="Edit">
              ✏️ Edit
            </button>
            <button @click="deleteBatch(batch.id)" class="btn-icon btn-delete" title="Delete">
              🗑️ Delete
            </button>
          </div>
        </div>
      </div>

      <div v-else class="no-data">
        <p>No batches created yet</p>
        <p>Create a new batch using the button above</p>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
import { showToast } from '../utils/toast'

export default {
  name: 'BatchManagement',
  data() {
    return {
      showModal: false,
      form: {
        id: null,
        courseName: '',
        location: '',
        startDate: '',
        endDate: '',
        trainerId: '',
        trainerName: '',
        maxCapacity: 30,
        enrolledCount: 0,
        status: 'Upcoming'
      },
      batches: [],
      availableTrainers: [],
      loading: false,
      editMode: false
    }
  },
  computed: {
    minStartDate() {
      const today = new Date()
      return today.toISOString().split('T')[0]
    },
    minEndDate() {
      const tomorrow = new Date()
      tomorrow.setDate(tomorrow.getDate() + 1)
      return tomorrow.toISOString().split('T')[0]
    }
  },
  mounted() {
    this.loadBatches()
    this.loadTrainers()
  },
  methods: {
    async loadBatches() {
      this.loading = true
      try {
        const response = await api.getBatches()
        this.batches = response.data || []
      } catch (error) {
        showToast('Error loading batches', 'error')
      } finally {
        this.loading = false
      }
    },

    async loadTrainers() {
      try {
        const response = await api.getTrainers()
        this.availableTrainers = response.data || []
      } catch (error) {
        console.error('Error loading trainers:', error)
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
      // Validation only for NEW batches (not edit)
      if (!this.editMode) {
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        
        const startDate = new Date(this.form.startDate)
        const endDate = new Date(this.form.endDate)

        if (startDate < today) {
          showToast('Start date cannot be in the past', 'error')
          return
        }

        if (endDate <= startDate) {
          showToast('End date must be after start date', 'error')
          return
        }
      } else {
        // For edit mode, just check end date is after start date
        const startDate = new Date(this.form.startDate)
        const endDate = new Date(this.form.endDate)

        if (endDate <= startDate) {
          showToast('End date must be after start date', 'error')
          return
        }
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateBatch(this.form.id, this.form)
          showToast('Batch updated successfully!', 'success')
        } else {
          await api.createBatch(this.form)
          showToast('Batch created successfully!', 'success')
        }
        
        this.closeModal()
        this.loadBatches()
      } catch (error) {
        showToast('Error: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },

    editBatch(batch) {
      this.editMode = true
      this.form = {
        id: batch.id,
        courseName: batch.courseName,
        location: batch.location,
        startDate: batch.startDate,
        endDate: batch.endDate,
        trainerId: batch.trainerId || '',
        trainerName: batch.trainerName,
        maxCapacity: batch.maxCapacity,
        enrolledCount: batch.enrolledCount,
        status: batch.status
      }
      this.showModal = true
    },

    async deleteBatch(id) {
      if (!confirm('Are you sure you want to delete this batch?')) {
        return
      }
      try {
        await api.deleteBatch(id)
        showToast('Batch deleted successfully!', 'success')
        this.loadBatches()
      } catch (error) {
        showToast('Error deleting batch', 'error')
      }
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        courseName: '',
        location: '',
        startDate: '',
        endDate: '',
        trainerId: '',
        trainerName: '',
        maxCapacity: 30,
        enrolledCount: 0,
        status: 'Upcoming'
      }
    },

    formatDate(date) {
      if (!date) return 'N/A'
      return new Date(date).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      })
    },

    getStatusClass(status) {
      const classes = {
        'Active': 'status-active',
        'Upcoming': 'status-upcoming',
        'Completed': 'status-completed'
      }
      return classes[status] || 'status-default'
    }
  }
}
</script>

<style scoped>
.batch-management {
  max-width: 1400px;
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
  max-width: 800px;
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

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.batches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.batch-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.batch-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.batch-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.batch-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status-active {
  background: #d4edda;
  color: #155724;
}

.status-upcoming {
  background: #cce5ff;
  color: #004085;
}

.status-completed {
  background: #e2e3e5;
  color: #383d41;
}

.batch-details {
  margin-bottom: 1rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  font-size: 0.95rem;
}

.detail-item .icon {
  font-size: 1.2rem;
}

.batch-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f0f0f0;
}

.btn-icon {
  flex: 1;
  background: white;
  border: 2px solid #e0e0e0;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.3s;
}

.btn-edit:hover {
  background: #e3f2fd;
  border-color: #2196f3;
}

.btn-delete:hover {
  background: #ffebee;
  border-color: #f44336;
}

.no-data {
  text-align: center;
  padding: 2rem;
  color: #666;
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

  .batches-grid {
    grid-template-columns: 1fr;
  }

  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}
</style>