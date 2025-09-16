# 组件使用示例

本文档提供了 EasyTiWu 设计系统中各种组件的使用示例和最佳实践。

## 页面布局组件

### PageContainer 页面容器

页面容器是所有页面的基础布局组件，提供统一的内边距和最大宽度限制。

```vue
<template>
  <PageContainer title="页面标题" subtitle="页面描述">
    <!-- 页面内容 -->
    <n-card title="内容卡片">
      <p>这里是页面的主要内容</p>
    </n-card>
  </PageContainer>
</template>

<script setup>
import PageContainer from '@/components/common/PageContainer.vue'
</script>
```

**Props 说明：**
- `title`: 页面标题
- `subtitle`: 页面副标题（可选）
- `containerSize`: 容器尺寸 (`'sm' | 'md' | 'lg' | 'xl' | 'full'`)
- `padding`: 内边距 (`'none' | 'sm' | 'md' | 'lg'`)

### AppLayout 应用布局

应用的主布局组件，包含导航栏、主内容区域和页脚。

```vue
<template>
  <AppLayout 
    :show-footer="true"
    container-size="lg"
    content-padding="md"
  >
    <!-- 主要内容 -->
    <router-view />
    
    <!-- 页脚内容 -->
    <template #footer>
      <div class="footer-content">
        <p>&copy; 2024 EasyTiWu. All rights reserved.</p>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import AppLayout from '@/components/layout/AppLayout.vue'
</script>
```

## 导航组件

### NavigationBar 导航栏

响应式导航栏，在桌面端显示水平菜单，移动端显示抽屉菜单。

```vue
<template>
  <NavigationBar 
    :menu-options="menuOptions"
    logo-src="/logo.png"
    logo-alt="EasyTiWu"
  />
</template>

<script setup>
import NavigationBar from '@/components/NavigationBar.vue'
import { menuOptions } from '@/config/menuOptions'
</script>
```

**特性：**
- 自动响应式布局
- 支持多级菜单
- 移动端抽屉式菜单
- 主题适配

## 表单组件

### 基础表单示例

```vue
<template>
  <div class="form-container">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="top"
      size="medium"
      class="mobile-form"
    >
      <!-- 文本输入 -->
      <n-form-item label="用户名" path="username">
        <n-input 
          v-model:value="formData.username"
          placeholder="请输入用户名"
          clearable
        />
      </n-form-item>
      
      <!-- 密码输入 -->
      <n-form-item label="密码" path="password">
        <n-input 
          v-model:value="formData.password"
          type="password"
          placeholder="请输入密码"
          show-password-on="mousedown"
        />
      </n-form-item>
      
      <!-- 选择器 -->
      <n-form-item label="角色" path="role">
        <n-select
          v-model:value="formData.role"
          placeholder="请选择角色"
          :options="roleOptions"
        />
      </n-form-item>
      
      <!-- 多行文本 -->
      <n-form-item label="备注" path="remark">
        <n-input
          v-model:value="formData.remark"
          type="textarea"
          placeholder="请输入备注信息"
          :rows="3"
        />
      </n-form-item>
      
      <!-- 操作按钮 -->
      <n-form-item>
        <div class="form-actions">
          <n-button @click="handleReset">{{ $t('common.reset') }}</n-button>
          <n-button 
            type="primary" 
            @click="handleSubmit"
            :loading="loading"
          >
            {{ $t('common.submit') }}
          </n-button>
        </div>
      </n-form-item>
    </n-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useMessage } from 'naive-ui'

const message = useMessage()
const formRef = ref()
const loading = ref(false)

const formData = reactive({
  username: '',
  password: '',
  role: null,
  remark: ''
})

const roleOptions = [
  { label: '管理员', value: 'admin' },
  { label: '教师', value: 'teacher' },
  { label: '学生', value: 'student' }
]

const rules = {
  username: {
    required: true,
    message: '请输入用户名',
    trigger: 'blur'
  },
  password: {
    required: true,
    message: '请输入密码',
    trigger: 'blur'
  },
  role: {
    required: true,
    message: '请选择角色',
    trigger: 'change'
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true
    
    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    message.success('提交成功')
  } catch (error) {
    message.error('请检查表单输入')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  Object.assign(formData, {
    username: '',
    password: '',
    role: null,
    remark: ''
  })
}
</script>

<style scoped>
.form-container {
  max-width: var(--max-width-md);
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.form-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
}

@media (max-width: 767px) {
  .form-container {
    padding: var(--spacing-md);
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .n-button {
    width: 100%;
  }
}
</style>
```

## 数据展示组件

### 卡片列表

