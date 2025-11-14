import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  // Trainer APIs
  getTrainers() {
    return api.get('/trainers')
  },
  
  getTrainerById(id) {
    return api.get(`/trainers/${id}`)
  },
  
  addTrainerAvailability(data) {
    return api.post('/trainers/availability', data)
  },
  
  updateTrainer(id, data) {
    return api.put(`/trainers/${id}`, data)
  },
  
  deleteTrainer(id) {
    return api.delete(`/trainers/${id}`)
  },
  
  getTrainerAvailability(trainerId) {
    return api.get(`/trainers/${trainerId}/availability`)
  },

  // Trainee APIs
  getTrainees() {
    return api.get('/trainees')
  },
  
  getTraineeById(id) {
    return api.get(`/trainees/${id}`)
  },
  
  enrollTrainee(data) {
    return api.post('/trainees/enroll', data)
  },
  
  updateTrainee(id, data) {
    return api.put(`/trainees/${id}`, data)
  },
  
  deleteTrainee(id) {
    return api.delete(`/trainees/${id}`)
  },
  
  getTraineesByBatch(batchId) {
    return api.get(`/trainees/batch/${batchId}`)
  },

  // Attendance APIs
  markAttendance(data) {
    return api.post('/attendance/mark', data)
  },
  
  updateAttendance(id, data) {
    return api.put(`/attendance/${id}`, data)
  },
  
  deleteAttendance(id) {
    return api.delete(`/attendance/${id}`)
  },
  
  getAttendanceByTrainee(traineeId) {
    return api.get(`/attendance/trainee/${traineeId}`)
  },
  
  getAttendanceByBatch(batchId) {
    return api.get(`/attendance/batch/${batchId}`)
  },
  
  getAllAttendance() {
    return api.get('/attendance')
  },

  // Batch APIs
  getBatches() {
    return api.get('/batches')
  },
  
  getBatchById(id) {
    return api.get(`/batches/${id}`)
  },
  
  createBatch(data) {
    return api.post('/batches', data)
  },
  
  updateBatch(id, data) {
    return api.put(`/batches/${id}`, data)
  },
  
  deleteBatch(id) {
    return api.delete(`/batches/${id}`)
  },
  
  getBatchDetails(batchId) {
    return api.get(`/batches/${batchId}/details`)
  },
  
  getBatchTrainees(batchId) {
    return api.get(`/batches/${batchId}/trainees`)
  },

  // Course APIs
  getCourses() {
    return api.get('/courses')
  },
  
  getCourseById(id) {
    return api.get(`/courses/${id}`)
  },
  
  createCourse(data) {
    return api.post('/courses', data)
  },
  
  updateCourse(id, data) {
    return api.put(`/courses/${id}`, data)
  },
  
  deleteCourse(id) {
    return api.delete(`/courses/${id}`)
  },

  // Reports APIs
  getTrainerOccupancy() {
    return api.get('/reports/trainer-occupancy')
  },
  
  getBatchEnrollments() {
    return api.get('/reports/batch-enrollments')
  },
  
  getAttendanceTrends(batchId) {
    return api.get(`/reports/attendance-trends/${batchId}`)
  },
  
  getAllAttendanceTrends() {
    return api.get('/reports/attendance-trends')
  }
}