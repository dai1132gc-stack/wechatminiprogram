import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from 'vite-plugin-mock'

/**
 * W2 起：认证走真实后端（/api/v1 → 8080），Mock 中间件只拦在 mock/ 里
 * 仍注册的接口（house / stat，对应 Controller 未实现，W3+ 逐个替换后删除）。
 * Mock 拦截优先于 proxy；全部替换完成后删除 viteMockServe 即可。
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
    proxy: {
      '/api/v1': { target: 'http://127.0.0.1:8080', changeOrigin: true },
    },
  },
}))
