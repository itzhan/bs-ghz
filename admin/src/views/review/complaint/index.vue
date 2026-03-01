<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已解决" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="食品质量" :value="1" />
            <el-option label="服务态度" :value="2" />
            <el-option label="环境卫生" :value="3" />
            <el-option label="其他" :value="4" />
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
        <el-table-column prop="type" label="投诉类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="complaintTypeTag(row.type).type" size="small">
              {{ complaintTypeTag(row.type).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="complaintStatusTag(row.status).type">{{ complaintStatusTag(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复内容" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ row.reply || "-" }}</template>
        </el-table-column>
        <el-table-column prop="handledAt" label="处理时间" min-width="170">
          <template #default="{ row }">{{ row.handledAt || "-" }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0 || row.status === 1" type="primary" link @click="openHandleDialog(row)">
              处理
            </el-button>
            <span v-else style="color: #909399">已处理</span>
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

      <!-- 处理弹窗 -->
      <el-dialog v-model="dialogVisible" title="处理投诉" width="500px" destroy-on-close>
        <el-form :model="handleForm" label-width="80px">
          <el-form-item label="处理状态">
            <el-select v-model="handleForm.status" placeholder="请选择">
              <el-option label="处理中" :value="1" />
              <el-option label="已解决" :value="2" />
              <el-option label="已驳回" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="回复内容">
            <el-input v-model="handleForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitHandle" :loading="submitLoading">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts" name="complaintList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getComplaintList, handleComplaint } from "@/api/modules/admin";

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  status: undefined as number | undefined,
  type: undefined as number | undefined
});

const dialogVisible = ref(false);
const currentId = ref(0);
const handleForm = reactive({ status: 2, reply: "" });

const complaintTypeTag = (type: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    1: { label: "食品质量", type: "danger" },
    2: { label: "服务态度", type: "warning" },
    3: { label: "环境卫生", type: "" },
    4: { label: "其他", type: "info" }
  };
  return map[type] || { label: "未知", type: "info" };
};

const complaintStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "待处理", type: "info" },
    1: { label: "处理中", type: "warning" },
    2: { label: "已解决", type: "success" },
    3: { label: "已驳回", type: "danger" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getComplaintList({
      page: pagination.page,
      size: pagination.size,
      status: searchForm.status,
      type: searchForm.type
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
  searchForm.status = undefined;
  searchForm.type = undefined;
  handleSearch();
};

const openHandleDialog = (row: any) => {
  currentId.value = row.id;
  handleForm.status = 2;
  handleForm.reply = "";
  dialogVisible.value = true;
};

const submitHandle = async () => {
  submitLoading.value = true;
  try {
    await handleComplaint(currentId.value, { status: handleForm.status, reply: handleForm.reply });
    ElMessage.success("处理成功");
    dialogVisible.value = false;
    fetchData();
  } catch (e) {
    console.error(e);
  } finally {
    submitLoading.value = false;
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
