<template>
  <el-container class="layout-root">
    <Sidebar />
    <el-container class="main-container">
      <Navbar />
      <el-main class="content">
        <breadcrumb class="content-breadcrumb" />
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
import Breadcrumb from './Breadcrumb.vue'
</script>

<style scoped lang="scss">
.main-container {
  flex-direction: column;
  min-width: 0;
}
.content {
  padding: 16px 20px;
  overflow-y: auto;
}
.content-breadcrumb {
  margin-bottom: 12px;
}
</style>