```vue
<template>
  <div class="card-list">
    <n-grid 
      :cols="gridCols" 
      :x-gap="16" 
      :y-gap="16"
      responsive="screen"
    >
      <n-grid-item 
        v-for="item in dataList" 
        :key="item.id"
      >
        <n-card 
          :title="item.title"
          hoverable
          class="data-card"
          @click="handleCardClick(item)"
        >
          <template #header-extra>
            <n-tag 
              :type="getStatusType(item.status)"
              size="small"
            >
              {{ item.statusText }}
            </n-tag>
          </template>
          
          <div class="card-content">
            <p class="description">{{ item.description }}</p>
            
            <div class="meta-info">
              <span class="meta-item">
                <n-icon :component="TimeIcon" />
                {{ formatDate(item.createdAt) }}
              </span>
              <span class="meta-item">
                <n-icon :component="UserIcon" />
                {{ item.author }}
              </span>
            </div>
          </div>
          
          <template #action>
            <n-space>
              <n-button size="small" @click.stop="handleEdit(item)">
                编辑
              </n-button>
              <n-button 
                size="small" 
                type="error" 
                @click.stop="handleDelete(item)"
              >
                删除
              </n-button>
            </n-space>
          </template>
        </n-card>
      </n-grid-item>
    </n-grid>
    
    <!-- 空状态 -->
    <n-empty 
      v-if="dataList.length === 0"
      description="暂无数据"
      class="empty-state"
    >
      <template #extra>
        <n-button type="primary" @click="handleCreate">
          创建新项目
        </n-button>
      </template>
    </n-empty>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useBreakpoints } from '@/composables/useBreakpoints'
import { TimeIcon, UserIcon } from '@/components/icons'

const { isMobile, isTablet } = useBreakpoints()

const dataList = ref([
  {
    id: 1,
    title: '项目标题 1',
    description: '这是项目的详细描述信息...',
    status: 'active',
    statusText: '进行中',
    author: '张三',
    createdAt: new Date('2024-01-15')
  },
  // 更多数据...
])

// 响应式网格列数
const gridCols = computed(() => {
  if (isMobile.value) return 1
  if (isTablet.value) return 2
  return 3
})

const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    pending: 'warning',
    completed: 'info',
    cancelled: 'error'
  }
  return typeMap[status] || 'default'
}

const formatDate = (date) => {
  return new Intl.DateTimeFormat('zh-CN').format(date)
}

const handleCardClick = (item) => {
  console.log('Card clicked:', item)
}

const handleEdit = (item) => {
  console.log('Edit item:', item)
}

const handleDelete = (item) => {
  console.log('Delete item:', item)
}

const handleCreate = () => {
  console.log('Create new item')
}
</script>

<style scoped>
.card-list {
  padding: var(--spacing-lg);
}

.data-card {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-card-medium);
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.description {
  color: var(--color-text-secondary);
  line-height: var(--line-height-relaxed);
  margin: 0;
}

.meta-info {
  display: flex;
  gap: var(--spacing-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.empty-state {
  margin: var(--spacing-2xl) 0;
}

@media (max-width: 767px) {
  .card-list {
    padding: var(--spacing-md);
  }
  
  .meta-info {
    flex-direction: column;
    gap: var(--spacing-xs);
  }
}
</style>
```

### 数据表格

