/**
 * Mock 公共工具：统一响应包装必须与技术方案 §6.1 完全一致，
 * 否则切真接口时全要改。所有 mock 文件只通过这里返回数据。
 */

export interface MockOption {
  url: unknown
  /** vite-plugin-mock v3 传入的 body 已是对象；兼容字符串以防版本差异 */
  body: unknown
  query: Record<string, string>
  headers: Record<string, string>
}

export interface MockItem {
  url: string
  method?: 'get' | 'post' | 'put' | 'delete'
  timeout?: number
  response: (option: MockOption) => unknown
}

let traceCounter = 0

function traceId(): string {
  traceCounter += 1
  return `mock${Date.now().toString(16)}${traceCounter.toString(16).padStart(4, '0')}`
}

/** 统一成功信封 */
export function ok<T>(data: T | null = null): { code: number; message: string; data: T | null; traceId: string; timestamp: number } {
  return { code: 0, message: 'ok', data, traceId: traceId(), timestamp: Date.now() }
}

/** 业务失败信封：HTTP 保持 200，前端按 code 分派（设计方案 §1.4） */
export function fail(code: number, message: string): { code: number; message: string; data: null; traceId: string; timestamp: number } {
  return { code, message, data: null, traceId: traceId(), timestamp: Date.now() }
}

/** 分页包装：字段固定 list/total/pageNum/pageSize/pages */
export function page<T>(list: T[], pageNum: number, pageSize: number): {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
} {
  return {
    list,
    total: list.length,
    pageNum,
    pageSize,
    pages: Math.max(1, Math.ceil(list.length / pageSize)),
  }
}

/** 对完整数组做真分页切片（列表 mock 用） */
export function slicePage<T>(all: T[], pageNum: number, pageSize: number): {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
} {
  const total = all.length
  const pages = Math.max(1, Math.ceil(total / pageSize))
  const start = (pageNum - 1) * pageSize
  return { list: all.slice(start, start + pageSize), total, pageNum, pageSize, pages }
}

export function parseBody<T>(body: unknown): T {
  if (body && typeof body === 'object') return body as T
  if (typeof body === 'string' && body.length > 0) {
    try {
      return JSON.parse(body) as T
    } catch {
      return {} as T
    }
  }
  return {} as T
}

/** ISO-8601 带时区（模拟后端时间格式） */
export function iso(minutesAgo: number): string {
  const d = new Date(Date.now() - minutesAgo * 60_000)
  const pad = (n: number, w = 2) => String(n).padStart(w, '0')
  return (
    `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}` +
    `T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}+08:00`
  )
}

/** 演示异常注入（设计方案 §0.3）：?mock=error 返回 90001，?mock=slow 延迟 3 秒 */
export function injectedError(option: MockOption): { code: number; message: string; data: null } | null {
  if (option.query.mock === 'error') {
    return fail(90001, '系统内部错误，请联系管理员')
  }
  return null
}
