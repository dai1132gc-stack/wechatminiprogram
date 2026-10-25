import { createRouter, createWebHistory } from 'vue-router'
import { constantRoutes } from '@/router/routes'
import { setupRouterGuard } from '@/router/guard'

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
})

setupRouterGuard(router)

export default router
