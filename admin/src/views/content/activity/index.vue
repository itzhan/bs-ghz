<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="档口ID">
          <el-input v-model="searchForm.stallId" placeholder="档口ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
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
        <el-table-column prop="stallId" label="档口ID" width="90" align="center" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="discountType" label="折扣类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="discountTypeTag(row.discountType).type" size="small">
              {{ discountTypeTag(row.discountType).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discountValue" label="折扣值" width="90" align="center" />
        <el-table-column prop="minSpend" label="最低消费" width="100" align="right">
          <template #default="{ row }">¥{{ (row.minSpend ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" min-width="170" />
        <el-table-column prop="endTime" label="结束时间" min-width="170" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="activityStatusTag(row.status).type">{{ activityStatusTag(row.status).label }}</el-tag>
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

<script setup lang="ts" name="activityList">
import { ref, reactive, onMounted } from "vue";
import { getActivityList } from "@/api/modules/admin";

const loading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  stallId: "",
  status: undefined as number | undefined
});

const discountTypeTag = (type: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    1: { label: "满减", type: "danger" },
    2: { label: "折扣", type: "warning" },
    3: { label: "赠品", type: "success" }
  };
  return map[type] || { label: "未知", type: "info" };
};

const activityStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "未开始", type: "info" },
    1: { label: "进行中", type: "success" },
    2: { label: "已结束", type: "danger" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getActivityList({
      page: pagination.page,
      size: pagination.size,
      stallId: searchForm.stallId || undefined,
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
  searchForm.stallId = "";
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
