<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listEmps, delEmps } from '@/api/emp'
import type { Emp, EmpQuery } from '@/api/emp'
import EmpDialog from '@/components/EmpDialog.vue'
import { genderOptions, genderText, positionText, msgMap, fmtTime } from '@/constants/emp'

/* ===== 列表 ===== */
const loading = ref(false)
const rows = ref<Emp[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const loadData = async () => {
  loading.value = true
  try {
    const params: EmpQuery = {
      name: queryForm.name || undefined,
      gender: queryForm.gender === '' ? undefined : queryForm.gender,
      begin: queryForm.dateRange?.[0] || undefined,
      end: queryForm.dateRange?.[1] || undefined,
      page: page.value,
      pageSize: pageSize.value,
    }
    const res = await listEmps(params)
    rows.value = res.data?.rows ?? []
    total.value = res.data?.total ?? 0
  } finally {
    loading.value = false
  }
}

const search = () => {
  page.value = 1
  loadData()
}

const resetSearch = () => {
  queryForm.name = ''
  queryForm.gender = ''
  queryForm.dateRange = []
  page.value = 1
  loadData()
}

onMounted(loadData)

/* ===== 查询条件 ===== */
const queryForm = reactive({ name: '', gender: '' as number | '', dateRange: [] as string[] })

/* ===== 批量删除（选择模式）===== */
const selectMode = ref(false)
const selectedIds = ref<number[]>([])

const toggleSelectMode = () => {
  selectMode.value = !selectMode.value
  selectedIds.value = []
}

const batchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先勾选要删除的员工')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 名员工吗？`, '批量删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await delEmps(selectedIds.value)
      ElMessage.success(msgMap.del)
      selectMode.value = false
      selectedIds.value = []
      await loadData()
    })
    .catch(() => {})
}

const onSelectionChange = (val: Emp[]) => {
  selectedIds.value = val.map((i) => i.id)
}

/* ===== 单行删除 ===== */
const handleDelete = (row: Emp) => {
  ElMessageBox.confirm(`确定要删除员工「${row.name}」吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await delEmps([row.id])
      ElMessage.success(msgMap.del)
      await loadData()
    })
    .catch(() => {})
}

/* ===== 新增 / 修改弹窗 ===== */
const dialogVisible = ref(false)
const editingEmp = ref<Emp | null>(null)

const openAdd = () => {
  editingEmp.value = null
  dialogVisible.value = true
}

const openEdit = (row: Emp) => {
  editingEmp.value = row
  dialogVisible.value = true
}

const onSaved = async () => {
  await loadData()
}
</script>

<template>
  <div class="emp-page">
    <div class="page-header">
      <span class="bar" />
      <h2 class="page-title">员工管理</h2>
    </div>

    <!-- 查询区 -->
    <div class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="queryForm.name" placeholder="请输入员工姓名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="queryForm.gender" placeholder="请选择" clearable style="width: 120px">
            <el-option v-for="g in genderOptions" :key="g.value" :label="g.label" :value="g.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职时间">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetSearch">清空</el-button>
        </el-form-item>
      </el-form>

      <div class="action-bar">
        <el-button type="primary" @click="openAdd">
          <el-icon><Plus /></el-icon>新增员工
        </el-button>
        <el-button v-if="!selectMode" type="danger" plain @click="toggleSelectMode">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
        <template v-else>
          <span class="select-tip">已选 {{ selectedIds.length }} 项</span>
          <el-button type="danger" @click="batchDelete">
            <el-icon><Delete /></el-icon>确认删除
          </el-button>
          <el-button @click="toggleSelectMode">取消</el-button>
        </template>
      </div>
    </div>

    <!-- 列表 -->
    <div class="body-card">
      <el-table
        v-loading="loading"
        :data="rows"
        style="width: 100%"
        stripe
        :class="{ 'select-col-hidden': !selectMode }"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="name" label="姓名" min-width="110" align="center" fixed />
        <el-table-column label="性别" min-width="70" align="center">
          <template #default="{ row }">{{ genderText(row.gender) }}</template>
        </el-table-column>
        <el-table-column label="头像" min-width="90" align="center">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.image" />
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="所属部门" min-width="130" align="center" />
        <el-table-column label="职位" min-width="130" align="center">
          <template #default="{ row }">{{ positionText(row.position) }}</template>
        </el-table-column>
        <el-table-column prop="hireDate" label="入职日期" min-width="150" align="center" />
        <el-table-column label="最后操作时间" min-width="150" align="center">
          <template #default="{ row }">{{ fmtTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="190" align="center">
          <template #default="{ row }">
            <span class="row-ops">
              <el-button link type="warning" @click="openEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </span>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无员工数据" />
        </template>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          @current-change="loadData"
          @size-change="search"
        />
      </div>
    </div>

    <!-- 新增/修改弹窗 -->
    <EmpDialog v-model:visible="dialogVisible" :emp="editingEmp" @saved="onSaved" />
  </div>
</template>

<style scoped>
.emp-page {
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

/* 查询卡片 */
.search-card,
.body-card {
  background: var(--card-bg);
  border-radius: 18px;
  padding: 18px 22px;
  box-shadow: 0 4px 14px rgba(126, 131, 160, 0.08);
  border: 1px solid var(--card-border);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  row-gap: 4px;
}

.action-bar {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.select-tip {
  font-size: 13px;
  color: var(--text-mid);
}

.row-ops {
  display: inline-flex;
  align-items: center;
  gap: 18px;
}

/* 表格 */
.body-card :deep(.el-table) {
  --el-table-border-color: var(--card-border);
  --el-table-header-bg-color: var(--hover-bg);
  --el-table-header-text-color: var(--text-strong);
  --el-table-row-hover-bg-color: var(--hover-bg);
  border-radius: 12px;
  overflow: hidden;
}

.body-card :deep(th.el-table__cell) {
  font-weight: 600;
}

/* 默认隐藏可选框：列仍占用空间，仅复选框不可见 */
.body-card :deep(.select-col-hidden th.el-table-column--selection .el-checkbox),
.body-card :deep(.select-col-hidden td.el-table-column--selection .el-checkbox) {
  visibility: hidden;
}

.body-card :deep(.el-table td.el-table__cell) {
  padding-top: 12px;
  padding-bottom: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}
</style>