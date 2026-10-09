<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { getUser, clearAuth } from '@/utils/auth'

// 主题切换
const theme = ref<string>(localStorage.getItem('zxyf-theme') ?? 'cool')
const toggleTheme = () => {
  theme.value = theme.value === 'cool' ? 'warm' : 'cool'
  document.documentElement.classList.toggle('theme-warm', theme.value === 'warm')
  localStorage.setItem('zxyf-theme', theme.value)
}

// 侧边栏收起/展开
const isCollapse = ref(false)
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 顶部标题
const title = '智学云帆-教学管理系统'

// 当前登录员工（登录时按 id 回填的姓名与数据库头像）
const currentUser = computed(() => {
  const u = getUser()
  return {
    nickname: u?.name ?? '未登录',
    avatar:
      u?.image ||
      'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=anime%20style%20cute%20girl%20avatar%20portrait%20pastel%20colors&image_size=square_hd',
  }
})

// 侧边栏菜单结构
const menuItems = [
  { index: '/home', title: '首页', icon: 'HomeFilled' },
  {
    index: 'clazzGroup',
    title: '班级学员管理',
    icon: 'School',
    children: [
      { index: '/clazz', title: '班级管理', icon: 'Notebook' },
      { index: '/student', title: '学员管理', icon: 'User' },
    ],
  },
  {
    index: 'systemGroup',
    title: '系统信息管理',
    icon: 'Setting',
    children: [
      { index: '/department', title: '部门管理', icon: 'OfficeBuilding' },
      { index: '/employee', title: '员工管理', icon: 'Postcard' },
    ],
  },
]

const route = useRoute()
const router = useRouter()

// 默认展开的分组
const defaultOpens = computed(() => {
  const active = route.path
  if (active === '/clazz' || active === '/student') return ['clazzGroup']
  if (active === '/department' || active === '/employee') return ['systemGroup']
  return []
})

const handleSelect = (index: string) => {
  router.push(index)
}

const handleCommand = async (command: string) => {
  if (command === 'profile') {
    // 个人信息（演示占位）
    console.log('跳转到个人信息')
  } else if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    clearAuth()
    router.push('/login')
  }
}
</script>

<template>
  <div class="layout">
    <!-- ===== 顶部导航栏 ===== -->
    <header class="layout-header">
      <div class="header-left">
        <span class="logo">📖</span>
        <h1 class="title">{{ title }}</h1>
      </div>

      <div class="header-right">
        <el-tooltip content="切换主题风格" placement="bottom">
          <div class="theme-switch" @click="toggleTheme">
            <span class="dot cool" :class="{ active: theme === 'cool' }" title="冷色调"></span>
            <span class="dot warm" :class="{ active: theme === 'warm' }" title="清新暖色"></span>
          </div>
        </el-tooltip>

        <el-dropdown trigger="hover" @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="34" :src="currentUser.avatar" class="user-avatar" />
            <span class="nickname">{{ currentUser.nickname }}</span>
            <el-icon class="arrow"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人信息
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>退出系统
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- ===== 主体区域 ===== -->
    <div class="layout-body">
      <!-- 左侧侧边栏 -->
      <aside class="layout-aside" :class="{ 'is-collapse': isCollapse }">
        <!-- 交界处折叠按钮 -->
        <div class="fold-trigger" @click="toggleCollapse">
          <el-icon><DArrowLeft v-if="!isCollapse" /><DArrowRight v-else /></el-icon>
        </div>

        <el-menu
          :default-active="route.path"
          :default-openeds="defaultOpens"
          :collapse="isCollapse"
          :collapse-transition="false"
          class="side-menu"
          background-color="transparent"
          @select="handleSelect"
        >
          <el-menu-item v-for="item in menuItems.filter(m => !m.children)" :key="item.index" :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>

          <el-sub-menu v-for="group in menuItems.filter(m => m.children)" :key="group.index" :index="group.index">
            <template #title>
              <el-icon><component :is="group.icon" /></el-icon>
              <span>{{ group.title }}</span>
            </template>
            <el-menu-item v-for="child in group.children" :key="child.index" :index="child.index">
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>

      <!-- 右侧核心展示区域 -->
      <main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ===== 顶部导航 ===== */
