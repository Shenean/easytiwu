<template>
  <n-card title="题库列表" class="bank-card" :segmented="{ content: true }" size="large">
    <n-data-table
        :columns="columns"
        :data="banks"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
    />
  </n-card>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import type { DataTableColumns } from 'naive-ui'
import { useMessage, NButton, NSpace, useDialog } from 'naive-ui'
import axios from 'axios'

interface QuestionBank {
  id: number
  name: string
  description: string
  total_count: number
  completed_count: number
  wrong_count: number
}

const message = useMessage()
const dialog = useDialog()
const banks = ref<QuestionBank[]>([])
const loading = ref(false)

const pagination = {
  pageSize: 10
}

/**
 * 表格列定义
 */
const columns: DataTableColumns<QuestionBank> = [
  {
    title: 'ID',
    key: 'id',
    width: 70
  },
  {
    title: '题库名',
    key: 'name',
    ellipsis: true
  },
  {
    title: '描述',
    key: 'description',
    ellipsis: true
  },
  {
    title: '总题数',
    key: 'total_count',
    width: 90
  },
  {
    title: '已完成',
    key: 'completed_count',
    width: 90
  },
  {
    title: '错题数',
    key: 'wrong_count',
    width: 90
  },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    render(row) {
      return h(NSpace, { size: 'small' }, () => [
        h(
            NButton,
            {
              size: 'small',
              type: 'primary',
              onClick: () => handlePractice(row.id)
            },
            { default: () => '全部练习' }
        ),
        h(
            NButton,
            {
              size: 'small',
              type: 'warning',
              onClick: () => handleWrongSet(row.id)
            },
            { default: () => '错题集' }
        ),
        h(
            NButton,
            {
              size: 'small',
              type: 'error',
              ghost: true,
              onClick: () => confirmDelete(row.id)
            },
            { default: () => '删除' }
        )
      ])
    }
  }
]

/**
 * 获取题库数据
 */
async function fetchBanks() {
  loading.value = true
  try {
    const res = await axios.get<QuestionBank[]>('/api/question-banks')
    banks.value = res.data
  } catch (err) {
    console.error(err)
    message.error('获取题库失败')
  } finally {
    loading.value = false
  }
}

/**
 * 操作处理
 */
function handlePractice(id: number) {
  message.success(`进入题库 ${id} 的全部练习`)
  // TODO: 路由跳转 or 调用后端接口
}

function handleWrongSet(id: number) {
  message.success(`进入题库 ${id} 的错题集`)
  // TODO: 路由跳转 or 调用后端接口
}

function confirmDelete(id: number) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除题库 ${id} 吗？此操作不可恢复！`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: () => handleDelete(id)
  })
}

async function handleDelete(id: number) {
  try {
    await axios.delete(`/api/question-banks/${id}`)
    message.success('删除成功')
    fetchBanks()
  } catch (err) {
    console.error(err)
    message.error('删除失败')
  }
}

onMounted(fetchBanks)
</script>

<style scoped>
.bank-card {
  max-width: 1000px;
  margin: 40px auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}
</style>
