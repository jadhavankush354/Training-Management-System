import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import TrainerAvailability from '../components/TrainerAvailability.vue'
import TraineeEnrollment from '../components/TraineeEnrollment.vue'
import AttendanceMarking from '../components/AttendanceMarking.vue'
import BatchManagement from '../components/BatchManagement.vue'
import CourseManagement from '../components/CourseManagement.vue'
import DashboardView from '../components/DashboardView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/trainer-availability',
    name: 'TrainerAvailability',
    component: TrainerAvailability
  },
  {
    path: '/trainee-enrollment',
    name: 'TraineeEnrollment',
    component: TraineeEnrollment
  },
  {
    path: '/attendance',
    name: 'AttendanceMarking',
    component: AttendanceMarking
  },
  {
    path: '/batches',
    name: 'BatchManagement',
    component: BatchManagement
  },
  {
    path: '/batch-management',
    redirect: '/batches'
  },
  {
    path: '/courses',
    name: 'CourseManagement',
    component: CourseManagement
  },
  {
    path: '/dashboard',
    name: 'DashboardView',
    component: DashboardView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router