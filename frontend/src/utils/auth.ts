// 登录凭证与用户信息的本地存取

const TOKEN_KEY = 'zxyf-token'
const USER_KEY = 'zxyf-user'

export interface LoginUser {
  id: number
  username: string
  name: string
  token: string
  /** 头像地址（来自数据库 image 字段） */
  image?: string
}

export const getToken = () => localStorage.getItem(TOKEN_KEY) ?? ''

export const setToken = (token: string) => localStorage.setItem(TOKEN_KEY, token)

export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

export const getUser = (): LoginUser | null => {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as LoginUser
  } catch {
    return null
  }
}

export const setUser = (user: LoginUser) => localStorage.setItem(USER_KEY, JSON.stringify(user))

export const clearUser = () => localStorage.removeItem(USER_KEY)

export const clearAuth = () => {
  clearToken()
  clearUser()
}