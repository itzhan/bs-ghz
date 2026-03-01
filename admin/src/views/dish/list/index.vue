<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="菜品名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="档口ID">
          <el-input v-model="searchForm.stallId" placeholder="档口ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="待审核" :value="0" />
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
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
        <el-table-column prop="name" label="菜品名" min-width="130" />
        <el-table-column prop="stallName" label="所属档口" min-width="120">
          <template #default="{ row }">{{ row.stallName || row.stallId }}</template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100">
          <template #default="{ row }">{{ row.categoryName || row.categoryId || "-" }}</template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="90" align="right">
          <template #default="{ row }">¥{{ (row.price ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="90" align="right">
          <template #default="{ row }">
            <span v-if="row.originalPrice">¥{{ row.originalPrice.toFixed(2) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="dishStatusTag(row.status).type">{{ dishStatusTag(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column prop="monthlySales" label="月销量" width="90" align="center" />
        <el-table-column prop="isRecommended" label="推荐" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isRecommended" type="success" size="small">是</el-tag>
            <el-tag v-else type="info" size="small">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <template v-if="row.status === 0">
              <el-button type="success" link @click="handleAudit(row.id, 1)">通过</el-button>
              <el-button type="danger" link @click="handleAudit(row.id, 2)">拒绝</el-button>
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
      <el-dialog v-model="dialogVisible" title="编辑菜品" width="520px" destroy-on-close>
        <el-form :model="editForm" label-width="80px" :rules="editRules" ref="editFormRef">
          <el-form-item label="菜品名" prop="name">
            <el-input v-model="editForm.name" placeholder="菜品名称" />
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input-number v-model="editForm.price" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="原价">
            <el-input-number v-model="editForm.originalPrice" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="库存">
            <el-input-number v-model="editForm.stock" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="editForm.status">
              <el-option label="待审核" :value="0" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="推荐">
            <el-switch v-model="editForm.isRecommended" :active-value="1" :inactive-value="0" />
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

<script setup lang="ts" name="dishList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { getDishList, auditDish } from "@/api/modules/admin";
import http from "@/api";

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({
  keyword: "",
  stallId: "",
  status: undefined as number | undefined
});

const dishStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "待审核", type: "info" },
    1: { label: "上架", type: "success" },
    2: { label: "下架", type: "danger" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getDishList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
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
  searchForm.keyword = "";
  searchForm.stallId = "";
  searchForm.status = undefined;
  handleSearch();
};

const handleAudit = async (id: number, status: number) => {
  const actionText = status === 1 ? "通过" : "拒绝";
  try {
    await ElMessageBox.confirm(`确定${actionText}该菜品？`, "审核确认", { type: "warning" });
    await auditDish(id, status);
    ElMessage.success(`已${actionText}`);
    fetchData();
  } catch (e) {
    // cancelled or error
  }
};

// 编辑菜品
const dialogVisible = ref(false);
const editId = ref(0);
const editStallId = ref(0);
const editFormRef = ref<FormInstance>();
const editForm = reactive({
  name: "",
  price: 0,
  originalPrice: null as number | null,
  stock: null as number | null,
  status: 1,
  isRecommended: 0
});
const editRules: FormRules = {
  name: [{ required: true, message: "请输入菜品名", trigger: "blur" }],
  price: [{ required: true, type: "number", message: "请输入价格", trigger: "blur" }]
};

const openEditDialog = (row: any) => {
  editId.value = row.id;
  editStallId.value = row.stallId;
  editForm.name = row.name || "";
  editForm.price = row.price ?? 0;
  editForm.originalPrice = row.originalPrice;
  editForm.stock = row.stock;
  editForm.status = row.status ?? 1;
  editForm.isRecommended = row.isRecommended ?? 0;
  dialogVisible.value = true;
};

const submitEdit = async () => {
  if (!editFormRef.value) return;
  await editFormRef.value.validate();
  submitLoading.value = true;
  try {
    await http.put(`/admin/dishes/${editId.value}`, { stallId: editStallId.value, ...editForm });
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
