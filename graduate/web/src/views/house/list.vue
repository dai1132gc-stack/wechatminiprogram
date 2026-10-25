<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>房源管理</span>
        <el-button type="primary" :icon="Plus" @click="$router.push('/house/edit')">发布房源</el-button>
      </div>
    </template>

    <!-- 查询区：点「查询」才请求，不做输入即查（设计方案 §2.4） -->
    <el-form inline :model="query" class="search-form" @submit.prevent>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="标题 / 地址" clearable @keyup.enter="fetchList" />
      </el-form-item>
      <el-form-item label="租赁状态">
        <el-select v-model="query.rentStatus" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="(label, value) in RENT_STATUS" :key="value" :label="label" :value="value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="fetchList">查询</el-button>
        <el-button :icon="RefreshRight" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="房源" min-width="220">
        <template #default="{ row }">
          <div class="house-cell">
            <span class="house-title">{{ row.title }}</span>
            <span class="house-sub">{{ row.district }} · {{ row.address }} · {{ row.layout }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="area" label="面积 m²" width="90" />
      <el-table-column label="租金 元/月" width="110">
        <template #default="{ row }">￥{{ row.rent }}</template>
      </el-table-column>
      <el-table-column label="审核 / 租赁状态" width="180">
        <template #default="{ row }">
          <!-- 两个状态并排展示（设计方案 §1.1） -->
          <el-tag :type="AUDIT_TAG[row.auditStatus]" size="small">{{ AUDIT_STATUS[row.auditStatus] }}</el-tag>
          <el-tag :type="RENT_TAG[row.rentStatus]" size="small" class="ml4">{{ RENT_STATUS[row.rentStatus] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最新甲醛 mg/m³" width="130">
        <template #default="{ row }">
          <span v-if="row.latestHcho != null" :class="{ exceeded: row.hchoExceeded }">
            {{ row.latestHcho.toFixed(3) }}{{ row.hchoExceeded ? ' ↑' : '' }}
          </span>
          <span v-else class="muted">无设备</span>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="发布时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pager"
      @current-change="fetchList"
      @size-change="fetchList"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { listHouses, type HouseQuery } from '@/api/house'
import type { HouseItem } from '@/types/api'

/** 枚举文案映射唯一来源 = 设计方案 §1.1；页面禁止硬编码文案 */
const AUDIT_STATUS: Record<string, string> = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
const RENT_STATUS: Record<string, string> = { AVAILABLE: '可租', LOCKED: '签约中', RENTED: '在租', OFFLINE: '已下架' }
const AUDIT_TAG: Record<string, 'warning' | 'success' | 'danger'> = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger',
}
const RENT_TAG: Record<string, 'success' | 'warning' | 'primary' | 'info'> = {
  AVAILABLE: 'success',
  LOCKED: 'warning',
  RENTED: 'primary',
  OFFLINE: 'info',
}

const loading = ref(false)
const rows = ref<HouseItem[]>([])
const total = ref(0)
const query = reactive<HouseQuery>({ keyword: '', rentStatus: undefined, pageNum: 1, pageSize: 10 })

async function fetchList(): Promise<void> {
  loading.value = true
  try {
    const data = await listHouses(query)
    rows.value = data?.list ?? []
    total.value = data?.total ?? 0
  } finally {
    loading.value = false
  }
}

function resetQuery(): void {
  query.keyword = ''
  query.rentStatus = undefined
  query.pageNum = 1
  fetchList()
}

/** 列表列时间格式：YYYY-MM-DD HH:mm（设计方案 §1.3） */
function formatDateTime(iso: string): string {
  return dayjs(iso).format('YYYY-MM-DD HH:mm')
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 4px;
}
.house-cell {
  .house-title {
    font-weight: 600;
  }
  .house-sub {
    display: block;
    font-size: 12px;
    color: #909399;
    margin-top: 2px;
  }
}
.exceeded {
  color: #a32d2d;
  font-weight: 700;
}
.muted {
  color: #c0c4cc;
}
.ml4 {
  margin-left: 4px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
