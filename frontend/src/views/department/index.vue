<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listDepts, addDept, updateDept, delDept } from '@/api/dept'
import type { Dept } from '@/api/dept'

const loading = ref(false)
const tableData = ref<Dept[]>([])

// ===== 分页（前端分页）=====
const page = ref(1)
const pageSize = ref(12)
const total = computed(() => tableData.value.length)
const pageData = computed(() =>
  tableData.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value),
)

// 业务操作成功提示文案
const msgMap = { add: '已新增部门', edit: '修改成功', del: '删除成功' }

// ===== 弹窗表单 =====
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const dialogTitle = computed(() => (dialogMode.value === 'add' ? '新增部门' : '修改部门'))
const submitting = ref(false)

const formRef = ref<FormInstance>()
const form = ref<{ id: number; name: string }>({ id: 0, name: '' })

const rules: FormRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
}

// ===== 数据加载 =====
const loadData = async () => {
  loading.value = true
  try {
    const res = await listDepts()
    tableData.value = res.data ?? []
    // 刷新后回到第一页，避免停留在空页
    page.value = 1
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

// ===== 新增 =====
const openAdd = () => {
  dialogMode.value = 'add'
  form.value = { id: 0, name: '' }
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// ===== 修改（回显原部门信息）=====
const openEdit = (row: Dept) => {
  dialogMode.value = 'edit'
  form.value = { id: row.id, name: row.name }
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

// ===== 提交 =====
const submit = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'add') {
      await addDept(form.value.name)
      ElMessage.success(msgMap.add)
    } else {
      await updateDept({ id: form.value.id, name: form.value.name })
      ElMessage.success(msgMap.edit)
    }
    dialogVisible.value = false
    // 异步刷新，不重新加载页面
    await loadData()
  } finally {
    submitting.value = false
  }
}

// ===== 删除（二次确认）=====
const handleDelete = (row: Dept) => {
  ElMessageBox.confirm(`确定要删除部门「${row.name}」吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await delDept(row.id)
      ElMessage.success(msgMap.del)
      await loadData()
    })
    .catch(() => {
      // 用户取消，无需处理
    })
}

// 格式化时间：2022-09-01T23:06:29 -> 2022-09-01 23:06:29
const fmtTime = (t?: string) => (t ? t.replace('T', ' ') : '-')
</script>

<template>
  <div class="dept-page">
    <div class="page-header">
      <span class="bar" />
      <h2 class="page-title">部门管理</h2>
      <el-button type="primary" class="add-btn" @click="openAdd">
        <el-icon><Plus /></el-icon>新增部门
      </el-button>
    </div>

    <div class="page-body">
      <el-table v-loading="loading" :data="pageData" style="width: 100%" stripe>
        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">{{ (page - 1) * pageSize + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="name" label="部门名称" width="300" align="center" show-overflow-tooltip />
        <el-table-column label="创建时间" min-width="180" align="center">
          <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="最后操作时间" min-width="180" align="center">
          <template #default="{ row }">{{ fmtTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button link type="warning" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无部门数据" />
        </template>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 15, 20, 50]"
        />
      </div>
    </div>

    <!-- 新增/修改 二级弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px" align-center>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" maxlength="30" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.dept-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 10px;
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
  flex: 1;
}

.add-btn {
  border-radius: 20px;
  font-weight: 600;
  box-shadow: 0 4px 12px var(--active-shadow);
  padding-left: 16px;
  padding-right: 18px;
}

.add-btn .el-icon {
  margin-right: 4px;
}

.page-body {
  background: var(--card-bg);
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 4px 14px rgba(126, 131, 160, 0.08);
  border: 1px solid var(--card-border);
}

.page-body :deep(.el-table) {
  --el-table-border-color: var(--card-border);
  --el-table-header-bg-color: var(--hover-bg);
  --el-table-header-text-color: var(--text-strong);
  --el-table-row-hover-bg-color: var(--hover-bg);
  border-radius: 12px;
  overflow: hidden;
}

/* 优化列间距：增大单元格内边距，避免文字贴边 */
.page-body :deep(.el-table th.el-table__cell),
.page-body :deep(.el-table td.el-table__cell) {
  padding: 12px 0;
}

.page-body :deep(.el-table td.el-table__cell) {
  padding-top: 14px;
  padding-bottom: 14px;
}

/* 部门名称列左对齐留出呼吸感 */
.page-body :deep(.el-table__cell .cell) {
  padding: 0 18px;
  line-height: 1.5;
}

/* 分页 */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.pagination-wrap :deep(.el-pagination) {
  --el-pagination-button-bg-color: var(--hover-bg);
  --el-pagination-hover-color: var(--menu-active-text);
}

.page-body :deep(th.el-table__cell) {
  font-weight: 600;
}
</style>