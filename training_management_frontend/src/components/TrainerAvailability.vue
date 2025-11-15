<template>
  <div class="trainer-availability">
    <div class="page-header">
      <h2>📅 Trainer Availability Management</h2>
      <button @click="openAddModal" class="btn btn-add">
        ➕ Add Availability
      </button>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editMode ? 'Edit Trainer' : 'Add Availability' }}</h3>
          <button @click="closeModal" class="modal-close">×</button>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>Trainer Name <span class="required">*</span></label>
              <input v-model="form.name" type="text" placeholder="Enter trainer name" required />
            </div>

            <div class="form-group">
              <label>Location <span class="required">*</span></label>
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
              <label>Available Date <span class="required">*</span></label>
              <input 
                v-model="form.availableDate" 
                type="date" 
                :min="editMode ? '' : minDate"
                required 
              />
              <small v-if="!editMode" class="hint">Cannot select past dates</small>
            </div>

            <div class="form-group">
              <label>Time Slot <span class="required">*</span></label>
              <select v-model="selectedTimeSlot" @change="handleTimeSlotChange" required>
                <option value="">Select Time Slot</option>
                <option value="06:00 AM - 09:00 AM">06:00 AM - 09:00 AM</option>
                <option value="09:00 AM - 12:00 PM">09:00 AM - 12:00 PM</option>
                <option value="12:00 PM - 03:00 PM">12:00 PM - 03:00 PM</option>
                <option value="03:00 PM - 06:00 PM">03:00 PM - 06:00 PM</option>
                <option value="06:00 PM - 09:00 PM">06:00 PM - 09:00 PM</option>
                <option value="09:00 AM - 06:00 PM">09:00 AM - 06:00 PM (Full Day)</option>
                <option value="other">Other (Custom Time)</option>
              </select>
            </div>
          </div>

          <!-- Custom Time Input -->
          <div v-if="showCustomTime" class="form-row">
            <div class="form-group">
              <label>Start Time <span class="required">*</span></label>
              <div class="time-input-group">
                <input 
                  v-model="customStartTime" 
                  type="time" 
                  required 
                />
                <span class="time-display">{{ formatTimeDisplay(customStartTime) }}</span>
              </div>
            </div>

            <div class="form-group">
              <label>End Time <span class="required">*</span></label>
              <div class="time-input-group">
                <input 
                  v-model="customEndTime" 
                  type="time" 
                  required 
                />
                <span class="time-display">{{ formatTimeDisplay(customEndTime) }}</span>
              </div>
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
            <tr v-for="trainer in trainers" :key="trainer.id">
              <td>{{ trainer.id }}</td>
              <td>{{ trainer.name }}</td>
              <td>{{ trainer.location }}</td>
              <td>{{ formatDate(trainer.availableDate) }}</td>
              <td>{{ trainer.timeSlot }}</td>
              <td>
                <span :class="['badge', trainer.assigned ? 'badge-warning' : 'badge-success']">
                  {{ trainer.assigned ? 'Assigned to Batch' : 'Available' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="editTrainer(trainer)" class="btn-icon btn-edit" title="Edit">
                    ✏️
                  </button>
                  <button @click="deleteTrainer(trainer.id)" class="btn-icon btn-delete" title="Delete">
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
  name: 'TrainerAvailability',
  data() {
    return {
      showModal: false,
      form: {
        id: null,
        name: '',
        location: '',
        availableDate: '',
        timeSlot: '',
        assigned: false
      },
      selectedTimeSlot: '',
      showCustomTime: false,
      customStartTime: '',
      customEndTime: '',
      trainers: [],
      loading: false,
      editMode: false
    }
  },
  computed: {
    minDate() {
      const today = new Date()
      return today.toISOString().split('T')[0]
    }
  },
  mounted() {
    this.loadTrainers()
  },
  methods: {
    async loadTrainers() {
      try {
        const response = await api.getTrainers()
        this.trainers = response.data
      } catch (error) {
        showToast('Error loading trainers', 'error')
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

    handleTimeSlotChange() {
      if (this.selectedTimeSlot === 'other') {
        this.showCustomTime = true
        this.form.timeSlot = ''
      } else {
        this.showCustomTime = false
        this.form.timeSlot = this.selectedTimeSlot
        this.customStartTime = ''
        this.customEndTime = ''
      }
    },

    formatTimeDisplay(time24) {
      if (!time24) return ''
      const [hours, minutes] = time24.split(':')
      const hour = parseInt(hours)
      const ampm = hour >= 12 ? 'PM' : 'AM'
      const hour12 = hour % 12 || 12
      return `${hour12}:${minutes} ${ampm}`
    },

    convertTo12Hour(time24) {
      if (!time24) return ''
      const [hours, minutes] = time24.split(':')
      const hour = parseInt(hours)
      const ampm = hour >= 12 ? 'PM' : 'AM'
      const hour12 = hour % 12 || 12
      return `${hour12.toString().padStart(2, '0')}:${minutes} ${ampm}`
    },

    async submitForm() {
      // Date validation only for ADD (not EDIT)
      if (!this.editMode) {
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        const selectedDate = new Date(this.form.availableDate)
        
        if (selectedDate < today) {
          showToast('Available date cannot be in the past', 'error')
          return
        }
      }

      // Handle custom time slot
      if (this.showCustomTime) {
        if (!this.customStartTime || !this.customEndTime) {
          showToast('Please enter both start and end time', 'error')
          return
        }
        
        if (this.customStartTime >= this.customEndTime) {
          showToast('End time must be after start time', 'error')
          return
        }
        
        const startFormatted = this.convertTo12Hour(this.customStartTime)
        const endFormatted = this.convertTo12Hour(this.customEndTime)
        this.form.timeSlot = `${startFormatted} - ${endFormatted}`
      }

      if (!this.form.timeSlot) {
        showToast('Please select a time slot', 'error')
        return
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateTrainer(this.form.id, this.form)
          showToast('Trainer updated successfully!', 'success')
        } else {
          await api.addTrainerAvailability(this.form)
          showToast('Trainer availability added successfully!', 'success')
        }
        this.closeModal()
        this.loadTrainers()
      } catch (error) {
        showToast('Error: ' + (error.response?.data?.message || error.message), 'error')
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
        assigned: trainer.assigned
      }
      
      // Check if it's a predefined slot or custom
      const predefinedSlots = [
        '06:00 AM - 09:00 AM',
        '09:00 AM - 12:00 PM',
        '12:00 PM - 03:00 PM',
        '03:00 PM - 06:00 PM',
        '06:00 PM - 09:00 PM',
        '09:00 AM - 06:00 PM (Full Day)'
      ]
      
      if (predefinedSlots.includes(trainer.timeSlot)) {
        this.selectedTimeSlot = trainer.timeSlot
        this.showCustomTime = false
      } else {
        this.selectedTimeSlot = 'other'
        this.showCustomTime = true
        // Parse the custom time (e.g., "10:30 AM - 02:30 PM")
        // For now, leave custom fields empty - user can re-enter if needed
      }
      
      this.showModal = true
    },

    async deleteTrainer(id) {
      if (!confirm('Are you sure you want to delete this trainer?')) {
        return
      }
      try {
        await api.deleteTrainer(id)
        showToast('Trainer deleted successfully!', 'success')
        this.loadTrainers()
      } catch (error) {
        showToast('Error deleting trainer', 'error')
      }
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        name: '',
        location: '',
        availableDate: '',
        timeSlot: '',
        assigned: false
      }
      this.selectedTimeSlot = ''
      this.showCustomTime = false
      this.customStartTime = ''
      this.customEndTime = ''
    },

    formatDate(date) {
      if (!date) return 'N/A'
      return new Date(date).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      })
    }
  }
}
</script>

<style scoped>
.trainer-availability {
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

.time-input-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.time-input-group input {
  flex: 1;
}

.time-display {
  font-size: 0.9rem;
  color: #667eea;
  font-weight: 600;
  min-width: 80px;
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

  .time-input-group {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>