<template>
  <div class="trainee-enrollment">
    <h2>👨‍🎓 Trainee Enrollment Management</h2>
    <div v-if="message" :class="['alert', messageType]">
      {{ message }}
    </div>

    <div class="card">
      <h3>{{ editMode ? 'Edit Trainee' : 'Enroll New Trainee' }}</h3>
      <form @submit.prevent="submitForm">
        <div class="form-row">
          <div class="form-group">
            <label>Trainee Name<span style="color:red"> *</span></label>
            <input v-model="form.name" type="text" placeholder="Enter trainee name" required />
          </div>

          <div class="form-group">
            <label>Email<span style="color:red"> *</span></label>
            <input v-model="form.email" type="email" placeholder="Enter email" required />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Phone<span style="color:red"> *</span></label>
            <input
              v-model="form.phone"
              type="tel"
              placeholder="Enter phone number"
              required
              pattern="^[0-9]{10}$"
              title="Phone number must be exactly 10 digits"
            />
          </div>

          <div class="form-group">
            <label>Select Batch<span style="color:red"> *</span></label>
            <select v-model="form.batchId" required>
              <option value="">Select a Batch</option>
              <option v-for="batch in batches" :key="batch.id" :value="batch.id">
                {{ batch.courseName }} - {{ batch.location }}
              </option>
            </select>
          </div>
        </div>

        <div class="button-group">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Processing...' : (editMode ? 'Update Trainee' : 'Enroll Trainee') }}
          </button>
          <button v-if="editMode" type="button" class="btn btn-secondary" @click="cancelEdit">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <div class="card" v-if="trainees.length > 0">
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

    <div v-else class="card">
      <div class="no-data">
        <p>No trainees enrolled yet</p>
        <p>Enroll trainees using the form above</p>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  name: 'TraineeEnrollment',
  data() {
    return {
      form: {
        id: null,
        name: '',
        email: '',
        phone: '',
        batchId: '',
        batchName: '',
        location: ''
      },
      batches: [],
      trainees: [],
      message: '',
      messageType: 'success',
      loading: false,
      editMode: false
    }
  },
  mounted() {
    this.loadBatches()
    this.loadTrainees()
  },
  methods: {
    async loadBatches() {
      try {
        const response = await api.getBatches()
        this.batches = response.data || []
      } catch (error) {
        console.error('Error loading batches:', error)
        this.showMessage('Error loading batches', 'error')
      }
    },

    async loadTrainees() {
      try {
        const response = await api.getTrainees()
        this.trainees = response.data || []
      } catch (error) {
        console.error('Error loading trainees:', error)
        this.showMessage('Error loading trainees', 'error')
      }
    },

    async submitForm() {
      this.loading = true
      try {
        if (this.editMode) {
          await api.updateTrainee(this.form.id, this.form)
          this.showMessage('✅ Trainee updated successfully!', 'success')
        } else {
          await api.enrollTrainee(this.form)
          this.showMessage('✅ Trainee enrolled successfully!', 'success')
        }
        
        this.loadTrainees()
        this.resetForm()
      } catch (error) {
        this.showMessage('❌ Error: ' + (error.response?.data?.message || error.message), 'error')
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
        batchId: trainee.batchId,
        batchName: trainee.batchName,
        location: trainee.location
      }
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    async deleteTrainee(id) {
      if (!confirm('Are you sure you want to delete this trainee?')) {
        return
      }

      try {
        await api.deleteTrainee(id)
        this.showMessage('✅ Trainee deleted successfully!', 'success')
        this.loadTrainees()
      } catch (error) {
        this.showMessage('❌ Error deleting trainee', 'error')
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
        email: '',
        phone: '',
        batchId: '',
        batchName: '',
        location: ''
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

.trainee-enrollment {
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