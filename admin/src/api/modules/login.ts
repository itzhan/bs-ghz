import { Login } from "@/api/interface/index";
import authMenuList from "@/assets/json/authMenuList.json";
import http from "@/api";

/**
 * @name 登录模块
 */
// 用户登录
export const loginApi = (params: Login.ReqLoginForm) => {
  return http.post<Login.ResLogin>(`/auth/login`, params, { loading: false });
};

// 获取菜单列表（使用本地JSON）
export const getAuthMenuListApi = () => {
  return authMenuList;
};

// 获取按钮权限（返回空对象）
export const getAuthButtonListApi = () => {
  return {};
};

// 用户退出登录（前端清除token即可）
export const logoutApi = () => {
  return Promise.resolve();
};
