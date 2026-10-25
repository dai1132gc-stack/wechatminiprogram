import { defineStore } from 'pinia'
import { ref } from 'vue'

/** 应用状态：侧边栏折叠（主题、字典缓存后续按需追加） */
export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)

  function toggleSidebar(): void {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return { sidebarCollapsed, toggleSidebar }
})
