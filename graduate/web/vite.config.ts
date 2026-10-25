import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from 'vite-plugin-mock'

/**
 * W1 阶段：Mock 在 dev server 生效（vite-plugin-mock v3 用 enable 控制）。
 * 切真接口时：删除 viteMockServe、放开 server.proxy，前端代码零改动（契约一致）。
 */
export default defineConfig(({ command }) => ({
  plugins: [
    vue(),
    viteMockServe({
      mockPath: 'mock',
      enable: command === 'serve',
      logger: false,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    host: '127.0.0.1',
    port: 5180,
    strictPort: true,
    // 接后端时启用（技术方案 §8）：
    // proxy: {
    //   '/api/v1': { target: 'http://localhost:8080', changeOrigin: true },
    // },
  },
}))
