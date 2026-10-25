<template>
  <header class="navbar">
    <div class="navbar-left">
      <el-icon class="collapse-btn" :size="18" @click="appStore.toggleSidebar()">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <span class="system-title">房屋租赁与环境监测管理平台</span>
    </div>

    <div class="navbar-right">
      <el-badge :value="alarmCount" :hidden="alarmCount === 0" :max="99" class="alarm-badge">
        <el-icon :size="18" class="alarm-bell" @click="$router.push('/env/alarm')">
          <Bell />
        </el-icon>
      </el-badge>

      <el-dropdown @command="handleCommand">
        <span class="user-entry">
          <el-avatar :size="30" class="user-avatar">{{ avatarText }}</el-avatar>
          <span class="user-name">{{ userStore.userInfo?.nickname ?? '-' }}</span>
          <span class="user-role">{{ roleLabel }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="password">修改密码</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { getActiveAlarmCount } from '@/api/stat'
import { logout } from '@/api/auth'
import { getRefreshToken } from '@/utils/auth'
import { resetRouterState } from '@/router/guard'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const alarmCount = ref(0)
let pollTimer: ReturnType<typeof setInterval> | null = null

const roleLabel = computed(() => {
  const map: Record<string, string> = { ADMIN: '管理员', LANDLORD: '房东', TENANT: '租客' }
  return map[userStore.userInfo?.role ?? ''] ?? ''
})

const avatarText = computed(() => (userStore.userInfo?.nickname ?? '?').slice(0, 1))

/** 铃铛 30 秒轮询（设计方案 §2.3）；W1 Mock 阶段返回固定值 */
async function pollAlarmCount(): Promise<void> {
  try {
    const data = await getActiveAlarmCount()
    alarmCount.value = data?.count ?? 0
  } catch {
    alarmCount.value = 0
  }
}

async function handleCommand(command: string): Promise<void> {
  if (command === 'logout') {
    try {
      const rt = getRefreshToken()
      if (rt) await logout({ refreshToken: rt })
    } catch {
      // 登出接口失败不阻塞本地清理
    }
    userStore.reset()
    resetRouterState(router)
    ElMessage.success('已退出登录')
    router.push('/login')
    return
  }
  ElMessage.info(command === 'profile' ? '个人信息页 W2 实现' : '修改密码 W2 实现')
}

onMounted(() => {
  pollAlarmCount()
  pollTimer = setInterval(pollAlarmCount, 30_000)
})
onBeforeUnmount(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped lang="scss">
.navbar {
  height: var(--header-height);
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.navbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  .collapse-btn {
    cursor: pointer;
    color: #606266;
  }
  .system-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}
.navbar-right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.alarm-bell {
  cursor: pointer;
  color: #606266;
}
.user-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  .user-avatar {
    background: var(--brand-gradient);
    font-size: 14px;
  }
  .user-name {
    font-size: 14px;
    color: #303133;
  }
  .user-role {
    font-size: 12px;
    color: #909399;
  }
}
</style>
