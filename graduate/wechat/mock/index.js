/**
 * Mock 分发器：request.js 在 useMock=true 时把请求转到这里。
 * 响应体与技术方案 §6.1 / openapi.yaml 完全一致，切换真实后端零改动。
 */
const { dispatch: dispatchAuth } = require('./handlers/auth')
const { dispatch: dispatchUser } = require('./handlers/user')
const { dispatch: dispatchHouse } = require('./handlers/house')

let counter = 0

function traceId() {
  counter += 1
  return `wxmock${Date.now().toString(16)}${counter}`
}

function ok(data = null) {
  return { code: 0, message: 'ok', data, traceId: traceId(), timestamp: Date.now() }
}

function fail(code, message) {
  return { code, message, data: null, traceId: traceId(), timestamp: Date.now() }
}

const handlers = [dispatchAuth, dispatchUser, dispatchHouse]

function dispatch(method, urlWithQuery, data = {}, header = {}) {
  const [path, queryString] = String(urlWithQuery).split('?')
  const query = {}
  if (queryString) {
    queryString.split('&').forEach((pair) => {
      const [k, v] = pair.split('=')
      if (k) query[decodeURIComponent(k)] = decodeURIComponent(v || '')
    })
  }
  const ctx = { method: method.toUpperCase(), path, query, body: data, header }

  const result = handlers.reduce((acc, handler) => acc || handler(ctx, { ok, fail }), null)
  const envelope = result || fail(10003, `Mock 未实现：${method} ${path}`)

  // 模拟网络延迟，让 loading 态真实可见
  return new Promise((resolve) => setTimeout(() => resolve(envelope), 200))
}

module.exports = { dispatch, ok, fail }
