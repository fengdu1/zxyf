import request from './request'
import type { ApiResult } from './request'

// 工作经历
export interface EmpExpr {
  id?: number
  startDate?: string
  endDate?: string
  company?: string
  position?: string
  empId?: number
}

// 员工
export interface Emp {
  id: number
  username: string
  name: string
  gender: number
  image?: string
  originalName?: string
  position?: number
  salary?: number
  hireDate?: string
  deptId?: number
  deptName?: string
  phone?: string
  createTime?: string
  updateTime?: string
  exprList?: EmpExpr[]
}

// 列表查询参数
export interface EmpQuery {
  name?: string
  gender?: number | ''
  begin?: string
  end?: string
  page: number
  pageSize: number
}

// 分页结果
export interface PageResult<T> {
  total: number
  rows: T[]
}

// 新增/修改提交体
export interface EmpPayload {
  id?: number
  username: string
  name: string
  gender: number | ''
  image?: string
  originalName?: string
  position?: number
  salary?: number
  hireDate?: string
  deptId?: number
  phone?: string
  exprList: EmpExpr[]
}

// 员工列表查询
export const listEmps = (params: EmpQuery) =>
  request.get<ApiResult<PageResult<Emp>>>('/emps', { params }).then((r) => r.data)

// 根据 ID 查询
export const getEmp = (id: number) =>
  request.get<ApiResult<Emp>>(`/emps/${id}`).then((r) => r.data)

// 添加员工
export const addEmp = (data: EmpPayload) => request.post<ApiResult>('/emps', data).then((r) => r.data)

// 修改员工
export const updateEmp = (data: EmpPayload) => request.put<ApiResult>('/emps', data).then((r) => r.data)

// 删除员工（支持批量）
export const delEmps = (ids: number[]) =>
  request.delete<ApiResult>('/emps', { params: { ids: ids.join(',') } }).then((r) => r.data)

// 文件上传（阿里云 OSS），返回文件访问路径
export const uploadFile = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request
    .post<ApiResult<string>>('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((r) => r.data)
}