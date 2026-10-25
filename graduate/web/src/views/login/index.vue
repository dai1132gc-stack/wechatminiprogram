<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="brand-panel">
      <div class="brand-content">
        <h1 class="brand-title">房屋租赁与环境监测管理平台</h1>
        <p class="brand-subtitle">房源管理 · 租赁签约 · 室内环境监测一体化</p>
        <ul class="brand-points">
          <li>房源发布与审核流转</li>
          <li>预约看房、租赁申请、电子合同全链路</li>
          <li>温湿度 / 甲醛 / TVOC / CO₂ 实时监测与超标告警</li>
        </ul>
      </div>
    </div>

    <!-- 右侧表单卡片 -->
    <div class="form-panel">
      <el-card class="login-card">
        <h2 class="login-title">登录</h2>
        <p class="login-tip">Web 管理后台（房东 / 管理员）</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item label="账号" prop="username">
            <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              autocomplete="current-password"
            >
              <template #prefix><el-icon><Lock /></el-icon></template>
              <template #suffix>
                <el-icon class="pwd-toggle" @click="showPassword = !showPassword">
                  <View v-if="showPassword" />
                  <Hide v-else />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-extra">
            <el-checkbox v-model="remember">记住账号</el-checkbox>
          </div>

          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            :disabled="lockUntil > Date.now()"
            @click="handleLogin"
          >
            {{ lockButtonLabel }}
          </el-button>
        </el-form>

        <el-divider class="demo-divider">演示账号（Mock）</el-divider>
        <div class="demo-accounts">
          <el-button size="small" @click="fill('admin')">管理员 admin</el-button>
          <el-button size="small" @click="fill('landlord1')">房东 landlord1</el-button>
        </div>

        <p class="footer-meta">v0.1.0 · W1 工程骨架 · Mock 数据</p>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Lock, User, View, Hide } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/modules/user'
import { getRememberedUsername, removeRememberedUsername, setRememberedUsername } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const form = reactive({ username: '', password: '' })
const showPassword = ref(false)
const remember = ref(false)
const loading = ref(false)
/** 20005 登录过于频繁：按钮禁用至锁定期结束（15 分钟） */
const lockUntil = ref(0)

const rules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32 位', trigger: 'blur' },
  ],
}

const lockButtonLabel = computed(() => {
  if (lockUntil.value <= Date.now()) return '登 录'
  const seconds = Math.ceil((lockUntil.value - Date.now()) / 1000)
  const min = Math.floor(seconds / 60)
  return `尝试过于频繁，${min > 0 ? `${min} 分钟` : `${seconds} 秒`}后重试`
})

function fill(username: string): void {
  form.username = username
  form.password = username === 'admin' ? 'admin123' : '123456'
}

async function handleLogin(): Promise<void> {
  if (loading.value || lockUntil.value > Date.now()) return
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const envelope = await login({ username: form.username, password: form.password })
    if (envelope.code !== 0 || !envelope.data) {
      // 按设计方案 §1.4 分派：20005 禁用按钮，20006 普通提示
      if (envelope.code === 20005) {
        lockUntil.value = Date.now() + 15 * 60 * 1000
        ElMessage.error('尝试过于频繁，请 15 分钟后再试')
      } else {
        ElMessage.error(envelope.message || '登录失败')
      }
      return
    }
    userStore.setLogin(envelope.data)
    if (remember.value) {
      setRememberedUsername(form.username)
    } else {
      removeRememberedUsername()
    }
    ElMessage.success(`欢迎，${envelope.data.userInfo.nickname}`)
    const redirect = (route.query.redirect as string) || '/dashboard'
    router.push(redirect)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const remembered = getRememberedUsername()
  if (remembered) {
    form.username = remembered
    remember.value = true
  }
})
</script>

<style scoped lang="scss">
.login-page {
  height: 100%;
  display: flex;
}
.brand-panel {
  flex: 1.2;
  background: var(--brand-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.brand-content {
  max-width: 460px;
  padding: 40px;
  .brand-title {
    font-size: 30px;
    line-height: 1.4;
    margin: 0 0 12px;
  }
  .brand-subtitle {
    font-size: 16px;
    opacity: 0.85;
    margin: 0 0 32px;
  }
  .brand-points {
    list-style: none;
    padding: 0;
    margin: 0;
    li {
      padding: 8px 0;
      font-size: 14px;
      opacity: 0.9;
      &::before {
        content: '✓';
        margin-right: 10px;
        font-weight: 700;
      }
    }
  }
}
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.login-card {
  width: 400px;
  padding: 8px 12px 4px;
  .login-title {
    margin: 8px 0 4px;
    font-size: 22px;
  }
  .login-tip {
    margin: 0 0 20px;
    color: #909399;
    font-size: 13px;
  }
}
.pwd-toggle {
  cursor: pointer;
}
.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.login-btn {
  width: 100%;
}
.demo-divider {
  margin: 20px 0 12px;
  :deep(.el-divider__text) {
    font-size: 12px;
    color: #909399;
  }
}
.demo-accounts {
  display: flex;
  gap: 8px;
  justify-content: center;
}
.footer-meta {
  margin: 16px 0 0;
  text-align: center;
  color: #c0c4cc;
  font-size: 12px;
}
@media (max-width: 768px) {
  .brand-panel {
    display: none;
  }
}
</style>
