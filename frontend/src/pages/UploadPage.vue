<template>
  <PageContainer title="上传题库" card-class="upload-card">
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80" size="large">
      <!-- 题库名 -->
      <n-form-item label="题库名" path="name">
        <n-input v-model:value="form.name" placeholder="请输入题库名" maxlength="15" show-count clearable aria-label="题库名称" />
      </n-form-item>

      <!-- 描述 -->
      <n-form-item label="描述" path="description">
        <n-input v-model:value="form.description" placeholder="请输入描述（可选）" type="textarea" maxlength="30" show-count
          clearable autosize aria-label="题库描述" />
      </n-form-item>

      <!-- 文件上传 -->
      <n-form-item label="文件" path="file">
        <n-upload v-model:file-list="form.file" :accept="'.docx,.pdf,.txt'" :max="1" :multiple="false" action="#"
          :custom-request="handleCustomRequest" @before-upload="handleBeforeUpload">
          <n-upload-dragger>
            <div style="margin-bottom: 12px">
              <n-icon size="48" :depth="3">
                <ArchiveIcon />
              </n-icon>
            </div>
            <n-text style="font-size: 16px">
              点击或者拖动文件到该区域来上传
            </n-text>
            <n-p depth="3" style="margin: 8px 0 0 0">
              支持 .docx、.pdf、.txt 格式，文件大小不超过 20MB
            </n-p>
          </n-upload-dragger>
        </n-upload>
      </n-form-item>

      <!-- 提交操作区 -->
      <n-form-item>
        <FormActions :loading="submitting" :disabled="!isFormValid" :show-reset="true" @submit="handleSubmit"
          @reset="handleReset" />
      </n-form-item>
    </n-form>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, ref} from 'vue'
import type {FormInst, UploadCustomRequestOptions, UploadFileInfo} from 'naive-ui'
import {useMessage} from 'naive-ui'
import {ArchiveOutline as ArchiveIcon} from '@vicons/ionicons5'
import {uploadAPI} from '../api/config'
import FormActions from '../components/common/FormActions.vue'
import PageContainer from '../components/common/PageContainer.vue'
import {bankFormRules} from '../validation/rulesBank'

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
  return !!form.value.name.trim() && form.value.file.length > 0
})

// 使用公共表单校验规则
const rules = bankFormRules



/**
 * 上传前校验文件
 */
function handleBeforeUpload(data: { file: UploadFileInfo; fileList: UploadFileInfo[] }) {
  const file = data.file
  const fileName = file.file?.name || file.name || '未知文件'

  // 文件类型校验
  const allowedTypes = ['.docx', '.pdf', '.txt']
  const fileExtension = fileName.toLowerCase().substring(fileName.lastIndexOf('.'))
  if (!allowedTypes.includes(fileExtension)) {
    message.error(`不支持的文件格式，请选择 ${allowedTypes.join('、')} 格式的文件`)
    return false
  }

  // 文件大小校验（20MB）
  const maxSize = 20 * 1024 * 1024
  if (file.file && file.file.size > maxSize) {
    message.error('文件大小不能超过 20MB')
    return false
  }

  console.log('文件上传前处理:', fileName)
  return true
}

/**
 * 自定义上传请求（阻止默认上传行为）
 */
function handleCustomRequest(options: UploadCustomRequestOptions) {
  // 阻止默认上传，文件将在表单提交时统一处理
  options.onFinish()
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
    // 重置表单数据，文件列表通过v-model自动同步到BaseUpload组件
    form.value.name = ''
    form.value.description = ''
    form.value.file = []
    message.info('表单已重置')
  }
}

/**
 * 提交处理
 */
function handleSubmit() {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      const firstError = Object.values(errors)
        .flat()
        .find(err => err.message)?.message || '请检查表单输入'
      message.error(firstError)
      return
    }

    submitting.value = true
    try {
      if (!form.value.file[0]?.file) {
        message.error('请选择有效的文件')
        return
      }

      const formData = new FormData()
      formData.append('name', form.value.name)
      formData.append('description', form.value.description)
      formData.append('file', form.value.file[0].file as Blob)

      await uploadAPI.uploadFile(formData)

      message.success('上传成功 🎉')
      // 上传成功后重置表单，文件列表通过v-model自动同步到BaseUpload组件
      form.value.name = ''
      form.value.description = ''
      form.value.file = []
    } catch (err: unknown) {
      console.error('Upload error:', err)
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response: { data?: string } }
        message.error(axiosErr.response?.data || '上传失败，请重试')
      } else {
        message.error('上传失败，请重试')
      }
    } finally {
      submitting.value = false
    }
  })
}

defineExpose({
  submit: handleSubmit,
  reset: handleReset,
  getFormData: () => ({ ...form.value })
})
</script>

<style scoped>
.upload-card {
  max-width: 640px;
  margin: 40px auto;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  transition: box-shadow 0.3s ease;
}

.upload-card:hover {
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
}

/* 移动端表单优化 */
@media (max-width: 768px) {
  .upload-card {
    margin: 16px;
    max-width: none;
  }

  /* 表单项移动端优化 */
  :deep(.n-form-item-label) {
    font-size: 14px;
    font-weight: 600;
  }

  :deep(.n-input) {
    font-size: 16px !important;
    min-height: 44px;
  }

  :deep(.n-input__input-el) {
    font-size: 16px !important;
  }

  :deep(.n-input__textarea-el) {
    font-size: 16px !important;
  }


}

@media (max-width: 480px) {
  .upload-card {
    margin: 12px;
    border-radius: 12px;
  }

  :deep(.n-form-item-label) {
    font-size: 13px;
  }

  :deep(.n-input) {
    min-height: 42px;
  }


}

/* 横屏模式优化 */
@media (max-width: 768px) and (orientation: landscape) {
  .upload-card {
    margin: 8px;
  }

  :deep(.n-input) {
    min-height: 38px;
  }


}
</style>
