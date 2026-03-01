<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-row">
        <h1 class="page-title">我的投诉</h1>
        <n-button type="primary" @click="showCreateModal = true">
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          发起投诉
        </n-button>
      </div>
    </div>

    <n-spin :show="loading">
      <div v-if="complaints.length" class="complaint-list">
        <n-card v-for="item in complaints" :key="item.id" style="margin-bottom: 12px;">
          <div class="complaint-header">
            <div class="complaint-meta">
              <n-tag size="small" :type="getTypeColor(item.type)">{{ item.type || '其他' }}</n-tag>
              <span class="complaint-time">{{ item.createTime }}</span>
            </div>
            <n-tag
              :type="getStatusColor(item.status)"
              size="small"
            >
              {{ getStatusText(item.status) }}
            </n-tag>
          </div>
          <p class="complaint-content">{{ item.content }}</p>
          <div class="complaint-reply" v-if="item.reply">
            <span class="reply-label">回复：</span>
            <span>{{ item.reply }}</span>
          </div>
          <div class="complaint-target" v-if="item.stallName || item.orderNo">
            <span v-if="item.stallName">档口：{{ item.stallName }}</span>
            <span v-if="item.orderNo">订单：{{ item.orderNo }}</span>
          </div>
        </n-card>
      </div>
      <n-empty v-else-if="!loading" description="暂无投诉记录" style="padding: 60px 0;" />
    </n-spin>

    <div class="pagination-wrap" v-if="total > pageSize">
      <n-pagination
        v-model:page="page"
        :page-size="pageSize"
        :item-count="total"
        @update:page="loadComplaints"
      />
    </div>

    <!-- Create Complaint Modal -->
    <n-modal v-model:show="showCreateModal" preset="card" title="发起投诉" style="max-width: 520px;">
      <n-form ref="complaintFormRef" :model="complaintForm" :rules="complaintRules" label-placement="top">
        <n-form-item label="投诉类型" path="type">
          <n-select
            v-model:value="complaintForm.type"
            :options="typeOptions"
            placeholder="选择投诉类型"
          />
        </n-form-item>
        <n-form-item label="投诉内容" path="content">
          <n-input
            v-model:value="complaintForm.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的问题..."
          />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space justify="end">
          <n-button @click="showCreateModal = false">取消</n-button>
          <n-button type="primary" :loading="createLoading" @click="handleCreate">
            提交投诉
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { AddOutline } from '@vicons/ionicons5'
import { getMyComplaints, createComplaint } from '@/api/complaint'

const message = useMessage()
const loading = ref(false)
const complaints = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showCreateModal = ref(false)
const createLoading = ref(false)
const complaintFormRef = ref<FormInst | null>(null)

const complaintForm = reactive({
  type: '',
  content: '',
})

const typeOptions = [
  { label: '食品质量', value: '食品质量' },
  { label: '服务态度', value: '服务态度' },
  { label: '环境卫生', value: '环境卫生' },
  { label: '配送问题', value: '配送问题' },
  { label: '其他', value: '其他' },
]

const complaintRules: FormRules = {
  type: [{ required: true, message: '请选择投诉类型', trigger: 'change' }],
  content: [
    { required: true, message: '请输入投诉内容', trigger: 'blur' },
    { min: 10, message: '投诉内容不少于10个字', trigger: 'blur' },
  ],
}

function getTypeColor(type: string): 'default' | 'info' | 'success' | 'warning' | 'error' {
  const map: Record<string, 'default' | 'info' | 'success' | 'warning' | 'error'> = {
    '食品质量': 'error',
    '服务态度': 'warning',
    '环境卫生': 'info',
    '配送问题': 'warning',
  }
  return map[type] || 'default'
}

function getStatusColor(status: string | number): 'default' | 'info' | 'success' | 'warning' | 'error' {
  const map: Record<string, 'default' | 'info' | 'success' | 'warning' | 'error'> = {
    pending: 'warning', '0': 'warning',
    processing: 'info', '1': 'info',
    resolved: 'success', '2': 'success',
    rejected: 'error', '3': 'error',
  }
  return map[String(status)] || 'default'
}

function getStatusText(status: string | number): string {
  const map: Record<string, string> = {
    pending: '待处理', '0': '待处理',
    processing: '处理中', '1': '处理中',
    resolved: '已解决', '2': '已解决',
    rejected: '已驳回', '3': '已驳回',
  }
  return map[String(status)] || '待处理'
}

async function loadComplaints() {
  loading.value = true
  try {
    const res: any = await getMyComplaints({ page: page.value, pageSize: pageSize.value })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      complaints.value = data?.records || data?.list || data || []
      total.value = data?.total || complaints.value.length
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  try {
    await complaintFormRef.value?.validate()
  } catch {
    return
  }
  createLoading.value = true
  try {
    const res: any = await createComplaint({
      type: complaintForm.type,
      content: complaintForm.content,
    })
    if (res.code === 200 || res.code === 0) {
      message.success('投诉已提交')
      showCreateModal.value = false
      complaintForm.type = ''
      complaintForm.content = ''
      loadComplaints()
    } else {
      message.error(res.message || '提交失败')
    }
  } catch {
    message.error('提交失败')
  } finally {
    createLoading.value = false
  }
}

onMounted(() => {
  loadComplaints()
})
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}

.page-header {
  margin-bottom: 24px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1E293B;
}

.complaint-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.complaint-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.complaint-time {
  font-size: 12px;
  color: #94A3B8;
}

.complaint-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  margin-bottom: 8px;
}

.complaint-reply {
  background: #F5F5F0;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 13px;
  color: #475569;
  margin-bottom: 8px;
}

.reply-label {
  font-weight: 600;
  color: #2D6A4F;
}

.complaint-target {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #94A3B8;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
