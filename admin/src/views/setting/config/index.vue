<template>
  <div class="table-box">
    <div class="card table-main">
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="configKey" label="配置项" min-width="180" />
        <el-table-column prop="configValue" label="配置值" min-width="220">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-input v-model="editForm.configValue" size="small" />
            </template>
            <template v-else>{{ row.configValue }}</template>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-button type="success" link @click="saveEdit(row)" :loading="saveLoading">保存</el-button>
              <el-button link @click="cancelEdit">取消</el-button>
            </template>
            <template v-else>
              <el-button type="primary" link @click="startEdit(row)">编辑</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts" name="sysConfig">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getConfigList, updateConfig } from "@/api/modules/admin";

const loading = ref(false);
const saveLoading = ref(false);
const tableData = ref<any[]>([]);

const editingId = ref<number | null>(null);
const editForm = ref({ configKey: "", configValue: "" });

const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getConfigList();
    // data could be array directly or paginated
    tableData.value = Array.isArray(res.data) ? res.data : res.data.records || [];
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const startEdit = (row: any) => {
  editingId.value = row.id;
  editForm.value = { configKey: row.configKey, configValue: row.configValue };
};

const cancelEdit = () => {
  editingId.value = null;
};

const saveEdit = async (row: any) => {
  saveLoading.value = true;
  try {
    await updateConfig({ id: row.id, configKey: editForm.value.configKey, configValue: editForm.value.configValue });
    row.configValue = editForm.value.configValue;
    editingId.value = null;
    ElMessage.success("保存成功");
  } catch (e) {
    console.error(e);
  } finally {
    saveLoading.value = false;
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.mt-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}
</style>
