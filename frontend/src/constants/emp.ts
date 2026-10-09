// 员工管理相关枚举与常量

// 性别选项
export const genderOptions = [
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]

// 职位选项
export const positionOptions = [
  { label: '班主任', value: 1 },
  { label: '讲师', value: 2 },
  { label: '学工主管', value: 3 },
  { label: '教研主管', value: 4 },
  { label: '咨询师', value: 5 },
]

// 默认头像（无图片时的占位）
export const DEFAULT_IMAGE =
  'https://zxyf-feng.oss-cn-beijing.aliyuncs.com/dc26a27f894748c4af371608eef7af8b.png'

// 操作成功提示文案
export const msgMap = { add: '已新增员工', edit: '修改成功', del: '删除成功' }

// 性别 / 职位 文本映射
export const genderText = (g?: number) => genderOptions.find((o) => o.value === g)?.label ?? '-'
export const positionText = (p?: number) => positionOptions.find((o) => o.value === p)?.label ?? '-'

// 时间格式化：T 分隔 → 空格分隔
export const fmtTime = (t?: string) => (t ? t.replace('T', ' ') : '-')