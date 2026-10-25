<template>
  <div class="not-found">
    <el-result icon="warning" title="404" sub-title="页面不存在或无权访问">
      <template #extra>
        <el-button type="primary" @click="$router.push('/dashboard')">返回工作台</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.not-found {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
</style>
