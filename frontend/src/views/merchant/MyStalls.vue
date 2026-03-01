<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">我的档口</h1>
      <n-button type="primary" @click="openCreateModal">+ 新建档口</n-button>
    </div>

    <n-spin :show="loading">
      <div v-if="stalls.length" class="stall-grid">
        <div v-for="stall in stalls" :key="stall.id" class="stall-card">
          <div class="stall-card-header">
            <div class="stall-name">{{ stall.name }}</div>
            <n-tag :type="statusTagType(stall.status)" size="small">{{ statusText(stall.status) }}</n-tag>
          </div>
          <div class="stall-info">
            <div v-if="stall.location" class="info-row">📍 {{ stall.location }}</div>
            <div v-if="stall.phone" class="info-row">📞 {{ stall.phone }}</div>
            <div v-if="stall.businessHours" class="info-row">🕐 {{ stall.businessHours }}</div>
          </div>
          <div class="stall-stats">
            <span>⭐ {{ stall.avgRating?.toFixed(1) || '暂无' }}</span>
            <span>月售 {{ stall.monthlySales || 0 }}</span>
          </div>
          <div class="stall-actions">
            <n-button size="small" type="primary" @click="router.push(`/merchant/stalls/${stall.id}`)">管理</n-button>
            <n-button size="small" @click="openEditModal(stall)">编辑</n-button>
            <n-dropdown :options="statusOptions(stall.status)" @select="(v: number) => handleStatusChange(stall.id, v)" trigger="click">
              <n-button size="small" quaternary>状态 ▾</n-button>
            </n-dropdown>
          </div>
        </div>
      </div>
      <n-empty v-else-if="!loading" description="还没有档口，点击上方按钮创建" style="padding: 80px 0;" />
    </n-spin>

    <!-- 创建 / 编辑 弹窗 -->
    <n-modal v-model:show="showModal" preset="card" :title="editingStall ? '编辑档口' : '新建档口'" style="max-width: 520px;">
      <n-form ref="formRef" :model="formData" :rules="formRules" label-placement="left" label-width="80px">
        <n-form-item label="名称" path="name">
          <n-input v-model:value="formData.name" placeholder="档口名称" />
        </n-form-item>
        <n-form-item label="描述">
          <n-input v-model:value="formData.description" type="textarea" :rows="2" placeholder="档口描述" />
        </n-form-item>
        <n-form-item label="位置">
          <n-input v-model:value="formData.location" placeholder="如：一楼A区" />
        </n-form-item>
        <n-form-item label="电话">
          <n-input v-model:value="formData.phone" placeholder="联系电话" />
        </n-form-item>
        <n-form-item label="营业时间">
          <n-input v-model:value="formData.businessHours" placeholder="如：07:00-21:00" />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { getMyStalls, createStall, updateStall, updateStallStatus } from '@/api/merchant'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const stalls = ref<any[]>([])

// 状态映射
function statusText(status: number) {
  return ({ 0: '待审核', 1: '营业中', 2: '休息中', 3: '已关闭' } as Record<number, string>)[status] || '未知'
}
function statusTagType(status: number): 'default' | 'success' | 'warning' | 'error' {
  return ({ 0: 'default', 1: 'success', 2: 'warning', 3: 'error' } as Record<number, any>)[status] || 'default'
}
function statusOptions(current: number) {
  const all = [
    { label: '营业中', key: 1 },
    { label: '休息中', key: 2 },
    { label: '已关闭', key: 3 },
  ]
  return all.filter(o => o.key !== current)
}

async function loadStalls() {
  loading.value = true
  try {
    const res: any = await getMyStalls()
    if (res.code === 200 || res.code === 0) {
      stalls.value = res.data || []
    }
  } catch {
    message.error('加载档口列表失败')
  } finally {
    loading.value = false
  }
}

async function handleStatusChange(id: number, status: number) {
  try {
    const res: any = await updateStallStatus(id, status)
    if (res.code === 200 || res.code === 0) {
      message.success('状态已更新')
      loadStalls()
    } else {
      message.error(res.message || '操作失败')
    }
  } catch {
    message.error('操作失败')
  }
}

// 表单
const showModal = ref(false)
const submitting = ref(false)
const editingStall = ref<any>(null)
const formRef = ref<FormInst | null>(null)
const formData = reactive({
  name: '',
  description: '',
  location: '',
  phone: '',
  businessHours: '',
})
const formRules: FormRules = {
  name: [{ required: true, message: '请输入档口名称', trigger: 'blur' }],
}

function openCreateModal() {
  editingStall.value = null
  Object.assign(formData, { name: '', description: '', location: '', phone: '', businessHours: '' })
  showModal.value = true
}

function openEditModal(stall: any) {
  editingStall.value = stall
  Object.assign(formData, {
    name: stall.name || '',
    description: stall.description || '',
    location: stall.location || '',
    phone: stall.phone || '',
    businessHours: stall.businessHours || '',
  })
  showModal.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    const payload = { ...formData }
    let res: any
    if (editingStall.value) {
      res = await updateStall(editingStall.value.id, payload)
    } else {
      res = await createStall(payload)
    }
    if (res.code === 200 || res.code === 0) {
      message.success(editingStall.value ? '编辑成功' : '创建成功')
      showModal.value = false
      loadStalls()
    } else {
      message.error(res.message || '操作失败')
    }
  } catch {
    message.error('操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadStalls)
</script>

<style scoped>
.page-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1E293B;
}
.stall-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.stall-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.stall-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stall-name {
  font-size: 18px;
  font-weight: 600;
  color: #1E293B;
}
.stall-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-row {
  font-size: 13px;
  color: #64748B;
}
.stall-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #94A3B8;
}
.stall-actions {
  display: flex;
  gap: 8px;
  padding-top: 4px;
  border-top: 1px solid #F1F5F9;
}
</style>
