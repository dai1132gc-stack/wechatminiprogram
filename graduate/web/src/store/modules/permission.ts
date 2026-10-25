import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'
import { asyncRoutes } from '@/router/routes'

/**
 * 动态路由：按角色过滤 asyncRoutes（meta.roles）。
 * 注意：LANDLORD「仅自己/本机构」的数据过滤由后端 @DataScope 完成，
 * 前端只负责不显示 ADMIN 专属菜单（设计方案 §2.1）。
 */
export const usePermissionStore = defineStore('permission', () => {
  function canAccess(roles: string[], metaRoles?: string[]): boolean {
    return !metaRoles || metaRoles.length === 0 || metaRoles.some((r) => roles.includes(r))
  }

  function filterRoutes(roles: string[]): RouteRecordRaw[] {
    return asyncRoutes
      .filter((route) => canAccess(roles, route.meta?.roles))
      .map((route) => {
        if (!route.children) return route
        const children = route.children.filter((child) => canAccess(roles, child.meta?.roles))
        // vue-router 的 RouteRecordRaw 是判别联合，展开后需显式断言回原类型
        return { ...route, children } as RouteRecordRaw
      })
  }

  return { filterRoutes }
})
