<template>
  <div class="dashboard">
    <h2>📊 Dashboard & Analytics</h2>
    <div v-if="error" class="alert error">
      {{ error }}
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👨‍🏫</div>
        <div class="stat-info">
          <h3>{{ totalTrainers }}</h3>
          <p>Total Trainers</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">👨‍🎓</div>
        <div class="stat-info">
          <h3>{{ totalTrainees }}</h3>
          <p>Total Trainees</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-info">
          <h3>{{ totalBatches }}</h3>
          <p>Active Batches</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
          <h3>{{ averageAttendance }}%</h3>
          <p>Avg Attendance</p>
        </div>
      </div>
    </div>

    <!-- Chart 1: Trainer Availability vs Occupancy -->
    <div class="card chart-card">
      <h3>📅 Trainer Availability vs Occupancy</h3>
      <p class="chart-description">Comparison of available days vs days assigned to batches</p>
      <div class="chart-container">
        <canvas ref="trainerChart"></canvas>
      </div>
    </div>

    <!-- Chart 2: Batch Enrollments -->
    <div class="card chart-card">
      <h3>📚 Batch Enrollments Distribution</h3>
      <p class="chart-description">Trainee count per batch</p>
      <div class="chart-container">
        <canvas ref="batchChart"></canvas>
      </div>
    </div>

    <!-- Chart 3: Attendance Trends -->
    <div class="card chart-card">
      <h3>✅ Attendance Trends</h3>
      <p class="chart-description">Attendance percentage over sessions</p>
      <div class="chart-container">
        <canvas ref="attendanceChart"></canvas>
      </div>
    </div>

    <div v-if="loading" class="loading">
      <p>Loading dashboard data...</p>
    </div>


  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'
import api from '../services/api'

Chart.register(...registerables)

