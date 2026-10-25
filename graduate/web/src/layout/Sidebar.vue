<template>
  <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="sidebar">
    <div class="brand">
      <el-icon :size="24"><Monitor /></el-icon>
      <span v-show="!appStore.sidebarCollapsed" class="brand-name">租赁环境监测平台</span>
    </div>
    <el-menu
      :default-active="route.path"
      :collapse="appStore.sidebarCollapsed"
      :collapse-transition="false"
      router
      class="sidebar-menu"
    >
      <sidebar-item v-for="menuRoute in menus" :key="menuRoute.path" :route="menuRoute" :base-path="menuRoute.path" />
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

/** 侧边栏菜单：动态路由过滤角色 + 剔除 hidden；首页（path '/'）单独置顶 */
const menus = computed(() => {
  const routes = permissionStore.filterRoutes(userStore.roles)
  const root = routes.find((r) => r.path === '/')
  const groups = routes.filter((r) => r.path !== '/' && !r.meta?.hidden)
  const home = root?.children?.map((c) => ({
    ...c,
    path: `/dashboard`,
    meta: { ...c.meta, icon: 'Odometer' },
  })) ?? []
  return [...home, ...groups]
})
</script>

<style scoped lang="scss">
.sidebar {
  background: #001529;
  transition: width 0.2s;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.brand {
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-weight: 600;
  .brand-name {
    font-size: 15px;
    white-space: nowrap;
  }
}
.sidebar-menu {
  border-right: none;
  background: #001529;
  flex: 1;
  overflow-y: auto;
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: rgba(255, 255, 255, 0.68);
    &:hover {
      background: rgba(255, 255, 255, 0.06);
      color: #fff;
    }
  }
  :deep(.el-menu-item.is-active) {
    background: var(--el-color-primary);
    color: #fff;
  }
}
</style>
