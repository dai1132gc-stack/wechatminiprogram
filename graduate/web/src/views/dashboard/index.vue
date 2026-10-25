<template>
  <div class="dashboard">
    <!-- 欢迎条 -->
    <el-card shadow="never" class="welcome-card">
      <div class="welcome">
        <el-avatar :size="48" class="welcome-avatar">{{ (userStore.userInfo?.nickname ?? '?').slice(0, 1) }}</el-avatar>
        <div>
          <div class="welcome-title">{{ greeting }}，{{ userStore.userInfo?.nickname ?? '' }}</div>
          <div class="welcome-sub">
            今日是 {{ today }}，{{ roleText }}。W1 骨架阶段：登录、布局、路由与权限菜单已就绪。
          </div>
        </div>
      </div>
    </el-card>

    <!-- ADMIN：统计卡片（GET /admin/stats/overview） -->
    <template v-if="isAdmin">
      <el-row :gutter="16" class="stat-row">
        <el-col v-for="card in statCards" :key="card.label" :xs="12" :sm="8" :md="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" :style="{ background: card.bg }">
              <el-icon :size="22"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-body">
              <div class="stat-value">{{ stats ? stats[card.field as keyof StatsOverviewData] : '-' }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never">
        <template #header>待办事项</template>
        <el-empty v-if="!stats || stats.housePendingAudit === 0" description="暂无待办" :image-size="80" />
        <div v-else class="todo-list">
          <el-button type="primary" plain @click="$router.push('/house/audit')">
            {{ stats.housePendingAudit }} 条房源待审核，去处理
          </el-button>
          <el-button v-if="stats.alarmActive > 0" type="danger" plain @click="$router.push('/env/alarm')">
            {{ stats.alarmActive }} 条告警待处置，去查看
          </el-button>
        </div>
      </el-card>
    </template>

    <!-- LANDLORD：快捷入口 -->
    <template v-else>
      <el-row :gutter="16">
        <el-col v-for="entry in landlordEntries" :key="entry.path" :xs="12" :md="8">
          <el-card shadow="hover" class="entry-card" @click="$router.push(entry.path)">
            <el-icon :size="26" color="#185fa5"><component :is="entry.icon" /></el-icon>
            <div class="entry-title">{{ entry.title }}</div>
            <div class="entry-sub">{{ entry.sub }}</div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { useUserStore } from '@/store/modules/user'
import { getStatsOverview } from '@/api/stat'
import type { StatsOverviewData } from '@/types/api'

dayjs.locale('zh-cn')

const userStore = useUserStore()
const stats = ref<StatsOverviewData | null>(null)

const isAdmin = computed(() => userStore.userInfo?.role === 'ADMIN')
const today = dayjs().format('YYYY年MM月DD日 dddd')
const greeting = computed(() => {
  const hour = dayjs().hour()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})
const roleText = computed(() => (isAdmin.value ? '系统管理员' : '房东'))

const statCards = [
  { field: 'userTotal', label: '用户总数', icon: 'User', bg: '#e4eff8' },
  { field: 'houseTotal', label: '房源总数', icon: 'House', bg: '#e8f5e9' },
  { field: 'houseAvailable', label: '可租房源', icon: 'Search', bg: '#fff7e6' },
  { field: 'housePendingAudit', label: '待审核房源', icon: 'Stamp', bg: '#fdeaea' },
  { field: 'contractEffective', label: '生效合同', icon: 'Document', bg: '#e4eff8' },
  { field: 'deviceOnline', label: '在线设备', icon: 'Monitor', bg: '#e8f5e9' },
  { field: 'alarmActive', label: '未处理告警', icon: 'Bell', bg: '#fdeaea' },
  { field: 'newUsers7d', label: '近7日新增用户', icon: 'TrendCharts', bg: '#fff7e6' },
]

const landlordEntries = [
  { path: '/house/list', title: '房源管理', sub: '发布、编辑、上下架', icon: 'House' },
  { path: '/application/list', title: '租赁申请', sub: '查看与审批租客申请', icon: 'Tickets' },
  { path: '/contract/list', title: '合同管理', sub: '签署与退租处理', icon: 'Document' },
]

onMounted(async () => {
  if (isAdmin.value) {
    stats.value = await getStatsOverview()
  }
})
</script>

<style scoped lang="scss">
.welcome-card {
  margin-bottom: 16px;
  .welcome {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .welcome-avatar {
    background: var(--brand-gradient);
    font-size: 20px;
    flex-shrink: 0;
  }
  .welcome-title {
    font-size: 18px;
    font-weight: 600;
  }
  .welcome-sub {
    margin-top: 4px;
    color: #909399;
    font-size: 13px;
  }
}
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  margin-bottom: 16px;
  :deep(.el-card__body) {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 18px;
  }
}
.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #185fa5;
  flex-shrink: 0;
}
.stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.2;
}
.stat-label {
  font-size: 12px;
  color: #909399;
}
.todo-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.entry-card {
  margin-bottom: 16px;
  cursor: pointer;
  text-align: center;
  :deep(.el-card__body) {
    padding: 24px 12px;
  }
  .entry-title {
    margin-top: 10px;
    font-weight: 600;
  }
  .entry-sub {
    margin-top: 4px;
    font-size: 12px;
    color: #909399;
  }
}
</style>
