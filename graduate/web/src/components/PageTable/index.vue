<template>
  <div class="page-table">
    <!-- 查询区：点「查询」触发，不做输入即查 -->
    <div v-if="$slots.search" class="search-area">
      <slot name="search" />
    </div>

    <!-- 工具栏 -->
    <div v-if="$slots.toolbar" class="toolbar">
      <div class="toolbar-left"><slot name="toolbar" /></div>
      <div class="toolbar-right"><slot name="toolbar-right" /></div>
    </div>

    <el-table v-loading="loading" :data="rows" stripe>
      <slot />
      <template v-if="$slots.empty" #empty>
        <slot name="empty" />
      </template>
    </el-table>

    <el-pagination
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pager"
      @current-change="$emit('page-change', $event)"
      @size-change="$emit('size-change', $event)"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 表格 + 分页 + 查询区一体化封装（设计方案 §2.4）：
 * 列由页面通过默认插槽传入（el-table-column），状态列配合 StatusTag。
 */
defineProps<{
  rows: unknown[]
  total: number
  loading: boolean
  pageNum: number
  pageSize: number
}>()

defineEmits<{
  'page-change': [pageNum: number]
  'size-change': [pageSize: number]
}>()
</script>

<style scoped lang="scss">
.search-area {
  margin-bottom: 12px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
