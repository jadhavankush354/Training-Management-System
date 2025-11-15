<template>
  <div class="trainer-availability">
    <h2>📅 Trainer Availability Management</h2>
    <div v-if="message" :class="['alert', messageType]">
      {{ message }}
    </div>

    <div class="card">
      <h3>Add Availability</h3>
      <form @submit.prevent="submitAvailability">
        <div class="form-row">
          <div class="form-group">
            <label>Trainer Name<span style="color:red"> *</span></label>
            <input v-model="form.name" type="text" required placeholder="Enter trainer name" />
          </div>

          <div class="form-group">
            <label>Location<span style="color:red"> *</span></label>
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
            <label>Available Date<span style="color:red"> *</span></label>
            <input v-model="form.availableDate" type="date" required :min="minDate" />
          </div>

          <div class="form-group">
            <label>Time Slot<span style="color:red"> *</span></label>
            <select v-model="form.timeSlot" required>
              <option value="">Select Time Slot</option>
              <option value="09:00-12:00">09:00 AM - 12:00 PM</option>
              <option value="12:00-15:00">12:00 PM - 03:00 PM</option>
              <option value="15:00-18:00">03:00 PM - 06:00 PM</option>
              <option value="09:00-18:00">Full Day (09:00 AM - 06:00 PM)</option>
            </select>
          </div>
        </div>

        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Adding...' : 'Add Availability' }}
        </button>
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
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in availabilities" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.location }}</td>
              <td>{{ formatDate(item.availableDate) }}</td>
              <td>{{ item.timeSlot }}</td>
              <td>
                <span :class="['badge', item.assigned ? 'badge-warning' : 'badge-success']">
                  {{ item.assigned ? 'Assigned to Batch' : 'Available' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <h3>No availability records found</h3>
      <p>Add trainer availability using the form above</p>
    </div>

    <div v-if="loading && availabilities.length === 0" class="loading">
      <p>Loading availabilities...</p>
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
        name: '',
        location: '',
        availableDate: '',
        timeSlot: ''
      },
      availabilities: [],
      message: '',
      messageType: 'success',
      loading: false,
      minDate: ''
    }
  },
  mounted() {
    this.setMinDate()
    this.loadAvailabilities()
  },
  methods: {
    setMinDate() {
      const today = new Date()
      this.minDate = today.toISOString().split('T')[0]
    },

    async submitAvailability() {
      this.loading = true
      try {
        await api.addTrainerAvailability(this.form)
        this.showMessage('Availability added successfully!', 'success')
        this.resetForm()
        this.loadAvailabilities()
      } catch (error) {
        this.showMessage('Error adding availability: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },
    
    async loadAvailabilities() {
      this.loading = true
      try {
        const response = await api.getTrainers()
        this.availabilities = response.data
      } catch (error) {
        console.error('Error loading availabilities:', error)
        this.showMessage('Error loading availabilities', 'error')
      } finally {
        this.loading = false
      }
    },
    
    resetForm() {
      this.form = {
        name: '',
        location: '',
        availableDate: '',
        timeSlot: ''
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
.alert {
  margin: 1rem 0;
  padding: 1rem;
  border-radius: 6px;
  font-weight: 600;
  text-align: center;
}

.success {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #81c784;
}

.error {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ef9a9a;
}

.trainer-availability {
  max-width: 1000px;
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

.table-responsive {
  overflow-x: auto;
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