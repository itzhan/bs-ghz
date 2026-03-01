<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="订单号" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="待付款" :value="0" />
            <el-option label="已付款" :value="1" />
            <el-option label="制作中" :value="2" />
            <el-option label="待取餐" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
            <el-option label="已退款" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="userId" label="用户ID" width="90" align="center" />
        <el-table-column prop="stallId" label="档口ID" width="90" align="center" />
        <el-table-column prop="totalAmount" label="总金额" width="100" align="right">
          <template #default="{ row }">¥{{ (row.totalAmount ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" width="100" align="right">
          <template #default="{ row }">¥{{ (row.actualAmount ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="orderStatusTag(row.status).type">{{ orderStatusTag(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" align="center">
          <template #default="{ row }">{{ payMethodText(row.paymentMethod) }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="130" align="center" fixed="right">
          <template #default="{ row }">
            <el-dropdown @command="(cmd: number) => handleUpdateStatus(row.id, cmd)" trigger="click">
              <el-button type="primary" link>
                更新状态<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="1" :disabled="row.status >= 1">已付款</el-dropdown-item>
                  <el-dropdown-item :command="2" :disabled="row.status >= 2">制作中</el-dropdown-item>
                  <el-dropdown-item :command="3" :disabled="row.status >= 3">待取餐</el-dropdown-item>
                  <el-dropdown-item :command="4" :disabled="row.status >= 4">已完成</el-dropdown-item>
                  <el-dropdown-item :command="5">已取消</el-dropdown-item>
                  <el-dropdown-item :command="6">已退款</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="mt-pagination"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @change="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts" name="orderList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { ArrowDown } from "@element-plus/icons-vue";
import { getOrderList, updateOrderStatus } from "@/api/modules/admin";

const loading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  keyword: "",
  status: undefined as number | undefined
});

const orderStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "待付款", type: "info" },
    1: { label: "已付款", type: "" },
    2: { label: "制作中", type: "warning" },
    3: { label: "待取餐", type: "warning" },
    4: { label: "已完成", type: "success" },
    5: { label: "已取消", type: "danger" },
    6: { label: "已退款", type: "info" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const payMethodText = (method: number | string) => {
  const map: Record<number, string> = { 1: "余额支付", 2: "微信支付", 3: "支付宝" };
  return map[Number(method)] || method || "-";
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getOrderList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    });
    tableData.value = res.data.records || [];
    pagination.total = res.data.total || 0;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchData();
};

const handleReset = () => {
  searchForm.keyword = "";
  searchForm.status = undefined;
  handleSearch();
};

const handleUpdateStatus = async (id: number, status: number) => {
  try {
    await ElMessageBox.confirm(`确定将订单状态更新为"${orderStatusTag(status).label}"？`, "操作确认", { type: "warning" });
    await updateOrderStatus(id, status);
    ElMessage.success("状态更新成功");
    fetchData();
  } catch (e) {
    // cancelled or error
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.search-form {
  margin-bottom: 15px;
}
.mt-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}
</style>
