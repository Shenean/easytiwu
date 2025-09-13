<template>
  <div class="upload-container">
    <!-- 点击上传按钮 -->
    <n-upload v-model:file-list="fileList" :max="max" :multiple="multiple" :default-upload="false"
      :on-before-upload="handleBeforeUpload" :on-remove="handleFileRemove" :show-file-list="false">
      <n-button v-if="fileList.length === 0" type="primary" size="large" class="upload-button">
        <template #icon>
          <n-icon>
            <i class="i-ion-cloud-upload-outline"></i>
          </n-icon>
        </template>
        点击选择文件
      </n-button>
    </n-upload>

    <!-- 上传提示信息 -->
    <div v-if="fileList.length === 0" class="upload-tip">
      {{ uploadTip }}
    </div>

    <!-- 已上传文件展示 -->
    <div v-if="fileList.length > 0" class="uploaded-file">
      <n-tag v-for="file in fileList" :key="file.id || file.name" type="success" size="medium" closable
        @close="() => handleFileRemove(file)" class="file-tag">
        <template #icon>
          <n-icon>
            <i class="i-ion-document-outline"></i>
          </n-icon>
        </template>
        {{ file.name }}
        <span v-if="file.file" class="file-size">
          ({{ formatFileSize(file.file.size) }})
        </span>
      </n-tag>

      <!-- 重新选择文件按钮 -->
      <n-upload v-model:file-list="fileList" :max="max" :multiple="multiple" :default-upload="false"
        :on-before-upload="handleBeforeUpload" :on-remove="handleFileRemove" :show-file-list="false">
        <n-button size="small" type="default" class="reselect-button">
          <template #icon>
            <n-icon>
              <i class="i-ion-refresh-outline"></i>
            </n-icon>
          </template>
          重新选择
        </n-button>
      </n-upload>
    </div>
  </div>
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
.upload-container {
  text-align: center;
  padding: 20px;
  background: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.upload-container:hover {
  border-color: #18a058;
  background: #f6fffe;
}

.upload-button {
  padding: 12px 20px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.upload-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 160, 88, 0.15);
}

.upload-tip {
  margin-top: 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  padding: 0 16px;
}

.uploaded-file {
  padding: 20px 16px;
  text-align: center;
  word-break: break-all;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.file-tag {
  font-size: 14px;
  max-width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1.4;
  white-space: normal;
  word-wrap: break-word;
  padding: 8px 12px;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.file-tag:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.file-size {
  margin-left: 6px;
  font-size: 12px;
  opacity: 0.7;
  font-weight: normal;
}

.reselect-button {
  margin-top: 12px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 4px;
  padding: 6px 12px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.reselect-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

/* 移动端优化 */
@media (max-width: 768px) {
  .upload-container {
    padding: 16px;
    margin: 0 4px;
  }

  .upload-button {
    padding: 12px 16px;
    font-size: 15px;
    min-height: 44px;
    width: 100%;
    max-width: 280px;
  }

  .upload-tip {
    font-size: 13px;
    margin-top: 12px;
    padding: 0 8px;
  }

  .uploaded-file {
    padding: 16px 12px;
    gap: 12px;
  }

  .file-tag {
    font-size: 13px;
    padding: 8px 10px;
    max-width: 95%;
  }

  .file-size {
    font-size: 11px;
    margin-left: 4px;
  }

  .reselect-button {
    font-size: 12px;
    padding: 8px 12px;
    margin-top: 8px;
  }
}

@media (max-width: 480px) {
  .upload-container {
    padding: 12px;
    border-radius: 6px;
  }

  .upload-button {
    padding: 10px 14px;
    font-size: 14px;
    min-height: 42px;
  }

  .upload-tip {
    font-size: 12px;
    margin-top: 10px;
  }

  .uploaded-file {
    padding: 12px 8px;
    border-radius: 6px;
  }

  .file-tag {
    font-size: 12px;
    padding: 6px 8px;
  }
}

/* 横屏模式优化 */
@media (max-width: 768px) and (orientation: landscape) {
  .upload-container {
    padding: 12px;
  }

  .upload-button {
    min-height: 38px;
    padding: 8px 16px;
  }

  .uploaded-file {
    padding: 12px;
  }
}
</style>
