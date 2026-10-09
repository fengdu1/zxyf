import request from './request'
import type { ApiResult } from './request'

export interface LoginReq {
  username: string
  password: string
}

export interface LoginRes {
  id: number
  username: string
  name: string
  token: string
}

// 员工登录，成功后排发 JWT 令牌
export const login = (data: LoginReq) =>
  request.post<ApiResult<LoginRes>>('/login', data).then((r) => r.data)