export default {
  name: 'DashboardView',
  data() {
    return {
      totalTrainers: 0,
      totalTrainees: 0,
      totalBatches: 0,
      averageAttendance: 0,
      loading: false,
      error: '',
      trainerChart: null,
      batchChart: null,
      attendanceChart: null
    }
  },
  mounted() {
    this.loadDashboardData()
  },
  beforeUnmount() {
    // Destroy charts when component is destroyed
    if (this.trainerChart) this.trainerChart.destroy()
    if (this.batchChart) this.batchChart.destroy()
    if (this.attendanceChart) this.attendanceChart.destroy()
  },
  methods: {
    async loadDashboardData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadStats(),
          this.loadTrainerOccupancyChart(),
          this.loadBatchEnrollmentsChart(),
          this.loadAttendanceTrendsChart()
        ])
      } catch (error) {
        this.error = 'Error loading dashboard data'
        console.error('Dashboard error:', error)
      } finally {
        this.loading = false
      }
    },

    async loadStats() {
      try {
        const [trainers, trainees, batches] = await Promise.all([
          api.getTrainers(),
          api.getTrainees(),
          api.getBatches()
        ])

        this.totalTrainers = trainers.data.length
        this.totalTrainees = trainees.data.length
        this.totalBatches = batches.data.length
        this.averageAttendance = 85 // Mock value
      } catch (error) {
        console.error('Error loading stats:', error)
      }
    },

    async loadTrainerOccupancyChart() {
      try {
        const response = await api.getTrainerOccupancy()
        const data = response.data

        // Mock data if API doesn't return proper format
        const chartData = data.length > 0 ? data : [
          { trainerName: 'Trainer A', availableDays: 20, occupiedDays: 15 },
          { trainerName: 'Trainer B', availableDays: 22, occupiedDays: 18 },
          { trainerName: 'Trainer C', availableDays: 18, occupiedDays: 12 },
          { trainerName: 'Trainer D', availableDays: 25, occupiedDays: 20 },
          { trainerName: 'Trainer E', availableDays: 15, occupiedDays: 10 }
        ]

        const ctx = this.$refs.trainerChart.getContext('2d')
        
        if (this.trainerChart) {
          this.trainerChart.destroy()
        }

        this.trainerChart = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: chartData.map(d => d.trainerName),
            datasets: [
              {
                label: 'Available Days',
                data: chartData.map(d => d.availableDays),
                backgroundColor: 'rgba(102, 126, 234, 0.6)',
                borderColor: 'rgba(102, 126, 234, 1)',
                borderWidth: 1
              },
              {
                label: 'Occupied Days',
                data: chartData.map(d => d.occupiedDays),
                backgroundColor: 'rgba(118, 75, 162, 0.6)',
                borderColor: 'rgba(118, 75, 162, 1)',
                borderWidth: 1
              }
            ]
          },
          options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
              y: {
                beginAtZero: true,
                title: {
                  display: true,
                  text: 'Number of Days'
                }
              }
            },
            plugins: {
              legend: {
                display: true,
                position: 'top'
              }
            }
          }
        })
      } catch (error) {
        console.error('Error loading trainer occupancy chart:', error)
      }
    },

    async loadBatchEnrollmentsChart() {
      try {
        const response = await api.getBatchEnrollments()
        const data = response.data

        // Mock data if API doesn't return proper format
        const chartData = data.length > 0 ? data : [
          { batchName: 'Java Full Stack - Mumbai', enrolledCount: 28 },
          { batchName: 'Python - Delhi', enrolledCount: 25 },
          { batchName: 'React - Bangalore', enrolledCount: 30 },
          { batchName: 'Angular - Hyderabad', enrolledCount: 22 },
          { batchName: 'DevOps - Chennai', enrolledCount: 20 }
        ]

        const ctx = this.$refs.batchChart.getContext('2d')
        
        if (this.batchChart) {
          this.batchChart.destroy()
        }

        this.batchChart = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: chartData.map(d => d.batchName),
            datasets: [{
              data: chartData.map(d => d.enrolledCount),
              backgroundColor: [
                'rgba(102, 126, 234, 0.8)',
                'rgba(118, 75, 162, 0.8)',
                'rgba(52, 211, 153, 0.8)',
                'rgba(251, 146, 60, 0.8)',
                'rgba(239, 68, 68, 0.8)'
              ],
              borderColor: [
                'rgba(102, 126, 234, 1)',
                'rgba(118, 75, 162, 1)',
                'rgba(52, 211, 153, 1)',
                'rgba(251, 146, 60, 1)',
                'rgba(239, 68, 68, 1)'
              ],
              borderWidth: 2
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
              legend: {
                display: true,
                position: 'bottom'
              }
            }
          }
        })
      } catch (error) {
        console.error('Error loading batch enrollments chart:', error)
      }
    },

    async loadAttendanceTrendsChart() {
      try {
        const response = await api.getAllAttendanceTrends()
        const data = response.data

        // Mock data if API doesn't return proper format
        const chartData = data.length > 0 ? data : [
          { session: 'Session 1', attendanceRate: 92 },
          { session: 'Session 2', attendanceRate: 88 },
          { session: 'Session 3', attendanceRate: 85 },
          { session: 'Session 4', attendanceRate: 90 },
          { session: 'Session 5', attendanceRate: 87 },
          { session: 'Session 6', attendanceRate: 89 },
          { session: 'Session 7', attendanceRate: 91 },
          { session: 'Session 8', attendanceRate: 86 }
        ]

        const ctx = this.$refs.attendanceChart.getContext('2d')
        
        if (this.attendanceChart) {
          this.attendanceChart.destroy()
        }

        this.attendanceChart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: chartData.map(d => d.session),
            datasets: [{
              label: 'Attendance Rate (%)',
              data: chartData.map(d => d.attendanceRate),
              backgroundColor: 'rgba(52, 211, 153, 0.2)',
              borderColor: 'rgba(52, 211, 153, 1)',
              borderWidth: 3,
              fill: true,
              tension: 0.4,
              pointRadius: 5,
              pointHoverRadius: 7,
              pointBackgroundColor: 'rgba(52, 211, 153, 1)'
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
              y: {
                beginAtZero: true,
                max: 100,
                title: {
                  display: true,
                  text: 'Attendance Percentage'
                }
              }
            },
            plugins: {
              legend: {
                display: true,
                position: 'top'
              }
            }
          }
        })
      } catch (error) {
        console.error('Error loading attendance trends chart:', error)
      }
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

.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  color: #2c3e50;
  margin-bottom: 2rem;
  font-size: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 3rem;
}

.stat-info h3 {
  margin: 0;
  font-size: 2rem;
  color: #667eea;
}

.stat-info p {
  margin: 0.25rem 0 0 0;
  color: #666;
  font-size: 0.9rem;
}

.chart-card {
  margin-bottom: 2rem;
}

.chart-card h3 {
  color: #667eea;
  margin-bottom: 0.5rem;
}

.chart-description {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.chart-container {
  position: relative;
  height: 400px;
  width: 100%;
}

.chart-container canvas {
  max-height: 400px;
}

@media (max-width: 768px) {
  h2 {
    font-size: 1.5rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-container {
    height: 300px;
  }
  
  .stat-icon {
    font-size: 2rem;
  }
  
  .stat-info h3 {
    font-size: 1.5rem;
  }
}
</style>