<template>
  <div class="trainer-availability">
    <h2>📅 Trainer Availability Management</h2>

    <div class="card">
      <h3>{{ editMode ? 'Edit Trainer' : 'Add Availability' }}</h3>
      <form @submit.prevent="submitForm">
        <div class="form-row">
          <div class="form-group">
            <label>Trainer Name:</label>
            <input v-model="form.name" type="text" placeholder="Enter trainer name" required />
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
            <label>Available Date:</label>
            <input v-model="form.availableDate" type="date" required />
          </div>

          <div class="form-group">
            <label>Time Slot:</label>
            <select v-model="form.timeSlot" required>
              <option value="">Select Time Slot</option>
              <option value="09:00-12:00">09:00 - 12:00</option>
              <option value="09:00-18:00">09:00 - 18:00</option>
              <option value="12:00-18:00">12:00 - 18:00</option>
              <option value="14:00-18:00">14:00 - 18:00</option>
            </select>
          </div>
        </div>

        <div class="button-group">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Processing...' : (editMode ? 'Update Trainer' : 'Add Availability') }}
          </button>
          <button v-if="editMode" type="button" class="btn btn-secondary" @click="cancelEdit">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <div class="card" v-if="availabilities.length > 0">
      <h3>Current Availabilities</h3>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Trainer</th>
              <th>Location</th>
              <th>Date</th>
              <th>Time Slot</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="availability in availabilities" :key="availability.id">
              <td>{{ availability.id }}</td>
              <td>{{ availability.name }}</td>
              <td>{{ availability.location }}</td>
              <td>{{ formatDate(availability.availableDate) }}</td>
              <td>{{ availability.timeSlot }}</td>
              <td>
                <span :class="['badge', availability.assigned ? 'badge-warning' : 'badge-success']">
                  {{ availability.assigned ? 'Assigned to Batch' : 'Available' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="editTrainer(availability)" class="btn-icon btn-edit" title="Edit">
                    ✏️
                  </button>
                  <button @click="deleteTrainer(availability.id)" class="btn-icon btn-delete" title="Delete">
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
        <p>No availability records found</p>
        <p>Add trainer availability using the form above</p>
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
  name: 'TrainerAvailability',
  data() {
    return {
      form: {
        id: null,
        name: '',
        location: '',
        availableDate: '',
        timeSlot: '',
        assigned: false,
        batchId: null
      },
      availabilities: [],
      message: '',
      messageType: 'success',
      loading: false,
      editMode: false
    }
  },
  mounted() {
    this.loadAvailabilities()
  },
  methods: {
    async submitForm() {
      this.loading = true
      try {
        if (this.editMode) {
          await api.updateTrainer(this.form.id, this.form)
          this.showMessage('✅ Trainer updated successfully!', 'success')
        } else {
          await api.addTrainerAvailability(this.form)
          this.showMessage('✅ Trainer availability added successfully!', 'success')
        }
        
        this.loadAvailabilities()
        this.resetForm()
      } catch (error) {
        this.showMessage('❌ Error: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },

    editTrainer(trainer) {
      this.editMode = true
      this.form = {
        id: trainer.id,
        name: trainer.name,
        location: trainer.location,
        availableDate: trainer.availableDate,
        timeSlot: trainer.timeSlot,
        assigned: trainer.assigned,
        batchId: trainer.batchId
      }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    async deleteTrainer(id) {
      if (!confirm('Are you sure you want to delete this trainer?')) {
        return
      }

      try {
        await api.deleteTrainer(id)
        this.showMessage('✅ Trainer deleted successfully!', 'success')
        this.loadAvailabilities()
      } catch (error) {
        this.showMessage('❌ Error deleting trainer', 'error')
      }
    },

    cancelEdit() {
      this.resetForm()
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        name: '',
        location: '',
        availableDate: '',
        timeSlot: '',
        assigned: false,
        batchId: null
      }
    },

    async loadAvailabilities() {
      try {
        const response = await api.getTrainers()
        this.availabilities = response.data || []
      } catch (error) {
        console.error('Error loading availabilities:', error)
        this.showMessage('Error loading trainer availabilities', 'error')
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
.trainer-availability {
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