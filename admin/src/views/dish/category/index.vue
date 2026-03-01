<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="档口ID">
          <el-input v-model="searchForm.stallId" placeholder="请输入档口ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 提示 -->
      <el-empty v-if="!searchForm.stallId && !tableData.length" description="请输入档口ID后搜索" />

      <!-- 表格 -->
      <el-table v-else :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="分类名" min-width="160" />
        <el-table-column prop="stallName" label="所属档口" min-width="140">
          <template #default="{ row }">{{ row.stallName || row.stallId || "-" }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该分类？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 编辑弹窗 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" destroy-on-close>
        <el-form :model="formData" label-width="80px" :rules="formRules" ref="formRef">
          <el-form-item label="分类名" prop="name">
            <el-input v-model="formData.name" placeholder="请输入分类名" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="formData.sort" :min="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts" name="dishCategory">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { getDishCategories } from "@/api/modules/admin";
import http from "@/api";

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<any[]>([]);
const searchForm = reactive({ stallId: "" });

const dialogVisible = ref(false);
const dialogTitle = ref("编辑分类");
const editId = ref(0);
const formRef = ref<FormInstance>();
const formData = reactive({ name: "", sort: 0 });
const formRules: FormRules = {
  name: [{ required: true, message: "请输入分类名", trigger: "blur" }]
};

const fetchData = async () => {
  if (!searchForm.stallId) {
    tableData.value = [];
    return;
  }
  loading.value = true;
  try {
    const res = await getDishCategories({ stallId: searchForm.stallId });
    const data = res.data;
    tableData.value = Array.isArray(data) ? data : data?.records || data?.list || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  fetchData();
};

const handleReset = () => {
  searchForm.stallId = "";
  tableData.value = [];
};

const openEditDialog = (row: any) => {
  dialogTitle.value = "编辑分类";
  editId.value = row.id;
  formData.name = row.name;
  formData.sort = row.sort || 0;
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    await http.put(`/merchant/categories/${editId.value}`, { ...formData });
    ElMessage.success("编辑成功");
    dialogVisible.value = false;
    fetchData();
  } catch (e) {
    console.error(e);
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = async (id: number) => {
  try {
    await http.delete(`/merchant/categories/${id}`);
    ElMessage.success("删除成功");
    fetchData();
  } catch (e) {
    console.error(e);
  }
};
</script>

<style scoped lang="scss">
.search-form {
  margin-bottom: 15px;
}
</style>
