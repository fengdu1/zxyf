import request from './request'
import type { ApiResult } from './request'

export interface Dept {
  id: number
  name: string
  createTime?: string
  updateTime?: string
}

// 部门列表查询
export const listDepts = () => request.get<ApiResult<Dept[]>>('/depts').then((r) => r.data)

// 根据 ID 查询
export const getDept = (id: number) => request.get<ApiResult<Dept>>(`/depts/${id}`).then((r) => r.data)

// 添加部门
export const addDept = (name: string) =>
  request.post<ApiResult>('/depts', { name }).then((r) => r.data)

// 修改部门
export const updateDept = (data: { id: number; name: string }) =>
  request.put<ApiResult>('/depts', data).then((r) => r.data)

// 删除部门
export const delDept = (id: number) =>
  request.delete<ApiResult>(`/depts/${id}`).then((r) => r.data)