```vue
<template>
  <div class="table-container">
    <div class="table-header">
      <h3>数据列表</h3>
      <n-space>
        <n-input 
          v-model:value="searchText"
          placeholder="搜索..."
          clearable
        >
          <template #prefix>
            <n-icon :component="SearchIcon" />
          </template>
        </n-input>
        <n-button type="primary" @click="handleCreate">
          <template #icon>
            <n-icon :component="PlusIcon" />
          </template>
          新建
        </n-button>
      </n-space>
    </div>
    
    <n-data-table
      :columns="columns"
      :data="filteredData"
      :loading="loading"
      :pagination="pagination"
      :row-key="row => row.id"
      size="medium"
      striped
    />
  </div>
</template>

<script setup>
import { ref, computed, h } from 'vue'
import { NButton, NTag, NSpace, useMessage } from 'naive-ui'
import { SearchIcon, PlusIcon, EditIcon, DeleteIcon } from '@/components/icons'

const message = useMessage()
const loading = ref(false)
const searchText = ref('')

const tableData = ref([
  {
    id: 1,
    name: '张三',
    email: 'zhangsan@example.com',
    role: 'admin',
    status: 'active',
    createdAt: new Date('2024-01-15')
  },
  // 更多数据...
])

const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '姓名',
    key: 'name',
    sorter: 'default'
  },
  {
    title: '邮箱',
    key: 'email'
  },
  {
    title: '角色',
    key: 'role',
    render: (row) => {
      const roleMap = {
        admin: { text: '管理员', type: 'error' },
        teacher: { text: '教师', type: 'warning' },
        student: { text: '学生', type: 'info' }
      }
      const role = roleMap[row.role] || { text: '未知', type: 'default' }
      return h(NTag, { type: role.type, size: 'small' }, { default: () => role.text })
    }
  },
  {
    title: '状态',
    key: 'status',
    render: (row) => {
      const statusMap = {
        active: { text: '活跃', type: 'success' },
        inactive: { text: '非活跃', type: 'warning' },
        banned: { text: '已禁用', type: 'error' }
      }
      const status = statusMap[row.status] || { text: '未知', type: 'default' }
      return h(NTag, { type: status.type, size: 'small' }, { default: () => status.text })
    }
  },
  {
    title: '创建时间',
    key: 'createdAt',
    render: (row) => new Intl.DateTimeFormat('zh-CN').format(row.createdAt),
    sorter: (a, b) => a.createdAt.getTime() - b.createdAt.getTime()
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render: (row) => {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, {
            size: 'small',
            onClick: () => handleEdit(row)
          }, {
            default: () => '编辑',
            icon: () => h(EditIcon)
          }),
          h(NButton, {
            size: 'small',
            type: 'error',
            onClick: () => handleDelete(row)
          }, {
            default: () => '删除',
            icon: () => h(DeleteIcon)
          })
        ]
      })
    }
  }
]

const pagination = {
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  showQuickJumper: true,
  prefix: ({ itemCount }) => `共 ${itemCount} 条`
}

const filteredData = computed(() => {
  if (!searchText.value) return tableData.value
  
  return tableData.value.filter(item => 
    item.name.toLowerCase().includes(searchText.value.toLowerCase()) ||
    item.email.toLowerCase().includes(searchText.value.toLowerCase())
  )
})

const handleCreate = () => {
  console.log('Create new record')
}

const handleEdit = (row) => {
  console.log('Edit record:', row)
}

const handleDelete = (row) => {
  console.log('Delete record:', row)
  message.warning('确认删除此记录？')
}
</script>

<style scoped>
.table-container {
  padding: var(--spacing-lg);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.table-header h3 {
  margin: 0;
  color: var(--color-text-primary);
}

@media (max-width: 767px) {
  .table-container {
    padding: var(--spacing-md);
  }
  
  .table-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: stretch;
  }
}
</style>
```

## 反馈组件

### 加载状态

```vue
<template>
  <div class="loading-examples">
    <!-- 页面级加载 -->
    <n-spin :show="pageLoading" size="large">
      <div class="content-area">
        <h2>页面内容</h2>
        <p>这里是页面的主要内容...</p>
      </div>
    </n-spin>
    
    <!-- 按钮加载 -->
    <n-space>
      <n-button 
        type="primary" 
        :loading="buttonLoading"
        @click="handleSubmit"
      >
        提交数据
      </n-button>
      
      <n-button 
        :loading="refreshLoading"
        @click="handleRefresh"
      >
        <template #icon>
          <n-icon :component="RefreshIcon" />
        </template>
        刷新
      </n-button>
    </n-space>
    
    <!-- 卡片加载 -->
    <n-card title="数据卡片">
      <n-skeleton v-if="cardLoading" text :repeat="3" />
      <div v-else>
        <p>卡片内容已加载完成</p>
      </div>
    </n-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { RefreshIcon } from '@/components/icons'

const pageLoading = ref(false)
const buttonLoading = ref(false)
const refreshLoading = ref(false)
const cardLoading = ref(true)

const handleSubmit = async () => {
  buttonLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    console.log('提交成功')
  } finally {
    buttonLoading.value = false
  }
}

const handleRefresh = async () => {
  refreshLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    console.log('刷新完成')
  } finally {
    refreshLoading.value = false
  }
}

// 模拟数据加载
setTimeout(() => {
  cardLoading.value = false
}, 3000)
</script>

<style scoped>
.loading-examples {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.content-area {
  min-height: 200px;
  padding: var(--spacing-lg);
  background: var(--color-surface);
  border-radius: var(--border-radius-lg);
}
</style>
```

### 消息提示

