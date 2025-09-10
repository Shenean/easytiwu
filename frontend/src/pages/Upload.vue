<template>
  <n-card title="上传题库" class="upload-card" :segmented="{ content: true }" size="large">
    <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="80"
        size="large"
    >
      <!-- 题库名 -->
      <n-form-item label="题库名" path="name">
        <n-input
            v-model:value="form.name"
            placeholder="请输入题库名"
            maxlength="15"
            show-count
            clearable
            aria-label="题库名称"
        />
      </n-form-item>

      <!-- 描述 -->
      <n-form-item label="描述" path="description">
        <n-input
            v-model:value="form.description"
            placeholder="请输入描述（可选）"
            type="textarea"
            maxlength="30"
            show-count
            clearable
            autosize
            aria-label="题库描述"
        />
      </n-form-item>

      <!-- 文件上传 -->
      <n-form-item label="文件" path="file">
        <n-upload
            v-model:file-list="form.file"
            :max="1"
            :multiple="false"
            :default-upload="false"
            :on-before-upload="beforeUpload"
            :on-remove="handleFileRemove"
        >
          <n-upload-dragger v-if="form.file.length === 0">
            <div style="margin-bottom: 12px;">
              <n-icon size="32" color="#18a058">
                <i class="i-ion-cloud-upload-outline"></i>
              </n-icon>
            </div>
            <div>点击或拖拽文件到此处上传</div>
            <div style="font-size: 12px; color: #999;">
              支持 .doc, .docx, .pdf, .txt，大小不超过 20MB
            </div>
          </n-upload-dragger>

          <!-- 已上传文件展示 -->
          <div v-else class="uploaded-file">
            <n-tag
                type="success"
                size="small"
                closable
                @close="handleFileRemove"
                class="file-tag"
            >
              {{ form.file[0].name }}
              <span v-if="form.file[0].file">
                ({{ formatFileSize(form.file[0].file.size) }})
              </span>
            </n-tag>
          </div>
        </n-upload>
      </n-form-item>

      <!-- 提交操作区 -->
      <n-form-item>
        <div class="form-actions">
          <n-button @click="handleReset">
            重置
          </n-button>
          <n-button
              type="primary"
              :loading="submitting"
              :disabled="!isFormValid"
              @click="handleSubmit"
          >
            提交
          </n-button>
        </div>
      </n-form-item>
    </n-form>
  </n-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules, UploadFileInfo } from 'naive-ui'

interface UploadForm {
  name: string
  description: string
  file: UploadFileInfo[]
}

const formRef = ref<FormInst | null>(null)
const message = useMessage()
const submitting = ref(false)

const form = ref<UploadForm>({
  name: '',
  description: '',
  file: []
})

// 表单是否有效（用于按钮禁用）
const isFormValid = computed(() => {
  // 简单判断：三项都非空（file 至少一个）
  return !!form.value.name.trim() && form.value.file.length > 0
})

/**
 * 表单校验规则
 */
const rules: FormRules = {
  name: [
    { required: true, message: '题库名不能为空', trigger: 'blur' },
    { max: 15, message: '最多 15 字', trigger: 'input' }
  ],
  description: [
    { max: 30, message: '最多 30 字', trigger: 'input' }
  ],
  file: [
    { required: true, message: '请上传文件', trigger: 'change' }
  ]
}

/**
 * 格式化文件大小
 */
function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 上传前校验文件
 */
function beforeUpload(file: UploadFileInfo) {
  const allowTypes = [
    'application/msword', // .doc
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // .docx
    'application/pdf', // .pdf
    'text/plain' // .txt
  ]

  const fileType = file.type || ''
  const fileSize = file.file?.size ?? 0
  const isAllowed = allowTypes.includes(fileType)
  const isLt20M = fileSize / 1024 / 1024 < 20

  if (!isAllowed) {
    message.error('仅支持 doc, docx, pdf, txt 格式')
    return false
  }
  if (!isLt20M) {
    message.error('文件大小不能超过 20MB')
    return false
  }
  return true
}

/**
 * 删除文件
 */
function handleFileRemove() {
  form.value.file = []
}

/**
 * 重置表单
 */
function handleReset() {
  const hasData = form.value.name || form.value.description || form.value.file.length > 0
  if (!hasData) {
    message.info('表单已是初始状态')
    return
  }

  if (window.confirm('⚠️ 确定重置表单？所有数据将丢失')) {
    form.value = { name: '', description: '', file: [] }
    message.info('表单已重置')
  }
}

/**
 * 提交处理
 */
function handleSubmit() {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      // 显示第一条错误信息
      const firstError = Object.values(errors)
        .flat()
        .find(err => err.message)?.message || '请检查表单输入'
      message.error(firstError)
      return
    }

    submitting.value = true
    try {
      // TODO: 调用后端 API 提交 form 数据
      await new Promise(resolve => setTimeout(resolve, 1200)) // 模拟异步
      message.success('上传成功 🎉')
      // 成功后可自动重置或跳转
      // form.value = { name: '', description: '', file: [] }
    } catch (err) {
      message.error('上传失败，请重试')
    } finally {
      submitting.value = false
    }
  })
}

// 暴露方法供父组件调用
defineExpose({
  submit: handleSubmit,
  reset: handleReset,
  getFormData: () => ({ ...form.value })
})
</script>

<style scoped>
.upload-card {
  max-width: 600px;
  margin: 40px auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

/* 上传区域悬停效果 */
:deep(.n-upload-dragger) {
  transition: all 0.3s ease;
  border-radius: 8px;
}
:deep(.n-upload-dragger:hover) {
  border-color: #18a058 !important;
  background-color: #f6ffed;
}

.uploaded-file {
  padding: 16px 0;
  text-align: center;
}

.file-tag {
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}
</style>