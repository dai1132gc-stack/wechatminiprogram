<template>
  <el-tag :type="tagType" size="small" class="status-tag">{{ text }}</el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

/**
 * 状态标签：文案与颜色的唯一来源是《设计方案》§1.1 枚举对照表，
 * 页面禁止硬编码状态文案。两个状态并排展示时用两个 StatusTag。
 */
const props = defineProps<{
  /** 枚举类别 */
  kind:
    | 'auditStatus'
    | 'rentStatus'
    | 'contractStatus'
    | 'applicationStatus'
    | 'appointmentStatus'
    | 'deviceStatus'
    | 'alarmLevel'
    | 'alarmStatus'
    | 'userStatus'
  /** 枚举值（UPPER_SNAKE） */
  value: string
}>()

interface Meta {
  text: string
  /** Element tag 类型：success / warning / danger / info / primary */
  type: 'success' | 'warning' | 'danger' | 'info' | 'primary'
}

const MAPPING: Record<string, Record<string, Meta>> = {
  auditStatus: {
    PENDING: { text: '待审核', type: 'warning' },
    APPROVED: { text: '已通过', type: 'success' },
    REJECTED: { text: '已驳回', type: 'danger' },
  },
  rentStatus: {
    AVAILABLE: { text: '可租', type: 'success' },
    LOCKED: { text: '签约中', type: 'warning' },
    RENTED: { text: '在租', type: 'primary' },
    OFFLINE: { text: '已下架', type: 'info' },
  },
  contractStatus: {
    DRAFT: { text: '草稿', type: 'info' },
    PENDING_SIGN: { text: '待签署', type: 'warning' },
    EFFECTIVE: { text: '生效中', type: 'success' },
    EXPIRED: { text: '已到期', type: 'info' },
    TERMINATED: { text: '已退租', type: 'info' },
    CANCELLED: { text: '已取消', type: 'info' },
  },
  applicationStatus: {
    SUBMITTED: { text: '待处理', type: 'warning' },
    APPROVED: { text: '已通过', type: 'success' },
    REJECTED: { text: '已拒绝', type: 'danger' },
    CANCELLED: { text: '已取消', type: 'info' },
  },
  appointmentStatus: {
    PENDING: { text: '待确认', type: 'warning' },
    CONFIRMED: { text: '已确认', type: 'success' },
    COMPLETED: { text: '已完成', type: 'primary' },
    CANCELLED: { text: '已取消', type: 'info' },
  },
  deviceStatus: {
    ONLINE: { text: '在线', type: 'success' },
    OFFLINE: { text: '离线', type: 'info' },
    UNBOUND: { text: '未绑定', type: 'warning' },
    FAULT: { text: '故障', type: 'danger' },
  },
  alarmLevel: {
    WARN: { text: '预警', type: 'warning' },
    DANGER: { text: '严重', type: 'danger' },
  },
  alarmStatus: {
    ACTIVE: { text: '待处理', type: 'danger' },
    RECOVERED: { text: '已恢复', type: 'success' },
    HANDLED: { text: '已处置', type: 'primary' },
    IGNORED: { text: '已忽略', type: 'info' },
  },
  userStatus: {
    ACTIVE: { text: '正常', type: 'success' },
    DISABLED: { text: '已禁用', type: 'danger' },
  },
}

const meta = computed<Meta>(
  () => MAPPING[props.kind]?.[props.value] ?? { text: props.value ?? '-', type: 'info' },
)
const text = computed(() => meta.value.text)
const tagType = computed(() => meta.value.type)
</script>

<style scoped>
.status-tag {
  margin-right: 4px;
}
</style>
