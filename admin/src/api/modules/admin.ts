import http from "@/api";

// 用户管理
export const getUserList = (params: any) => http.get("/admin/users", params);
export const updateUserStatus = (id: number, status: number) => http.put(`/admin/users/${id}/status`, { status });
export const deleteUser = (id: number) => http.delete(`/admin/users/${id}`);

// 档口管理
export const getStallList = (params: any) => http.get("/stalls", params);
export const auditStall = (id: number, status: number) => http.put(`/admin/stalls/${id}/audit`, { status });

// 菜品管理
export const getDishList = (params: any) => http.get("/dishes", params);
export const auditDish = (id: number, status: number) => http.put(`/admin/dishes/${id}/audit`, { status });
export const getDishCategories = (params: any) => http.get("/dish-categories", params);

// 订单管理
export const getOrderList = (params: any) => http.get("/admin/orders", params);
export const updateOrderStatus = (id: number, status: number) => http.put(`/admin/orders/${id}/status`, { status });
export const getPaymentList = (params: any) => http.get("/admin/payments", params);

// 评价管理
export const getReviewList = (params: any) => http.get("/admin/reviews", params);
export const updateReviewStatus = (id: number, status: number) => http.put(`/admin/reviews/${id}/status`, { status });

// 投诉管理
export const getComplaintList = (params: any) => http.get("/admin/complaints", params);
export const handleComplaint = (id: number, data: any) => http.put(`/admin/complaints/${id}/handle`, data);

// 公告管理
export const getAnnouncementList = (params: any) => http.get("/announcements", params);
export const createAnnouncement = (data: any) => http.post("/admin/announcements", data);
export const updateAnnouncement = (id: number, data: any) => http.put(`/admin/announcements/${id}`, data);
export const deleteAnnouncement = (id: number) => http.delete(`/admin/announcements/${id}`);
export const publishAnnouncement = (id: number) => http.put(`/admin/announcements/${id}/publish`);
export const takeDownAnnouncement = (id: number) => http.put(`/admin/announcements/${id}/takedown`);

// 活动管理
export const getActivityList = (params: any) => http.get("/activities", params);

// 统计
export const getDashboardStats = () => http.get("/admin/stats/dashboard");
export const getOrderStats = (params: any) => http.get("/admin/stats/orders", params);
export const getDishRanking = (params: any) => http.get("/admin/stats/dishes", params);
export const getRevenueStats = (params: any) => http.get("/admin/stats/revenue", params);

// 系统配置
export const getConfigList = () => http.get("/admin/configs");
export const updateConfig = (data: any) => http.put("/admin/configs", data);