```vue
<template>
  <div class="message-examples">
    <n-space>
      <n-button @click="showSuccess">成功消息</n-button>
      <n-button @click="showWarning">警告消息</n-button>
      <n-button @click="showError">错误消息</n-button>
      <n-button @click="showInfo">信息消息</n-button>
      <n-button @click="showLoading">加载消息</n-button>
    </n-space>
    
    <n-space>
      <n-button @click="showNotification">通知</n-button>
      <n-button @click="showDialog">对话框</n-button>
      <n-button @click="showDrawer">抽屉</n-button>
    </n-space>
  </div>
</template>

<script setup>
import { useMessage, useNotification, useDialog } from 'naive-ui'

const message = useMessage()
const notification = useNotification()
const dialog = useDialog()

const showSuccess = () => {
  message.success('操作成功！')
}

const showWarning = () => {
  message.warning('请注意检查输入内容')
}

const showError = () => {
  message.error('操作失败，请重试')
}

const showInfo = () => {
  message.info('这是一条信息提示')
}

const showLoading = () => {
  const loading = message.loading('正在处理中...', {
    duration: 3000
  })
  
  setTimeout(() => {
    loading.destroy()
    message.success('处理完成')
  }, 2000)
}

const showNotification = () => {
  notification.success({
    title: '系统通知',
    content: '您有新的消息，请及时查看',
    duration: 5000
  })
}

const showDialog = () => {
  dialog.warning({
    title: '确认操作',
    content: '此操作不可撤销，是否继续？',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: () => {
      message.success('操作已确认')
    },
    onNegativeClick: () => {
      message.info('操作已取消')
    }
  })
}

const showDrawer = () => {
  // 抽屉组件需要在模板中定义
  console.log('显示抽屉')
}
</script>

<style scoped>
.message-examples {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}
</style>
```

## 移动端适配示例

### 响应式布局

```vue
<template>
  <div class="responsive-layout">
    <!-- 桌面端：侧边栏 + 主内容 -->
    <div v-if="!isMobile" class="desktop-layout">
      <aside class="sidebar">
        <nav class="sidebar-nav">
          <!-- 侧边栏导航 -->
        </nav>
      </aside>
      <main class="main-content">
        <!-- 主要内容 -->
      </main>
    </div>
    
    <!-- 移动端：全屏布局 -->
    <div v-else class="mobile-layout">
      <main class="main-content mobile-optimized">
        <!-- 移动端内容 -->
      </main>
      
      <!-- 底部导航 -->
      <nav class="mobile-nav">
        <a 
          v-for="item in navItems" 
          :key="item.key"
          :class="['nav-item', { active: currentRoute === item.key }]"
          @click="handleNavClick(item)"
        >
          <div class="icon">
            <n-icon :component="item.icon" />
          </div>
          <span class="label">{{ item.label }}</span>
        </a>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useBreakpoints } from '@/composables/useBreakpoints'
import { HomeIcon, UserIcon, SettingsIcon } from '@/components/icons'

const { isMobile } = useBreakpoints()
const currentRoute = ref('home')

const navItems = [
  { key: 'home', label: '首页', icon: HomeIcon },
  { key: 'profile', label: '个人', icon: UserIcon },
  { key: 'settings', label: '设置', icon: SettingsIcon }
]

const handleNavClick = (item) => {
  currentRoute.value = item.key
  console.log('Navigate to:', item.key)
}
</script>

<style scoped>
.responsive-layout {
  height: 100vh;
}

.desktop-layout {
  display: flex;
  height: 100%;
}

.sidebar {
  width: var(--sidebar-width);
  background: var(--color-surface);
  border-right: var(--border-width-1) solid var(--color-border);
}

.mobile-layout {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.main-content {
  flex: 1;
  overflow-y: auto;
}

/* 移动端导航样式在 mobile-optimizations.css 中定义 */
</style>
```

## 最佳实践总结

### 1. 组件组合

优先使用组件组合而不是继承：

```vue
<!-- 好的做法 -->
<PageContainer>
  <DataTable :data="tableData" />
</PageContainer>

<!-- 避免的做法 -->
<CustomPageWithTable :data="tableData" />
```

### 2. 响应式设计

使用 `useBreakpoints` 组合式函数：

```javascript
const { isMobile, isTablet, isDesktop } = useBreakpoints()

const gridCols = computed(() => {
  if (isMobile.value) return 1
  if (isTablet.value) return 2
  return 3
})
```

### 3. 主题适配

使用 CSS 变量确保主题兼容：

```css
.custom-component {
  background: var(--color-surface);
  color: var(--color-text-primary);
  border: var(--border-width-1) solid var(--color-border);
}
```

### 4. 可访问性

确保组件支持键盘导航和屏幕阅读器：

```vue
<button 
  :aria-label="buttonLabel"
  :aria-pressed="isPressed"
  @click="handleClick"
  @keydown.enter="handleClick"
>
  {{ buttonText }}
</button>
```

---

更多示例和最佳实践，请参考 [设计系统文档](./design-system.md)。