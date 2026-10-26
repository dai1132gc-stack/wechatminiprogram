<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>房源审核</span>
        <el-button :icon="Refresh" circle @click="table.fetchList" />
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
      <template #empty>
        <el-empty :image-size="80" description="暂无待审核房源" />
      </template>

      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="房源" min-width="220">
        <template #default="{ row }">
          <div class="house-cell">
            <span>{{ row.title }}</span>
            <span class="sub">{{ row.district }} · {{ row.address }} · {{ row.layout }} · {{ row.area }}m²</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="租金 元/月" width="110">
        <template #default="{ row }">￥{{ row.rent }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
          <el-button link type="success" @click="openAudit(row, 'APPROVED')">通过</el-button>
          <el-button link type="danger" @click="openAudit(row, 'REJECTED')">驳回</el-button>
        </template>
      </el-table-column>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="actionTitle" width="420px">
      <el-form label-position="top">
        <el-form-item :label="action === 'REJECTED' ? '驳回原因（必填）' : '备注（可选）'">
          <el-input
            v-model="remark"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            :placeholder="action === 'REJECTED' ? '房东将看到此原因，如：图片不清晰' : ''"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="action === 'REJECTED' ? 'danger' : 'primary'" :loading="submitting" @click="submit">
          确定{{ action === 'REJECTED' ? '驳回' : '通过' }}
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import PageTable from '@/components/PageTable/index.vue'
import { useTable } from '@/hooks/useTable'
import { auditHouse, pendingAuditHouses } from '@/api/house'
import type { HouseItem } from '@/types/api'

const router = useRouter()

const table = useTable<HouseItem, Record<string, never>>(pendingAuditHouses, {})
table.fetchList()

const dialogVisible = ref(false)
const submitting = ref(false)
const action = ref<'APPROVED' | 'REJECTED'>('APPROVED')
const remark = ref('')
const currentId = ref(0)

const actionTitle = computed(() => (action.value === 'REJECTED' ? '驳回房源' : '通过房源'))

function goDetail(id: number): void {
  router.push(`/house/detail/${id}`)
}

function openAudit(row: HouseItem, act: 'APPROVED' | 'REJECTED'): void {
  currentId.value = row.id
  action.value = act
  remark.value = ''
  dialogVisible.value = true
}

async function submit(): Promise<void> {
  if (action.value === 'REJECTED' && !remark.value.trim()) {
    ElMessage.warning('驳回时必须填写原因')
    return
  }
  submitting.value = true
  try {
    await auditHouse(currentId.value, { auditStatus: action.value, remark: remark.value.trim() })
    ElMessage.success(action.value === 'REJECTED' ? '已驳回' : '已通过')
    dialogVisible.value = false
    table.fetchList()
  } finally {
    submitting.value = false
  }
}

function formatTime(iso: string): string {
  return dayjs(iso).format('YYYY-MM-DD HH:mm')
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.house-cell {
  display: flex;
  flex-direction: column;
  .sub {
    font-size: 12px;
    color: #909399;
    margin-top: 2px;
  }
}
</style>
