<template>
  <n-upload
    v-model:file-list="fileList"
    :max="max"
    :multiple="multiple"
    :default-upload="false"
    :on-before-upload="handleBeforeUpload"
    :on-remove="handleFileRemove"
  >
    <!-- 拖拽上传区域 -->
    <n-upload-dragger v-if="fileList.length === 0">
      <slot name="dragger">
        <div style="margin-bottom: 12px;">
          <n-icon size="32" color="#18a058">
            <i class="i-ion-cloud-upload-outline"></i>
          </n-icon>
        </div>
        <div>点击或拖拽文件到此处上传</div>
        <div style="font-size: 12px; color: #999;">
          {{ uploadTip }}
        </div>
      </slot>
    </n-upload-dragger>

    <!-- 已上传文件展示 -->
    <div v-else class="uploaded-file">
      <n-tag
        v-for="file in fileList"
        :key="file.id || file.name"
        type="success"
        size="small"
        closable
        @close="() => handleFileRemove(file)"
        class="file-tag"
      >
        {{ file.name }}
        <span v-if="file.file">
          ({{ formatFileSize(file.file.size) }})
        </span>
      </n-tag>
    </div>
  </n-upload>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useVModel } from '@vueuse/core'
import { useMessage } from 'naive-ui'
import type { UploadFileInfo } from 'naive-ui'

interface Props {
  /** 文件类型限制，如 ['.doc', '.docx', '.pdf', '.txt'] */
  accept?: string[]
  /** 最大文件尺寸（MB） */
  maxSize?: number
  /** 是否支持多选 */
  multiple?: boolean
  /** 最大上传数量 */
  max?: number
  /** 文件列表 */
  modelValue?: UploadFileInfo[]
  /** 自定义上传提示文本 */
  tip?: string
  /** MIME 类型映射 */
  mimeTypes?: Record<string, string[]>
}

const props = withDefaults(defineProps<Props>(), {
  accept: () => ['.doc', '.docx', '.pdf', '.txt'],
  maxSize: 20,
  multiple: false,
  max: 1,
  modelValue: () => [],
  tip: '',
  mimeTypes: () => ({
    '.doc': ['application/msword'],
    '.docx': ['application/vnd.openxmlformats-officedocument.wordprocessingml.document'],
    '.pdf': ['application/pdf'],
    '.txt': ['text/plain']
  })
})

const emit = defineEmits<{
  /** 文件列表更新事件 */
  'update:modelValue': [fileList: UploadFileInfo[]]
  /** 上传前校验事件 */
  'beforeUpload': [file: UploadFileInfo]
  /** 文件移除事件 */
  'remove': [file: UploadFileInfo]
}>()

const message = useMessage()

const fileList = useVModel(props, 'modelValue', emit)



// 生成上传提示文本
const uploadTip = computed(() => {
  if (props.tip) return props.tip
  const acceptText = props.accept.join(', ')
  const sizeText = `大小不超过 ${props.maxSize}MB`
  return `支持 ${acceptText}，${sizeText}`
})

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
function handleBeforeUpload(file: UploadFileInfo): boolean {
  const fileName = file.file?.name || file.name || ''
  if (!fileName) {
    message.error('无法获取文件名')
    return false
  }

  // 获取文件扩展名
  const fileExt = fileName.includes('.') 
    ? '.' + fileName.split('.').pop()?.toLowerCase() 
    : ''

  // 检查文件类型
  const isExtAllowed = props.accept.includes(fileExt)
  if (!isExtAllowed) {
    message.error(`仅支持 ${props.accept.join(', ')} 格式`)
    return false
  }

  // 检查文件大小
  const fileSize = file.file?.size ?? 0
  if (fileSize / 1024 / 1024 >= props.maxSize) {
    message.error(`文件大小不能超过 ${props.maxSize}MB`)
    return false
  }

  // 检查MIME类型（警告但不阻止）
  const fileType = file.file?.type || ''
  const allowedMimeTypes = props.mimeTypes[fileExt] || []
  if (!allowedMimeTypes.includes(fileType) && allowedMimeTypes.length > 0) {
    console.warn(`[MIME mismatch] 文件 ${fileName} 的 MIME 类型为 ${fileType}，但扩展名合法。`)
  }

  emit('beforeUpload', file)
  return true
}

/**
 * 删除文件
 */
function handleFileRemove(file?: UploadFileInfo) {
  if (file) {
    const index = fileList.value.findIndex(f => f === file)
    if (index > -1) {
      fileList.value.splice(index, 1)
      emit('remove', file)
    }
  } else {
    const removedFiles = [...fileList.value]
    fileList.value = []
    removedFiles.forEach(f => emit('remove', f))
  }
}

// 暴露方法供父组件调用
defineExpose({
  clear: () => handleFileRemove(),
  getFileList: () => [...fileList.value],
  formatFileSize
})
</script>

<style scoped>
:deep(.n-upload-dragger) {
  transition: all 0.3s ease;
  border: 2px dashed #d9d9d9;
  padding: 24px;
  border-radius: 10px;
  background-color: #fafafa;
  cursor: pointer;
}

:deep(.n-upload-dragger:hover) {
  border-color: #18a058 !important;
  background-color: #f0fdf4;
  transform: scale(1.02);
}

.uploaded-file {
  padding: 16px 0;
  text-align: center;
  word-break: break-all;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.file-tag {
  font-size: 14px;
  max-width: 90%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1.2;
  white-space: normal;
  word-wrap: break-word;
}


</style>
