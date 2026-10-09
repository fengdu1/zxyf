<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { login } from '@/api/login'
import { getEmp } from '@/api/emp'
import { setToken, setUser } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '' })

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const bgImage =
  'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
  encodeURIComponent(
    'anime style clean celeste blue sky with soft clouds above the sea, gentle sunlight, dreamy japanese animation scenery, soft pastel colors, cinematic wide landscape',
  ) +
  '&image_size=landscape_16_9'

// 悬浮光球坐标（模板 v-for 内联调用）
const orbLeft = (n: number) => `${(n * 13 + 5) % 100}%`
const orbTop = (n: number) => `${(n * 17 + 10) % 90}%`
const orbDelay = (n: number) => `${n * 0.9}s`

const handleLogin = async () => {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await login(form)
    if (res.code !== 1) {
      ElMessage.error(res.msg || '登录失败')
      return
    }
    const data = res.data!
    setToken(data.token)
    // 登录接口不含头像，需按 id 查询员工信息读取 image 字段
    let image: string | undefined
    try {
      image = (await getEmp(data.id)).data?.image
    } catch {
      image = undefined
    }
    setUser({ id: data.id, username: data.username, name: data.name, token: data.token, image })
    ElMessage.success(`欢迎回来，${data.name}`)
    const redirect = (route.query.redirect as string) || '/'
    router.replace(redirect)
  } catch {
    ElMessage.error('登录失败，请检查网络或稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- 动态日漫风背景 -->
    <div class="bg">
      <img class="bg-img" :src="bgImage" alt="背景" />
      <div class="bg-veil" />
      <div class="float-orbs">
        <span v-for="n in 8" :key="n" class="orb" :style="{ left: orbLeft(n), top: orbTop(n), animationDelay: orbDelay(n) }" />
      </div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="brand">
        <span class="brand-logo">📖</span>
        <h1 class="brand-title">智学云帆</h1>
        <p class="brand-sub">教学管理系统</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button class="login-btn" type="primary" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>

      <p class="foot">智学云帆 · 让教学更简单</p>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-family: 'Helvetica Neue', Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 动态日漫背景 */
.bg {
  position: absolute;
  inset: 0;
}
.bg-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.bg-veil {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, rgba(180, 205, 235, 0.18), rgba(255, 255, 255, 0.12));
}
.float-orbs {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.orb {
  position: absolute;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.55);
  filter: blur(1px);
  animation: floatUp 8s linear infinite;
  opacity: 0;
}
@keyframes floatUp {
  0% {
    transform: translateY(30px);
    opacity: 0;
  }
  20% {
    opacity: 0.7;
  }
  100% {
    transform: translateY(-80px);
    opacity: 0;
  }
}

/* 登录卡片 */
.login-card {
  position: relative;
  width: 400px;
  padding: 44px 40px 30px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(16px);
  border-radius: 24px;
  box-shadow: 0 18px 48px rgba(80, 110, 160, 0.28);
  border: 1px solid rgba(255, 255, 255, 0.6);
  text-align: center;
  animation: cardIn 0.7s ease;
}
@keyframes cardIn {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.brand-logo {
  font-size: 44px;
  display: inline-block;
  margin-bottom: 6px;
}
.brand-title {
  font-size: 26px;
  font-weight: 800;
  margin: 4px 0 2px;
  color: #3a4a63;
  letter-spacing: 2px;
}
.brand-sub {
  font-size: 14px;
  color: #7d8ea6;
  margin: 0 0 28px;
  letter-spacing: 4px;
}
.login-btn {
  width: 100%;
  margin-top: 4px;
  border-radius: 10px;
  font-size: 16px;
  letter-spacing: 6px;
}
.foot {
  margin-top: 22px;
  font-size: 12px;
  color: #9aa7bb;
}
</style>