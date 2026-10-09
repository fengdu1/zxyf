<script setup lang="ts">
defineProps<{ title: string; describe?: string }>()
</script>

<template>
  <div class="page">
    <div class="page-header">
      <span class="bar" />
      <h2 class="page-title">{{ title }}</h2>
    </div>

    <div class="page-body">
      <div class="placeholder">
        <span class="emoji">🏗️</span>
        <p class="placeholder-title">{{ describe ?? `${title}页面` }}</p>
        <p class="placeholder-desc">此区域将在后续步骤中填充具体功能内容</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
}

.bar {
  width: 5px;
  height: 22px;
  border-radius: 4px;
  background: var(--bar-grad);
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-strong);
}

.page-body {
  background: var(--card-bg);
  border-radius: 18px;
  padding: 40px;
  box-shadow: 0 4px 14px rgba(126, 131, 160, 0.08);
  border: 1px solid var(--card-border);
}

.placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 60px 0;
  border: 2px dashed var(--dash-border);
  border-radius: 16px;
}

.emoji {
  font-size: 52px;
  margin-bottom: 14px;
}

.placeholder-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-strong);
}

.placeholder-desc {
  margin-top: 8px;
  color: var(--text-soft);
  font-size: 14px;
}
</style>