<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单ID">
          <el-input v-model="searchForm.orderId" placeholder="订单ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
            <el-option label="支付失败" :value="3" />
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
        <el-table-column prop="orderId" label="订单ID" width="90" align="center" />
        <el-table-column prop="paymentNo" label="支付单号" min-width="200" />
        <el-table-column prop="amount" label="金额" width="100" align="right">
          <template #default="{ row }">¥{{ (row.amount ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="payMethodTag(row.paymentMethod).type" size="small">
              {{ payMethodTag(row.paymentMethod).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="payStatusTag(row.status).type">{{ payStatusTag(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paidAt" label="支付时间" min-width="170" />
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
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

<script setup lang="ts" name="paymentList">
import { ref, reactive, onMounted } from "vue";
import { getPaymentList } from "@/api/modules/admin";

const loading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  orderId: "",
  status: undefined as number | undefined
});

const payMethodTag = (method: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    1: { label: "余额支付", type: "" },
    2: { label: "微信支付", type: "success" },
    3: { label: "支付宝", type: "" }
  };
  return map[method] || { label: "其他", type: "info" };
};

const payStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "待支付", type: "info" },
    1: { label: "已支付", type: "success" },
    2: { label: "已退款", type: "warning" },
    3: { label: "支付失败", type: "danger" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getPaymentList({
      page: pagination.page,
      size: pagination.size,
      orderId: searchForm.orderId || undefined,
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
  searchForm.orderId = "";
  searchForm.status = undefined;
  handleSearch();
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
