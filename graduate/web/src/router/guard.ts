import type { Router } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import { asyncRoutes } from '@/router/routes'
import { getCurrentUser } from '@/api/user'

/**
 * 路由守卫（设计方案 §2.2）：
 * ① 白名单（/login、/404）放行
 * ② 无 token → 跳 /login?redirect=
 * ③ 有 token 无 userInfo → 拉 /users/me → 按角色过滤动态路由 addRoute → 重放当前跳转
 * ④ 校验 to.meta.roles，不含 → /404
 *
 * 纵深防御：即使旧会话的动态路由未清干净，第 ④ 步的角色校验也会兜底送 /404。
 */
let dynamicRoutesAdded = false

export function setupRouterGuard(router: Router): void {
  router.beforeEach(async (to, _from, next) => {
    const userStore = useUserStore()

    if (to.meta.public) {
      next()
      return
    }
    if (!userStore.isLoggedIn) {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      return
    }
    if (!dynamicRoutesAdded || !userStore.userInfo) {
      try {
        const info = await getCurrentUser()
        if (!info) throw new Error('用户信息获取失败')
        userStore.setUserInfo(info)
      } catch {
        userStore.reset()
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
        return
      }
      const permissionStore = usePermissionStore()
      permissionStore
        .filterRoutes(userStore.roles)
        .forEach((route) => router.addRoute(route))
      dynamicRoutesAdded = true
      next({ ...to, replace: true })
      return
    }
    const roles = to.meta.roles
    if (roles && roles.length > 0 && !roles.some((r) => userStore.roles.includes(r as never))) {
      next('/404')
      return
    }
    next()
  })
}

/** 退出登录 / 切换账号前调用：移除已注册的动态路由并复位标志位 */
export function resetRouterState(router: Router): void {
  const registeredNames = new Set(router.getRoutes().map((r) => r.name))
  const asyncNames = new Set<string>()
  asyncRoutes.forEach((route) => {
    route.children?.forEach((child) => child.name && asyncNames.add(String(child.name)))
    if (route.name) asyncNames.add(String(route.name))
  })
  asyncNames.forEach((name) => {
    if (registeredNames.has(name)) router.removeRoute(name)
  })
  dynamicRoutesAdded = false
}
