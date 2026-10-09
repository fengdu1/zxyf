<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { addEmp, getEmp, updateEmp, uploadFile } from '@/api/emp'
import type { Emp, EmpPayload } from '@/api/emp'
import { listDepts } from '@/api/dept'
import type { Dept } from '@/api/dept'
import { genderOptions, positionOptions, DEFAULT_IMAGE, msgMap } from '@/constants/emp'

const props = defineProps<{
  visible: boolean
  emp: Emp | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'saved'): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (v) => emit('update:visible', v),
})

const dialogMode = computed(() => (props.emp ? 'edit' : 'add'))
const dialogTitle = computed(() => (dialogMode.value === 'add' ? '新增员工' : '修改员工'))

const allDepts = ref<Dept[]>([])
listDepts().then((res) => {
  allDepts.value = res.data ?? []
})

const submitting = ref(false)
const formRef = ref<FormInstance>()

const emptyForm = (): EmpPayload => ({
  username: '',
  name: '',
  gender: '',
  position: undefined,
  salary: undefined,
  hireDate: '',
  deptId: undefined,
  phone: '',
  image: DEFAULT_IMAGE,
  originalName: '',
  exprList: [],
})

const form = reactive<EmpPayload>(emptyForm())

const rules: FormRules = {
  username: [
    { required: true, message: '请输入员工用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名需为 2-20 个字符', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名需为 2-10 个字符', trigger: 'blur' },
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  hireDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
}

// 头像：本地预览 + 待上传的原始文件（提交时上传到 OSS 后回填 url）
const avatarPreview = ref(DEFAULT_IMAGE)
const avatarFile = ref<File | null>(null)
const uploadingAvatar = ref(false)

const handleAvatarChange = (file: { raw: File }) => {
  const typeOK = ['image/png', 'image/jpeg', 'image/jpg'].includes(file.raw.type)
  const sizeOK = file.raw.size / 1024 / 1024 <= 2
  if (!typeOK) {
    ElMessage.warning('仅支持 PNG/JPEG/JPG 图片')
    return
  }
  if (!sizeOK) {
    ElMessage.warning('图片大小不能超过 2MB')
    return
  }
  form.originalName = file.raw.name
  avatarPreview.value = URL.createObjectURL(file.raw)
  avatarFile.value = file.raw
}

const resetAvatar = () => {
  avatarFile.value = null
  uploadingAvatar.value = false
}

// 工作经历：添加 / 删除一行
const addExpr = () => {
  form.exprList.push({ company: '', position: '', startDate: '', endDate: '' })
}
const removeExpr = (index: number) => {
  form.exprList.splice(index, 1)
}

// 打开弹窗时初始化表单
watch(
  () => props.visible,
  async (open) => {
    if (!open) return
    Object.assign(form, emptyForm())
    avatarPreview.value = DEFAULT_IMAGE
    resetAvatar()
    // 修改：联合查询员工工作信息并回显
    if (props.emp) {
      const data = (await getEmp(props.emp.id)).data
      Object.assign(form, {
        id: data.id,
        username: data.username,
        name: data.name,
        gender: data.gender,
        position: data.position,
        salary: data.salary,
        hireDate: data.hireDate,
        deptId: data.deptId,
        phone: data.phone ?? '',
        image: data.image ?? DEFAULT_IMAGE,
        originalName: data.originalName ?? '',
        exprList: (data.exprList ?? []).map((e) => ({ ...e })),
      })
      avatarPreview.value = data.image ?? DEFAULT_IMAGE
    }
    formRef.value?.clearValidate()
  },
)

const submit = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    // 若用户选择了新头像，先上传到 OSS 获取访问路径，再回填 image
    let imageUrl = form.image
    if (avatarFile.value) {
      uploadingAvatar.value = true
      const up = await uploadFile(avatarFile.value)
      if (!up.data) throw new Error('上传头像失败')
      imageUrl = up.data
    }
    const payload: EmpPayload = {
      ...form,
      salary: form.salary ? Number(form.salary) : undefined,
      image: imageUrl,
      exprList: form.exprList,
    }
    if (dialogMode.value === 'add') {
      await addEmp(payload)
      ElMessage.success(msgMap.add)
    } else {
      await updateEmp(payload)
      ElMessage.success(msgMap.edit)
    }
    dialogVisible.value = false
    resetAvatar()
    emit('saved')
  } catch (e) {
    if (e instanceof Error) ElMessage.error(e.message)
  } finally {
    uploadingAvatar.value = false
    submitting.value = false
  }
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="860px" align-center top="4vh">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="emp-form">
      <div class="form-grid">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="dialogMode === 'edit'" placeholder="请输入员工用户名，2-20个字" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入员工姓名，2-10个字" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
            <el-option v-for="g in genderOptions" :key="g.value" :label="g.label" :value="g.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入员工手机号" />
        </el-form-item>
        <el-form-item label="职位">
          <el-select v-model="form.position" placeholder="请选择" clearable style="width: 100%">
            <el-option v-for="p in positionOptions" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="薪资">
          <el-input-number v-model="form.salary" :min="0" :step="500" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择" style="width: 100%">
            <el-option v-for="d in allDepts" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="hireDate">
          <el-date-picker v-model="form.hireDate" type="date" placeholder="请选择入职日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </div>

      <!-- 头像 -->
      <el-form-item label="头像">
        <div class="avatar-box">
          <el-avatar :size="72" :src="avatarPreview" />
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/png,image/jpeg,image/jpg"
            :on-change="handleAvatarChange"
          >
            <el-button size="small" type="primary" plain>选择图片</el-button>
          </el-upload>
          <span class="avatar-tip">仅支持 PNG/JPEG/JPG，大小不超过 2MB</span>
        </div>
      </el-form-item>

      <!-- 工作经历 -->
      <div class="expr-section">
        <div class="expr-head">
          <span class="expr-title">工作经历</span>
          <el-button size="small" type="success" @click="addExpr">
            <el-icon><Plus /></el-icon>添加工作经历
          </el-button>
        </div>

        <div v-for="(expr, i) in form.exprList" :key="i" class="expr-row">
          <span class="expr-label">时间：</span>
          <el-date-picker v-model="expr.startDate" type="date" placeholder="开始时间" value-format="YYYY-MM-DD" />
          <span class="expr-sep">至</span>
          <el-date-picker v-model="expr.endDate" type="date" placeholder="结束时间" value-format="YYYY-MM-DD" />
          <span class="expr-label">公司：</span>
          <el-input v-model="expr.company" placeholder="公司" />
          <span class="expr-label">职位：</span>
          <el-input v-model="expr.position" placeholder="职位" />
          <el-button link type="danger" @click="removeExpr(i)">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </div>
      </div>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting || uploadingAvatar" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.emp-form {
  padding: 4px 8px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 36px;
  row-gap: 6px;
}

.avatar-box {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-tip {
  font-size: 12px;
  color: var(--text-soft);
}

/* 工作经历 */
.expr-section {
  margin-top: 16px;
  border-top: 1px dashed var(--card-border);
  padding-top: 18px;
}

.expr-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.expr-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-strong);
}

.expr-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.expr-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-mid);
  white-space: nowrap;
}

.expr-row .el-date-editor {
  width: 118px;
}

.expr-row :deep(.el-input) {
  flex: 1;
  min-width: 130px;
}
</style>