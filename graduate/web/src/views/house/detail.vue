<template>
  <el-card v-loading="loading" shadow="never">
    <template #header>
      <div class="card-header">
        <span>房源详情</span>
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </template>

    <template v-if="house">
      <div class="title-row">
        <h2 class="title">{{ house.title }}</h2>
        <div class="tags">
          <StatusTag kind="auditStatus" :value="house.auditStatus" />
          <StatusTag kind="rentStatus" :value="house.rentStatus" />
        </div>
      </div>
      <div class="sub">{{ house.district }}{{ house.street ? ' · ' + house.street : '' }} · {{ house.address }}</div>

      <el-descriptions :column="3" border class="desc">
        <el-descriptions-item label="户型">{{ house.layout }}</el-descriptions-item>
        <el-descriptions-item label="面积">{{ house.area }} m²</el-descriptions-item>
        <el-descriptions-item label="租金">￥{{ house.rent }}/月</el-descriptions-item>
        <el-descriptions-item label="押金">￥{{ house.deposit }}</el-descriptions-item>
        <el-descriptions-item label="楼层">
          {{ house.floor ?? '-' }} / {{ house.totalFloor ?? '-' }} 层
        </el-descriptions-item>
        <el-descriptions-item label="朝向">{{ house.orientation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="装修">{{ house.decoration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备">
          <template v-if="house.device">
            <StatusTag kind="deviceStatus" :value="house.device.status" />
            {{ house.device.name }}
          </template>
          <span v-else class="muted">未绑定</span>
        </el-descriptions-item>
        <el-descriptions-item label="最新甲醛">
          <span v-if="house.latestHcho != null" :class="{ exceeded: house.hchoExceeded }">
            {{ house.latestHcho.toFixed(3) }} mg/m³{{ house.hchoExceeded ? ' ↑' : '' }}
          </span>
          <span v-else class="muted">无数据</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-descriptions v-if="house.landlord" :column="2" border class="desc" title="房东信息（仅本人/管理员可见）">
        <el-descriptions-item label="昵称">{{ house.landlord.nickname }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ house.landlord.phone }}</el-descriptions-item>
      </el-descriptions>

      <el-alert
        v-if="house.auditStatus === 'REJECTED' && house.auditRemark"
        :title="`驳回原因：${house.auditRemark}`"
        type="error"
        :closable="false"
        class="remark"
      />

      <div v-if="house.description" class="section-title">房源描述</div>
      <p class="description">{{ house.description }}</p>
    </template>
    <el-empty v-else-if="!loading" description="房源不存在或无权访问" />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import StatusTag from '@/components/StatusTag/index.vue'
import { getHouse, type HouseDetail } from '@/api/house'

const route = useRoute()
const house = ref<HouseDetail | null>(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    house.value = await getHouse(route.params.id as string)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  .title {
    margin: 0;
    font-size: 20px;
  }
}
.sub {
  color: #909399;
  font-size: 13px;
  margin: 8px 0 16px;
}
.desc {
  margin-bottom: 16px;
}
.remark {
  margin-bottom: 16px;
}
.section-title {
  font-weight: 600;
  margin: 12px 0 8px;
}
.description {
  color: #606266;
  line-height: 1.8;
  white-space: pre-wrap;
}
.exceeded {
  color: #a32d2d;
  font-weight: 700;
}
.muted {
  color: #c0c4cc;
}
</style>
