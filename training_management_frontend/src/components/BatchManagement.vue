<template>
  <div class="batch-management">
    <h2>📚 Batch Management</h2>

    <div class="card">
      <h3>{{ editMode ? 'Edit Batch' : 'Create New Batch' }}</h3>
      <form @submit.prevent="submitForm">
        <div class="form-row">
          <div class="form-group">
            <label>Course Name:</label>
            <input v-model="form.courseName" type="text" placeholder="Enter course name" required />
          </div>

          <div class="form-group">
            <label>Location:</label>
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
            <label>Start Date:</label>
            <input v-model="form.startDate" type="date" required />
          </div>

          <div class="form-group">
            <label>End Date:</label>
            <input v-model="form.endDate" type="date" required />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Select Trainer:</label>
            <select v-model="form.trainerId">
              <option value="">Select Trainer (Optional)</option>
              <option v-for="trainer in availableTrainers" :key="trainer.id" :value="trainer.id">
                {{ trainer.name }} - {{ trainer.location }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Max Capacity:</label>
            <input v-model.number="form.maxCapacity" type="number" min="1" placeholder="Enter max capacity" required />
          </div>
        </div>

        <div class="form-group">
          <label>Status:</label>
          <select v-model="form.status" required>
            <option value="Active">Active</option>
            <option value="Upcoming">Upcoming</option>
            <option value="Completed">Completed</option>
          </select>
        </div>

        <div class="button-group">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Processing...' : (editMode ? 'Update Batch' : 'Create Batch') }}
          </button>
          <button v-if="editMode" type="button" class="btn btn-secondary" @click="cancelEdit">
            Cancel
          </button>
        </div>
      </form>
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
        <p>Create a new batch using the form above</p>
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
  name: 'BatchManagement',
  data() {
    return {
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
      message: '',
      messageType: 'success',
      loading: false,
      editMode: false
    }
  },
  mounted() {
    console.log('BatchManagement component mounted')
    this.loadBatches()
    this.loadTrainers()
  },
  methods: {
    async loadBatches() {
      console.log('Loading batches...')
      this.loading = true
      try {
        const response = await api.getBatches()
        console.log('Batches response:', response.data)
        this.batches = response.data || []
        console.log('Batches loaded:', this.batches.length)
      } catch (error) {
        console.error('Error loading batches:', error)
        this.showMessage('Error loading batches: ' + error.message, 'error')
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

    async submitForm() {
      if (new Date(this.form.endDate) <= new Date(this.form.startDate)) {
        this.showMessage('End date must be after start date', 'error')
        return
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateBatch(this.form.id, this.form)
          this.showMessage('✅ Batch updated successfully!', 'success')
        } else {
          await api.createBatch(this.form)
          this.showMessage('✅ Batch created successfully!', 'success')
        }
        
        this.loadBatches()
        this.resetForm()
      } catch (error) {
        console.error('Submit error:', error)
        this.showMessage('❌ Error: ' + (error.response?.data?.message || error.message), 'error')
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
        trainerId: batch.trainerId,
        trainerName: batch.trainerName,
        maxCapacity: batch.maxCapacity,
        enrolledCount: batch.enrolledCount,
        status: batch.status
      }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    async deleteBatch(id) {
      if (!confirm('Are you sure you want to delete this batch? All associated trainees will be affected.')) {
        return
      }

      try {
        await api.deleteBatch(id)
        this.showMessage('✅ Batch deleted successfully!', 'success')
        this.loadBatches()
      } catch (error) {
        console.error('Delete error:', error)
        this.showMessage('❌ Error deleting batch', 'error')
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
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    },

    getStatusClass(status) {
      const classes = {
        'Active': 'status-active',
        'Upcoming': 'status-upcoming',
        'Completed': 'status-completed'
      }
      return classes[status] || 'status-default'
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
.batch-management {
  max-width: 1400px;
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
  h2 {
    font-size: 1.5rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }

  .batches-grid {
    grid-template-columns: 1fr;
  }
}
</style>