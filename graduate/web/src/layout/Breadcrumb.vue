<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in items" :key="item.path">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

/** 由 route.matched 生成，最后一级不可点（设计方案 §2.3） */
const items = computed(() =>
  route.matched
    .filter((r) => r.meta?.title && r.path !== '/')
    .map((r) => ({ path: r.path, title: r.meta.title as string })),
)
</script>
