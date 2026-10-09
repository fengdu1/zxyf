<script setup lang="ts">
const stats = [
  { label: '班级总数', value: 68, icon: '🏫', color: '#7ec8f5' },
  { label: '在册学员', value: 1320, icon: '🧑‍🎓', color: '#ff9fb6' },
  { label: '教学部门', value: 12, icon: '🏢', color: '#ffd08a' },
  { label: '在职员工', value: 96, icon: '👩‍🏫', color: '#9be0c8' },
]
</script>

<template>
  <div class="home">
    <section class="hero-card">
      <div class="hero-text">
        <h2 class="hero-title">你好呀，欢迎回来 👋</h2>
        <p class="hero-desc">愿你今天的教学与学习都充满阳光与元气！</p>
        <p class="hero-desc">祝你今天有好的学习效果！</p>
      </div>
      <span class="hero-emoji">☁️</span>
    </section>

    <section class="stat-grid">
      <div v-for="s in stats" :key="s.label" class="stat-card" :style="{ '--c': s.color }">
        <span class="stat-icon">{{ s.icon }}</span>
        <div class="stat-info">
          <span class="stat-value">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>
    </section>

    <section class="panel">
      <div class="panel-title">📋 快捷操作</div>
      <div class="tips">
        <p>· 左侧菜单选择「班级管理」可维护班级信息</p>
        <p>· 「学员管理」用于维护学员信息</p>
        <p>· 「部门管理 / 员工管理」用于系统信息配置</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  gap: 20px;
}

.hero-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 32px;
  border-radius: 20px;
  background: var(--hero-grad);
  box-shadow: 0 8px 24px var(--hero-shadow);
  overflow: hidden;
}

.hero-title {
  font-size: 24px;
  color: var(--text-strong);
  font-weight: 700;
}

.hero-desc {
  margin-top: 8px;
  color: var(--text-mid);
  font-size: 15px;
}

.hero-emoji {
  font-size: 64px;
  filter: drop-shadow(0 4px 8px rgba(255, 255, 255, 0.6));
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: var(--card-bg);
  border-radius: 18px;
  box-shadow: 0 4px 14px rgba(126, 131, 160, 0.08);
  border: 1px solid var(--card-border);
}

.stat-icon {
  font-size: 38px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  background: var(--c);
  background: color-mix(in srgb, var(--c) 18%, white);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-strong);
}

.stat-label {
  font-size: 13px;
  color: var(--text-soft);
  margin-top: 2px;
}

.panel {
  background: var(--card-bg);
  border-radius: 18px;
  padding: 22px 26px;
  box-shadow: 0 4px 14px rgba(126, 131, 160, 0.08);
}

.panel-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-strong);
  margin-bottom: 14px;
}

.tips p {
  color: var(--text-mid);
  line-height: 2;
  font-size: 14px;
}
</style>