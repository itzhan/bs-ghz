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
            <el-option label="正常" :value="1" />
            <el-option label="隐藏" :value="0" />
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
        <el-table-column prop="userId" label="用户ID" width="90" align="center" />
        <el-table-column prop="stallId" label="档口ID" width="90" align="center" />
        <el-table-column prop="rating" label="评分" width="160" align="center">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled :colors="['#F56C6C', '#E6A23C', '#67C23A']" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reply" label="商户回复" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ row.reply || "-" }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "正常" : "隐藏" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link @click="handleToggleStatus(row)">
              {{ row.status === 1 ? "隐藏" : "显示" }}
            </el-button>
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

<script setup lang="ts" name="reviewList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getReviewList, updateReviewStatus } from "@/api/modules/admin";

const loading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  stallId: "",
  status: undefined as number | undefined
});

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getReviewList({
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

const handleToggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1;
  try {
    await updateReviewStatus(row.id, newStatus);
    row.status = newStatus;
    ElMessage.success("操作成功");
  } catch (e) {
    console.error(e);
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
