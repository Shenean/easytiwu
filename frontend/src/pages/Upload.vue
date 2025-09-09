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
        />
      </n-form-item>

      <!-- 文件 -->
      <n-form-item label="文件" path="file">
        <n-upload
            v-model:file-list="form.file"
            :max="1"
            :multiple="false"
            :default-upload="false"
            :on-before-upload="beforeUpload"
            directory-dnd
        >
          <n-upload-dragger>
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
        </n-upload>
      </n-form-item>

      <!-- 提交 -->
      <n-form-item>
        <div class="form-actions">
          <n-button @click="handleReset">重置</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">
            提交
          </n-button>
        </div>
      </n-form-item>
    </n-form>
  </n-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
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
 * 上传前校验文件
 */
function beforeUpload(file: UploadFileInfo) {
  const allowTypes = [
    'application/msword', // .doc
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // .docx
    'application/pdf', // .pdf
    'text/plain' // .txt
  ]
  const isAllowed = allowTypes.includes(file.type || '')
  const isLt20M = file.file && file.file.size! / 1024 / 1024 < 20

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
 * 重置表单
 */
function handleReset() {
  form.value = { name: '', description: '', file: [] }
  message.info('表单已重置')
}

/**
 * 提交处理
 */
function handleSubmit() {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      submitting.value = true
      try {
        // TODO: 调用后端 API 提交 form 数据
        await new Promise(resolve => setTimeout(resolve, 1200)) // 模拟异步
        message.success('上传成功 🎉')
      } catch (err) {
        message.error('上传失败')
      } finally {
        submitting.value = false
      }
    } else {
      message.error('请检查表单输入')
    }
  })
}
</script>

<style scoped>
.upload-card {
  max-width: 600px;
  margin: 40px auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}
</style>
