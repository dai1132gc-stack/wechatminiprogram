<template>
  <!-- 单子路由（或无 children）：直接渲染菜单项 -->
  <el-menu-item v-if="visibleChildren.length <= 1" :index="indexPath">
    <el-icon v-if="icon"><component :is="icon" /></el-icon>
    <template #title>{{ title }}</template>
  </el-menu-item>
  <!-- 分组：渲染子菜单 -->
  <el-sub-menu v-else :index="route.path">
    <template #title>
      <el-icon v-if="icon"><component :is="icon" /></el-icon>
      <span>{{ title }}</span>
    </template>
    <el-menu-item
      v-for="child in visibleChildren"
      :key="child.path"
      :index="`${route.path}/${child.path}`.replace(/\/+/g, '/')"
    >
      {{ child.meta?.title }}
    </el-menu-item>
  </el-sub-menu>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

const props = defineProps({
  route: { type: Object as PropType<RouteRecordRaw>, required: true },
  basePath: { type: String, required: true },
})

const visibleChildren = computed(() => (props.route.children ?? []).filter((c) => !c.meta?.hidden))

/** 单子路由时直接指向子路由完整路径 */
const indexPath = computed(() => {
  if (visibleChildren.value.length === 1) {
    return `${props.basePath}/${visibleChildren.value[0].path}`.replace(/\/+/g, '/')
  }
  return props.basePath
})

const title = computed(() => {
  if (visibleChildren.value.length === 1) {
    return visibleChildren.value[0].meta?.title ?? ''
  }
  return props.route.meta?.title ?? ''
})

const icon = computed(() => props.route.meta?.icon ?? visibleChildren.value[0]?.meta?.icon)
</script>
