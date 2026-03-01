<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/昵称/手机" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="普通用户" :value="0" />
            <el-option label="商户" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <el-table-column prop="username" label="用户名" min-width="100" />
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="phone" label="手机" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.role === 0">普通用户</el-tag>
            <el-tag v-else-if="row.role === 1" type="warning">商户</el-tag>
            <el-tag v-else-if="row.role === 2" type="danger">管理员</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
              :disabled="row.role === 2"
            />
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="100" align="right">
          <template #default="{ row }">¥{{ (row.balance ?? 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该用户？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link :disabled="row.role === 2">删除</el-button>
              </template>
            </el-popconfirm>
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
      <el-dialog v-model="dialogVisible" title="编辑用户" width="500px" destroy-on-close>
        <el-form :model="editForm" label-width="80px" :rules="editRules" ref="editFormRef">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="editForm.nickname" placeholder="昵称" />
          </el-form-item>
          <el-form-item label="手机">
            <el-input v-model="editForm.phone" placeholder="手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="邮箱" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="editForm.role">
              <el-option label="普通用户" :value="0" />
              <el-option label="商户" :value="1" />
              <el-option label="管理员" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="editForm.status">
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
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

<script setup lang="ts" name="userList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import { getUserList, updateUserStatus, deleteUser } from "@/api/modules/admin";
import http from "@/api";

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });

const searchForm = reactive({
  keyword: "",
  role: undefined as number | undefined,
  status: undefined as number | undefined
});

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getUserList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      role: searchForm.role,
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
  searchForm.role = undefined;
  searchForm.status = undefined;
  handleSearch();
};

const handleStatusChange = async (row: any) => {
  try {
    await updateUserStatus(row.id, row.status);
    ElMessage.success("状态更新成功");
  } catch (e) {
    row.status = row.status === 1 ? 0 : 1;
  }
};

const handleDelete = async (id: number) => {
  try {
    await deleteUser(id);
    ElMessage.success("删除成功");
    fetchData();
  } catch (e) {
    console.error(e);
  }
};

// 编辑用户
const dialogVisible = ref(false);
const editId = ref(0);
const editFormRef = ref<FormInstance>();
const editForm = reactive({ nickname: "", phone: "", email: "", role: 0, status: 1 });
const editRules: FormRules = {
  nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }]
};

const openEditDialog = (row: any) => {
  editId.value = row.id;
  editForm.nickname = row.nickname || "";
  editForm.phone = row.phone || "";
  editForm.email = row.email || "";
  editForm.role = row.role ?? 0;
  editForm.status = row.status ?? 1;
  dialogVisible.value = true;
};

const submitEdit = async () => {
  if (!editFormRef.value) return;
  await editFormRef.value.validate();
  submitLoading.value = true;
  try {
    await http.put(`/admin/users/${editId.value}`, { ...editForm });
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
