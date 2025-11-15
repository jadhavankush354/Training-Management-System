<template>
  <div class="attendance-marking">
    <div class="page-header">
      <h2>✅ Mark Attendance</h2>
      <button @click="openAddModal" class="btn btn-add">
        ➕ Mark Attendance
      </button>
    </div>

    <!-- Modal Overlay -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editMode ? 'Edit Attendance' : 'Mark Attendance' }}</h3>
          <button @click="closeModal" class="modal-close">×</button>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>Select Batch <span class="required">*</span></label>
              <select v-model="form.batchId" @change="loadTraineesForBatch" required>
                <option value="">Select a Batch</option>
                <option v-for="batch in batches" :key="batch.id" :value="batch.id">
                  {{ batch.courseName }} - {{ batch.location }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Attendance Date <span class="required">*</span></label>
              <input 
                v-model="form.date" 
                type="date" 
                :max="maxDate"
                required 
              />
              <small class="hint">Cannot select future dates</small>
            </div>
          </div>

          <div class="form-row" v-if="form.batchId">
            <div class="form-group">
              <label>Select Trainee <span class="required">*</span></label>
              <select v-model="form.traineeId" required>
                <option value="">Select a Trainee</option>
                <option v-for="trainee in batchTrainees" :key="trainee.id" :value="trainee.id">
                  {{ trainee.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Status <span class="required">*</span></label>
              <select v-model="form.status" required>
                <option value="">Select Status</option>
                <option value="PRESENT">Present</option>
                <option value="ABSENT">Absent</option>
                <option value="LATE">Late</option>
              </select>
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" :disabled="loading || !form.batchId">
              {{ loading ? 'Processing...' : (editMode ? 'Update' : 'Mark') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div class="card">
      <h3>Attendance Records</h3>
      <div class="filter-section">
        <label>Filter by Batch:</label>
        <select v-model="filterBatchId" @change="filterAttendance">
          <option value="">All Batches</option>
          <option v-for="batch in batches" :key="batch.id" :value="batch.id">
            {{ batch.courseName }} - {{ batch.location }}
          </option>
        </select>
      </div>

      <div v-if="loadingRecords" class="loading-message">
        <p>Loading attendance records...</p>
      </div>

      <div v-else-if="filteredAttendance.length > 0" class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Date</th>
              <th>Trainee</th>
              <th>Batch</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in filteredAttendance" :key="record.id">
              <td>{{ record.id }}</td>
              <td>{{ formatDate(record.date) }}</td>
              <td>{{ record.traineeName }}</td>
              <td>{{ record.batchName }}</td>
              <td>
                <span :class="['badge', getStatusClass(record.status)]">
                  {{ record.status }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button @click="editAttendance(record)" class="btn-icon btn-edit" title="Edit">
                    ✏️
                  </button>
                  <button @click="deleteAttendance(record.id)" class="btn-icon btn-delete" title="Delete">
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="no-data">
        <p>📋 No attendance records found</p>
        <p>Click "Mark Attendance" button to add new records</p>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'
import { showToast } from '../utils/toast'

export default {
  name: 'AttendanceMarking',
  data() {
    return {
      showModal: false,
      form: {
        id: null,
        batchId: '',
        traineeId: '',
        date: new Date().toISOString().split('T')[0],
        status: ''
      },
      batches: [],
      batchTrainees: [],
      attendanceRecords: [],
      filteredAttendance: [],
      filterBatchId: '',
      loading: false,
      loadingRecords: false,
      editMode: false
    }
  },
  computed: {
    maxDate() {
      const today = new Date()
      return today.toISOString().split('T')[0]
    }
  },
  mounted() {
    this.loadBatches()
    this.loadAttendance()
  },
  methods: {
    async loadBatches() {
      try {
        const response = await api.getBatches()
        this.batches = response.data || []
      } catch (error) {
        console.error('Error loading batches:', error)
        showToast('Error loading batches', 'error')
      }
    },

    async loadAttendance() {
      this.loadingRecords = true
      try {
        const response = await api.getAllAttendance()
        console.log('Attendance response:', response.data)
        this.attendanceRecords = response.data || []
        this.filteredAttendance = this.attendanceRecords
      } catch (error) {
        console.error('Error loading attendance:', error)
        showToast('Error loading attendance records', 'error')
        this.attendanceRecords = []
        this.filteredAttendance = []
      } finally {
        this.loadingRecords = false
      }
    },

    async loadTraineesForBatch() {
      if (!this.form.batchId) {
        this.batchTrainees = []
        return
      }
      try {
        const response = await api.getTraineesByBatch(this.form.batchId)
        this.batchTrainees = response.data || []
        
        if (this.batchTrainees.length === 0) {
          showToast('No trainees found in this batch', 'warning')
        }
      } catch (error) {
        console.error('Error loading trainees:', error)
        showToast('Error loading trainees for batch', 'error')
        this.batchTrainees = []
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
      // Date validation - cannot be future date
      const selectedDate = new Date(this.form.date)
      const today = new Date()
      today.setHours(23, 59, 59, 999)
      
      if (selectedDate > today) {
        showToast('Cannot mark attendance for future dates', 'error')
        return
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateAttendance(this.form.id, this.form)
          showToast('Attendance updated successfully!', 'success')
        } else {
          await api.markAttendance(this.form)
          showToast('Attendance marked successfully!', 'success')
        }
        this.closeModal()
        this.loadAttendance()
      } catch (error) {
        console.error('Submit error:', error)
        showToast('Error: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },

    editAttendance(record) {
      this.editMode = true
      this.form = {
        id: record.id,
        batchId: record.batchId,
        traineeId: record.traineeId,
        date: record.date,
        status: record.status
      }
      this.loadTraineesForBatch()
      this.showModal = true
    },

    async deleteAttendance(id) {
      if (!confirm('Are you sure you want to delete this attendance record?')) {
        return
      }
      try {
        await api.deleteAttendance(id)
        showToast('Attendance deleted successfully!', 'success')
        this.loadAttendance()
      } catch (error) {
        showToast('Error deleting attendance', 'error')
      }
    },

    filterAttendance() {
      if (!this.filterBatchId) {
        this.filteredAttendance = this.attendanceRecords
      } else {
        this.filteredAttendance = this.attendanceRecords.filter(
          record => record.batchId === parseInt(this.filterBatchId)
        )
      }
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        batchId: '',
        traineeId: '',
        date: new Date().toISOString().split('T')[0],
        status: ''
      }
      this.batchTrainees = []
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
        'PRESENT': 'badge-success',
        'ABSENT': 'badge-danger',
        'LATE': 'badge-warning'
      }
      return classes[status] || 'badge-info'
    }
  }
}
</script>

<style scoped>
.attendance-marking {
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

.filter-section {
  margin-bottom: 1.5rem;
}

.filter-section label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.filter-section select {
  width: 100%;
  max-width: 400px;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
}

.loading-message {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.no-data {
  text-align: center;
  padding: 3rem 2rem;
  color: #666;
  background: #f8f9fa;
  border-radius: 8px;
}

.no-data p:first-child {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
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