.layout-header {
  flex: 0 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: var(--header-grad);
  box-shadow: 0 2px 12px var(--header-shadow);
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.fold-trigger {
  position: absolute;
  top: 50%;
  right: -14px;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  box-shadow: 0 2px 8px rgba(126, 131, 160, 0.2);
  color: var(--menu-text);
  cursor: pointer;
  z-index: 20;
  transition: background 0.25s, color 0.25s, box-shadow 0.25s;
}

.fold-trigger .el-icon {
  font-size: 15px;
}

.fold-trigger:hover {
  background: var(--active-bg);
  color: var(--menu-active-text);
  box-shadow: 0 2px 10px var(--active-shadow);
}

.logo {
  font-size: 26px;
  line-height: 1;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: #5b4a63;
  letter-spacing: 1px;
  background: var(--title-grad);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

/* 主题切换 */
.theme-switch {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 20px;
  background: var(--hover-soft);
  cursor: pointer;
  transition: background 0.25s;
  border: 1px solid var(--card-border);
}

.theme-switch:hover {
  background: rgba(255, 255, 255, 0.85);
}

.theme-switch .dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  transition: transform 0.2s, box-shadow 0.2s;
}

.theme-switch .dot.cool {
  background: linear-gradient(135deg, #5c8bea, #47d1f0);
}

.theme-switch .dot.warm {
  background: linear-gradient(135deg, #ff8fab, #ffd08a);
}

.theme-switch .dot.active {
  transform: scale(1.35);
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.7);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 24px;
  transition: background 0.25s;
  outline: none;
}

.user-info:hover {
  background: var(--hover-soft);
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 6px var(--avatar-shadow);
}

.nickname {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-strong);
}

.arrow {
  color: var(--text-soft);
  font-size: 14px;
}

/* ===== 主体 ===== */
.layout-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧侧边栏 */
.layout-aside {
  position: relative;
  flex: 0 0 220px;
  width: 220px;
  background: var(--card-bg);
  border-right: 1px solid var(--card-border);
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 10px;
  box-shadow: 2px 0 12px rgba(126, 131, 160, 0.06);
  transition: width 0.25s ease, flex-basis 0.25s ease, padding 0.25s ease;
}

.layout-aside.is-collapse {
  flex: 0 0 64px;
  width: 64px;
  padding: 12px 6px;
}

.side-menu {
  border-right: none;
  border-radius: 16px;
}

.side-menu :deep(.el-menu-item) {
  height: 46px;
  margin: 4px 0;
  border-radius: 12px;
  color: var(--menu-text);
  transition: all 0.25s;
}

.side-menu :deep(.el-sub-menu__title) {
  height: 46px;
  margin: 4px 0;
  border-radius: 12px;
  color: var(--menu-text);
  transition: all 0.25s;
}

.side-menu :deep(.el-menu-item:hover),
.side-menu :deep(.el-sub-menu__title:hover) {
  background: var(--hover-bg);
  color: var(--text-strong);
}

.side-menu :deep(.el-menu-item.is-active) {
  background: var(--active-bg);
  color: var(--menu-active-text);
  font-weight: 600;
  box-shadow: 0 4px 12px var(--active-shadow);
}

/* 收起状态下菜单项居中对齐 */
.layout-aside.is-collapse .side-menu :deep(.el-menu-item),
.layout-aside.is-collapse .side-menu :deep(.el-sub-menu__title) {
  justify-content: center;
  padding: 0;
}

/* 右侧核心展示区域 */
.layout-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: var(--main-bg);
}

/* 路由切换过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>