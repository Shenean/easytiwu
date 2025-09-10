<template>
  <n-card title="题库列表" class="bank-card" :segmented="{ content: true }" size="large">
    <!-- 空状态占位 -->
    <div v-if="!loading && banks.length === 0" class="empty-state">
      <n-empty description="暂无题库数据">
        <template #extra>
          <n-button type="primary" size="small" @click="fetchBanks">
            刷新数据
          </n-button>
        </template>
      </n-empty>
    </div>

    <!-- 数据表格 -->
    <n-data-table
        v-else
        :columns="columns"
        :data="banks"
        :loading="loading"
        :bordered="false"
    />
  </n-card>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import type { DataTableColumns } from 'naive-ui'
import { useMessage, NButton, NSpace, useDialog, NEllipsis } from 'naive-ui'
import { useRouter } from 'vue-router'
import axios, { AxiosError } from 'axios'

// 定义统一响应格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

interface QuestionBank {
  id: number
  name: string
  description: string
  total_count: number
  completed_count: number
  wrong_count: number
}

// ================== 状态管理 ==================
const message = useMessage()
const dialog = useDialog()
const router = useRouter()
const banks = ref<QuestionBank[]>([])
const loading = ref(false)



// ================== 表格列定义 ==================
const columns: DataTableColumns<QuestionBank> = [
  {
    title: 'ID',
    key: 'id',
    width: 70,
    align: 'center'
  },
  {
    title: '题库名',
    key: 'name',
    render(row) {
      return h(NEllipsis, { tooltip: true }, () => row.name)
    }
  },
  {
    title: '描述',
    key: 'description',
    render(row) {
      return h(NEllipsis, { tooltip: true }, () => row.description || '—')
    }
  },
  {
    title: '总题数',
    key: 'total_count',
    width: 90,
    align: 'center'
  },
  {
    title: '已完成',
    key: 'completed_count',
    width: 90,
    align: 'center'
  },
  {
    title: '错题数',
    key: 'wrong_count',
    width: 90,
    align: 'center'
  },
  {
    title: '操作',
    key: 'actions',
    width: 260,
    align: 'center',
    fixed: 'right', 
    render(row) {
      return h(NSpace, { size: 'small' }, () => [
        h(
            NButton,
            {
              size: 'small',
              type: 'primary',
              onClick: () => handlePractice(row.id),
              'aria-label': `练习题库 ${row.name}`
            },
            { default: () => '全部练习' }
        ),
        h(
            NButton,
            {
              size: 'small',
              type: 'warning',
              onClick: () => handleWrongSet(row.id),
              'aria-label': `查看错题集 ${row.name}`
            },
            { default: () => '错题集' }
        ),
        h(
            NButton,
            {
              size: 'small',
              type: 'error',
              ghost: true,
              onClick: () => confirmDelete(row.id),
              'aria-label': `删除题库 ${row.name}`
            },
            { default: () => '删除' }
        )
      ])
    }
  }
]

// ================== 数据获取 ==================
/**
 * 获取题库数据
 */
async function fetchBanks() {
  loading.value = true
  try {
    const res = await axios.get<ApiResponse<QuestionBank[]>>('/api/bank')

    // 检查响应格式并正确处理数据
    if (res.data.code === 200) {
      banks.value = res.data.data || []
    } else {
      message.error(res.data.message || '获取题库失败')
    }
  } catch (err) {
    console.error('[API Error] 获取题库失败:', err)
    if (err instanceof AxiosError) {
      message.error(`请求失败：${err.response?.data?.message || '未知错误'}`)
    } else {
      message.error('获取题库失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// ================== 操作处理 ==================
function handlePractice(id: number) {
  try {
    router.push({ name: 'content', params: { bankId: id.toString(), type: 'all' } })
    message.success(`进入题库 ID: ${id} 的全部练习`)
  } catch (error) {
    console.error('路由跳转失败:', error)
    message.error('页面跳转失败，请稍后重试')
  }
}

function handleWrongSet(id: number) {
  try {
    router.push({ name: 'content', params: { bankId: id.toString(), type: 'wrong' } })
    message.success(`进入题库 ID: ${id} 的错题集`)
  } catch (error) {
    console.error('路由跳转失败:', error)
    message.error('页面跳转失败，请稍后重试')
  }
}

function confirmDelete(id: number) {
  const targetBank = banks.value.find(b => b.id === id)
  const bankName = targetBank?.name || `ID: ${id}`

  dialog.warning({
    title: '⚠️ 确认删除',
    content: `确定要删除题库 “${bankName}” 吗？此操作不可恢复！`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await handleDelete(id)
    },
    onNegativeClick: () => {
      message.info('已取消删除')
    }
  })
}

async function handleDelete(id: number) {
  try {
    const res = await axios.delete<ApiResponse<void>>(`/api/bank/${id}`)
    
    // 检查响应格式
    if (res.data.code === 200) {
      message.success('删除成功 ✅')
      // 优化：前端移除，避免重新拉取全部数据
      banks.value = banks.value.filter(b => b.id !== id)
    } else {
      message.error(res.data.message || '删除失败')
    }
  } catch (err) {
    console.error('[API Error] 删除失败:', err)
    if (err instanceof AxiosError) {
      message.error(`删除失败：${err.response?.data?.message || '未知错误'}`)
    } else {
      message.error('删除失败，请稍后重试')
    }
  }
}

// ================== 生命周期 ==================
onMounted(() => {
  fetchBanks()
})

// ================== 暴露方法（供父组件调用） ==================
defineExpose({
  refresh: fetchBanks,
  getBanks: () => [...banks.value]
})
</script>

<style scoped>
.bank-card {
  max-width: 1080px;
  margin: 48px auto;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  transition: box-shadow 0.3s ease;
}

.bank-card:hover {
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.08);
}

.empty-state {
  padding: 48px 0;
  text-align: center;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表格优化：鼠标悬停高亮当前行 */
:deep(.n-data-table-tr:hover) {
  background-color: #f7fefb !important;
  transition: background-color 0.25s ease;
}

/* 操作列按钮组统一 spacing、视觉平衡 */
:deep(.n-space) {
  justify-content: center;
  align-items: center;
}

:deep(.n-button) {
  transition: all 0.2s ease;
}

:deep(.n-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

:deep(.n-button:active) {
  transform: translateY(0);
  box-shadow: none;
}

/* 空状态提示按钮动画 */
:deep(.n-empty .n-button) {
  animation: fadeIn 0.3s ease 0.2s backwards;
}

/* 小屏适配优化 */
@media (max-width: 768px) {
  .bank-card {
    margin: 24px 12px;
    padding: 12px;
  }

  .empty-state {
    padding: 32px 0;
  }

  :deep(.n-space) {
    flex-direction: column;
    gap: 8px;
  }
}

</style>