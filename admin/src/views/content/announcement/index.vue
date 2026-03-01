<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="openDialog('add')">新增公告</el-button>
      </div>

      <!-- 搜索 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
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
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.type === 0" size="small">系统</el-tag>
            <el-tag v-else-if="row.type === 1" type="warning" size="small">商户</el-tag>
            <el-tag v-else size="small" type="info">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="announcementStatusTag(row.status).type">{{ announcementStatusTag(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" min-width="170">
          <template #default="{ row }">{{ row.publishedAt || "-" }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog('edit', row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handlePublish(row.id)">发布</el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handleTakeDown(row.id)">下架</el-button>
            <el-popconfirm title="确定删除该公告？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
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

      <!-- 新增/编辑弹窗 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
        <el-form :model="formData" label-width="80px" :rules="formRules" ref="formRef">
          <el-form-item label="标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入公告标题" />
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择类型">
              <el-option label="系统公告" :value="0" />
              <el-option label="商户公告" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
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

<script setup lang="ts" name="announcementList">
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";
import {
  getAnnouncementList,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  publishAnnouncement,
  takeDownAnnouncement
} from "@/api/modules/admin";

const loading = ref(false);
const submitLoading = ref(false);
const tableData = ref<any[]>([]);
const pagination = reactive({ page: 1, size: 10, total: 0 });
const searchForm = reactive({ status: undefined as number | undefined });

const dialogVisible = ref(false);
const dialogTitle = ref("新增公告");
const dialogMode = ref<"add" | "edit">("add");
const editId = ref(0);
const formRef = ref<FormInstance>();

const formData = reactive({ title: "", type: 0, content: "" });
const formRules: FormRules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }]
};

const announcementStatusTag = (status: number) => {
  const map: Record<number, { label: string; type: "" | "success" | "warning" | "danger" | "info" }> = {
    0: { label: "草稿", type: "info" },
    1: { label: "已发布", type: "success" },
    2: { label: "已下架", type: "warning" }
  };
  return map[status] || { label: "未知", type: "info" };
};

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getAnnouncementList({
      page: pagination.page,
      size: pagination.size,
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
  searchForm.status = undefined;
  handleSearch();
};

const openDialog = (mode: "add" | "edit", row?: any) => {
  dialogMode.value = mode;
  if (mode === "add") {
    dialogTitle.value = "新增公告";
    formData.title = "";
    formData.type = 0;
    formData.content = "";
    editId.value = 0;
  } else {
    dialogTitle.value = "编辑公告";
    formData.title = row.title;
    formData.type = row.type;
    formData.content = row.content;
    editId.value = row.id;
  }
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitLoading.value = true;
  try {
    if (dialogMode.value === "add") {
      await createAnnouncement({ ...formData });
      ElMessage.success("新增成功");
    } else {
      await updateAnnouncement(editId.value, { ...formData });
      ElMessage.success("编辑成功");
    }
    dialogVisible.value = false;
    fetchData();
  } catch (e) {
    console.error(e);
  } finally {
    submitLoading.value = false;
  }
};

const handlePublish = async (id: number) => {
  try {
    await publishAnnouncement(id);
    ElMessage.success("发布成功");
    fetchData();
  } catch (e) {
    console.error(e);
  }
};

const handleTakeDown = async (id: number) => {
  try {
    await takeDownAnnouncement(id);
    ElMessage.success("已下架");
    fetchData();
  } catch (e) {
    console.error(e);
  }
};

const handleDelete = async (id: number) => {
  try {
    await deleteAnnouncement(id);
    ElMessage.success("删除成功");
    fetchData();
  } catch (e) {
    console.error(e);
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.toolbar {
  margin-bottom: 15px;
}
.search-form {
  margin-bottom: 15px;
}
.mt-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}
</style>
