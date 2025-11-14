<template>
  <div class="attendance-marking">
    <h2>✅ Mark Attendance</h2>

    <div class="card">
      <h3>Mark Attendance</h3>
      <form @submit.prevent="markAttendance">
        <div class="form-row">
          <div class="form-group">
            <label>Select Batch:</label>
            <select v-model="selectedBatchId" @change="loadTraineesByBatch" required>
              <option value="">Select a Batch</option>
              <option v-for="batch in batches" :key="batch.id" :value="batch.id">
                {{ batch.courseName }} - {{ batch.location }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Attendance Date:</label>
            <input v-model="form.date" type="date" required :max="maxDate" />
          </div>
        </div>

        <div class="form-group" v-if="selectedBatchId && batchTrainees.length > 0">
          <label>Select Trainee:</label>
          <select v-model="form.traineeId" required>
            <option value="">Select a Trainee</option>
            <option v-for="trainee in batchTrainees" :key="trainee.id" :value="trainee.id">
              {{ trainee.name }} ({{ trainee.email }})
            </option>
          </select>
        </div>

        <div class="form-group" v-if="selectedBatchId && batchTrainees.length === 0 && !loading">
          <p class="info-message">⚠️ No trainees enrolled in this batch yet. Please enroll trainees first.</p>
        </div>

        <div class="form-group" v-if="form.traineeId || editMode">
          <label>Attendance Status:</label>
          <div class="radio-group">
            <label class="radio-label">
              <input type="radio" v-model="form.status" value="PRESENT" required />
              <span class="badge badge-success">Present</span>
            </label>
            <label class="radio-label">
              <input type="radio" v-model="form.status" value="ABSENT" required />
              <span class="badge badge-danger">Absent</span>
            </label>
            <label class="radio-label">
              <input type="radio" v-model="form.status" value="LATE" required />
              <span class="badge badge-warning">Late</span>
            </label>
          </div>
        </div>

        <div class="button-group">
          <button type="submit" class="btn btn-primary" :disabled="loading || (!form.traineeId && !editMode)">
            {{ loading ? 'Processing...' : (editMode ? 'Update Attendance' : 'Mark Attendance') }}
          </button>
          <button v-if="editMode" type="button" class="btn btn-secondary" @click="cancelEdit">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <div class="card">
      <h3>Attendance Records</h3>
      
      <div class="form-group">
        <label>Filter by Batch:</label>
        <select v-model="filterBatchId" @change="filterAttendance">
          <option value="">All Batches</option>
          <option v-for="batch in batches" :key="batch.id" :value="batch.id">
            {{ batch.courseName }} - {{ batch.location }}
          </option>
        </select>
      </div>

      <div class="table-responsive" v-if="filteredAttendanceRecords.length > 0">
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
            <tr v-for="record in filteredAttendanceRecords" :key="record.id">
              <td>{{ record.id }}</td>
              <td>{{ formatDate(record.date) }}</td>
              <td>{{ record.traineeName }}</td>
              <td>{{ record.batchName }}</td>
              <td>
                <span :class="['badge', getBadgeClass(record.status)]">
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
        <p>No attendance records found</p>
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
  name: 'AttendanceMarking',
  data() {
    return {
      form: {
        id: null,
        traineeId: '',
        batchId: '',
        date: '',
        status: 'PRESENT'
      },
      selectedBatchId: '',
      filterBatchId: '',
      batches: [],
      batchTrainees: [],
      attendanceRecords: [],
      filteredAttendanceRecords: [],
      message: '',
      messageType: 'success',
      loading: false,
      editMode: false,
      maxDate: ''
    }
  },
  mounted() {
    this.setMaxDate()
    this.loadBatches()
    this.loadAttendanceRecords()
  },
  methods: {
    setMaxDate() {
      const today = new Date()
      this.maxDate = today.toISOString().split('T')[0]
      this.form.date = this.maxDate
    },

    async loadBatches() {
      try {
        const response = await api.getBatches()
        this.batches = response.data || []
      } catch (error) {
        console.error('Error loading batches:', error)
        this.showMessage('Error loading batches', 'error')
      }
    },

    async loadTraineesByBatch() {
      if (!this.selectedBatchId) {
        this.batchTrainees = []
        this.form.traineeId = ''
        return
      }
      
      this.loading = true
      this.form.traineeId = ''
      this.batchTrainees = []
      
      try {
        const response = await api.getTraineesByBatch(this.selectedBatchId)
        this.batchTrainees = response.data || []
        this.form.batchId = this.selectedBatchId
      } catch (error) {
        console.error('Error loading trainees:', error)
        this.batchTrainees = []
      } finally {
        this.loading = false
      }
    },

    async markAttendance() {
      if ((!this.form.traineeId || !this.form.batchId) && !this.editMode) {
        this.showMessage('Please select batch and trainee', 'error')
        return
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateAttendance(this.form.id, this.form)
          this.showMessage('✅ Attendance updated successfully!', 'success')
        } else {
          await api.markAttendance(this.form)
          this.showMessage('✅ Attendance marked successfully!', 'success')
        }
        
        this.loadAttendanceRecords()
        this.resetForm()
      } catch (error) {
        this.showMessage('❌ Error: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.loading = false
      }
    },

    editAttendance(record) {
      this.editMode = true
      this.form = {
        id: record.id,
        traineeId: record.traineeId,
        batchId: record.batchId,
        date: record.date,
        status: record.status
      }
      this.selectedBatchId = record.batchId
      this.loadTraineesByBatch()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    async deleteAttendance(id) {
      if (!confirm('Are you sure you want to delete this attendance record?')) {
        return
      }

      try {
        await api.deleteAttendance(id)
        this.showMessage('✅ Attendance deleted successfully!', 'success')
        this.loadAttendanceRecords()
      } catch (error) {
        this.showMessage('❌ Error deleting attendance', 'error')
      }
    },

    cancelEdit() {
      this.resetForm()
    },

    resetForm() {
      this.editMode = false
      this.form = {
        id: null,
        traineeId: '',
        batchId: '',
        date: this.maxDate,
        status: 'PRESENT'
      }
      this.selectedBatchId = ''
      this.batchTrainees = []
    },

    async loadAttendanceRecords() {
      try {
        const response = await api.getAllAttendance()
        this.attendanceRecords = response.data || []
        this.filterAttendance()
      } catch (error) {
        console.error('Error loading attendance records:', error)
      }
    },

    filterAttendance() {
      if (this.filterBatchId) {
        this.filteredAttendanceRecords = this.attendanceRecords.filter(
          record => record.batchId == this.filterBatchId
        )
      } else {
        this.filteredAttendanceRecords = [...this.attendanceRecords]
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

    getBadgeClass(status) {
      const classes = {
        'PRESENT': 'badge-success',
        'ABSENT': 'badge-danger',
        'LATE': 'badge-warning'
      }
      return classes[status] || 'badge-info'
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
.attendance-marking {
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

.radio-group {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.radio-label input[type="radio"] {
  width: auto;
  cursor: pointer;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.info-message {
  color: #856404;
  background: #fff3cd;
  padding: 1rem;
  border-radius: 5px;
  border: 1px solid #ffc107;
  text-align: center;
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
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>