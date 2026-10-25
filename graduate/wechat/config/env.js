/**
 * 三套环境（develop / trial / release）自动切换 + Mock 总开关。
 * 上线前把 useMock 置 false，并配置合法域名（request 合法域名 = apiBase 对应域名）。
 */
const envs = {
  // 开发者工具 / 真机调试
  develop: {
    apiBase: 'http://127.0.0.1:8080/api/v1',
    useMock: true,
  },
  // 体验版
  trial: {
    apiBase: 'https://trial-api.example.com/api/v1',
    useMock: false,
  },
  // 正式版
  release: {
    apiBase: 'https://api.example.com/api/v1',
    useMock: false,
  },
}

function currentEnv() {
  const { envVersion } = wx.getAccountInfoSync().miniProgram
  if (envVersion === 'develop') return 'develop'
  if (envVersion === 'trial') return 'trial'
  return 'release'
}

module.exports = {
  envs,
  currentEnv,
}
