<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="档口名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="待审核" :value="0" />
            <el-option label="营业中" :value="1" />
            <el-option label="休息中" :value="2" />
            <el-option label="已关闭" :value="3" />
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
        <el-table-column prop="name" label="档口名称" min-width="140" />
        <el-table-column prop="ownerName" label="所属商户" min-width="100">
          <template #default="{ row }">{{ row.ownerName || row.ownerId }}</template>
        </el-table-column>
        <el-table-column prop="location" label="位置" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="120" />
        <el-table-column prop="businessHours" label="营业时间" min-width="140" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="stallStatusTag(row.status).type">{{ stallStatusTag(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="80" align="center">
          <template #default="{ row }">{{ (row.rating ?? 0).toFixed(1) }}</template>
        </el-table-column>
        <el-table-column prop="monthlySales" label="月销量" width="90" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <template v-if="row.status === 0">
              <el-button type="success" link @click="handleAudit(row.id, 1)">通过</el-button>
              <el-button type="danger" link @click="handleAudit(row.id, 3)">拒绝</el-button>
            </template>
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

      <!-- 编辑弹窗 -->
      <el-dialog v-model="dialogVisible" title="编辑档口" width="500px" destroy-on-close>
        <el-form :model="editForm" label-width="80px" :rules="editRules" ref="editFormRef">
          <el-form-item label="名称" prop="name">
            <el-input v-model="editForm.name" placeholder="档口名称" />
          </el-form-item>
          <el-form-item label="位置">
            <el-input v-model="editForm.location" placeholder="如：一楼A区" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="editForm.phone" placeholder="联系电话" />
          </el-form-item>
          <el-form-item label="营业时间">
            <el-input v-model="editForm.businessHours" placeholder="如07:00-21:00" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="editForm.status">
              <el-option label="待审核" :value="0" />
              <el-option label="营业中" :value="1" />
              <el-option label="休息中" :value="2" />
              <el-option label="已关闭" :value="3" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitLoading">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts" name="stallList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { getStallList, auditStall } from "@/api/modules/admin";
import http from "@/api";

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  keyword: "",
  status: undefined as number | undefined
});

const stallStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "待审核", type: "info" },
    1: { label: "营业中", type: "success" },
    2: { label: "休息中", type: "warning" },
    3: { label: "已关闭", type: "danger" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getStallList({
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

const handleAudit = async (id: number, status: number) => {
  const actionText = status === 1 ? "通过" : "拒绝";
  try {
    await ElMessageBox.confirm(`确定${actionText}该档口？`, "审核确认", { type: "warning" });
    await auditStall(id, status);
    ElMessage.success(`已${actionText}`);
    fetchData();
  } catch (e) {
    // cancelled or error
  }
};

// 编辑档口
const dialogVisible = ref(false);
const editId = ref(0);
const editFormRef = ref<FormInstance>();
const editForm = reactive({ name: "", location: "", phone: "", businessHours: "", status: 1 });
const editRules: FormRules = {
  name: [{ required: true, message: "请输入档口名称", trigger: "blur" }]
};

const openEditDialog = (row: any) => {
  editId.value = row.id;
  editForm.name = row.name || "";
  editForm.location = row.location || "";
  editForm.phone = row.phone || "";
  editForm.businessHours = row.businessHours || "";
  editForm.status = row.status ?? 1;
  dialogVisible.value = true;
};

const submitEdit = async () => {
  if (!editFormRef.value) return;
  await editFormRef.value.validate();
  submitLoading.value = true;
  try {
    await http.put(`/admin/stalls/${editId.value}`, { ...editForm });
    ElMessage.success("编辑成功");
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
