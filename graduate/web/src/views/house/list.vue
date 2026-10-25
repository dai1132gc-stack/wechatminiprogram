<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>房源管理</span>
        <el-button type="primary" :icon="Plus" @click="$router.push('/house/edit')">发布房源</el-button>
      </div>
    </template>

    <PageTable
      :rows="table.rows.value"
      :total="table.total.value"
      :loading="table.loading.value"
      :page-num="table.query.pageNum"
      :page-size="table.query.pageSize"
      @page-change="table.onPageChange"
      @size-change="table.onSizeChange"
    >
      <template #search>
        <el-form inline @submit.prevent="table.search">
          <el-form-item label="关键词">
            <el-input v-model="table.query.keyword" placeholder="标题 / 地址" clearable @keyup.enter="table.search" />
          </el-form-item>
          <el-form-item label="租赁状态">
            <el-select v-model="table.query.rentStatus" placeholder="全部" clearable style="width: 140px">
              <el-option label="可租" value="AVAILABLE" />
              <el-option label="签约中" value="LOCKED" />
              <el-option label="在租" value="RENTED" />
              <el-option label="已下架" value="OFFLINE" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="table.search">查询</el-button>
            <el-button :icon="RefreshRight" @click="table.reset">重置</el-button>
          </el-form-item>
        </el-form>
      </template>

      <template #empty>
        <el-empty
          :image-size="80"
          :description="table.query.keyword || table.query.rentStatus ? '无匹配结果，换个条件试试' : '暂无房源'"
        />
      </template>

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
          <!-- 两个状态并排展示（设计方案 §1.1），文案颜色唯一来源 StatusTag -->
          <StatusTag kind="auditStatus" :value="row.auditStatus" />
          <StatusTag kind="rentStatus" :value="row.rentStatus" />
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
      <el-table-column prop="createdAt" label="发布时间" width="120">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
    </PageTable>
  </el-card>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import PageTable from '@/components/PageTable/index.vue'
import StatusTag from '@/components/StatusTag/index.vue'
import { useTable } from '@/hooks/useTable'
import { listHouses, type HouseQuery } from '@/api/house'
import type { HouseItem } from '@/types/api'

const table = useTable<HouseItem, HouseQuery>(listHouses, { keyword: '', rentStatus: undefined })

/** 列表列时间格式：YYYY-MM-DD HH:mm（设计方案 §1.3） */
function formatDateTime(iso: string): string {
  return dayjs(iso).format('YYYY-MM-DD HH:mm')
}

table.fetchList()
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
</style>
