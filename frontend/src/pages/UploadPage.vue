<template>
  <PageContainer :title="t('upload.title')" :show-card="true" card-class="upload-card">
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80" size="large">
      <n-form-item :label="t('upload.bankName')" path="name">
        <n-input v-model:value="form.name" :placeholder="t('upload.bankNamePlaceholder')" maxlength="15" show-count
          clearable :aria-label="t('upload.bankName')" />
      </n-form-item>

      <n-form-item :label="t('upload.description')" path="description">
        <n-input v-model:value="form.description" :placeholder="t('upload.descriptionPlaceholder')" type="textarea"
          maxlength="30" show-count clearable autosize :aria-label="t('upload.description')" />
      </n-form-item>

      <n-form-item :label="t('upload.file')" path="file">
        <n-upload v-model:file-list="form.file" :accept="'.docx,.pdf,.txt'" :max="1" :multiple="false" action="#"
          :custom-request="handleCustomRequest" @before-upload="handleBeforeUpload">
          <n-upload-dragger>
            <div style="margin-bottom: var(--spacing-3)">
              <n-icon size="48" :depth="3">
                <ArchiveIcon />
              </n-icon>
            </div>
            <n-text style="font-size: var(--font-size-base)">
              {{ t("upload.uploadText") }}
            </n-text>
            <n-p depth="3" style="margin: var(--spacing-2) 0 0 0">
              {{ t("upload.uploadHint") }}
            </n-p>
          </n-upload-dragger>
        </n-upload>
      </n-form-item>

      <n-form-item>
        <div style="display: flex; justify-content: flex-end; width: 100%;">
          <n-space size="medium" :vertical="false" align="center">
            <n-button size="large" secondary @click="handleReset">
              {{ t('common.reset') }}
            </n-button>
            <n-button type="primary" size="large" :loading="submitting" :disabled="!isFormValid" @click="handleSubmit">
              {{ t('common.submit') }}
            </n-button>
          </n-space>
        </div>
      </n-form-item>
    </n-form>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, ref} from "vue";
import type {FormInst, UploadCustomRequestOptions, UploadFileInfo,} from "naive-ui";
import {useMessage} from "naive-ui";
import {useI18n} from "vue-i18n";
import {ArchiveOutline as ArchiveIcon} from "@vicons/ionicons5";
import {uploadAPI} from "../api/config";


import PageContainer from "../components/common/PageContainer.vue";
import {bankFormRules} from "../validation/rulesBank";

interface UploadForm {
  name: string;
  description: string;
  file: UploadFileInfo[];
}

const formRef = ref<FormInst | null>(null);
const message = useMessage();
const { t } = useI18n();
const submitting = ref(false);


const form = ref<UploadForm>({
  name: "",
  description: "",
  file: [],
});

// 表单是否有效（用于按钮禁用）
const isFormValid = computed(() => {
  return !!form.value.name.trim() && form.value.file.length > 0;
});

// 使用公共表单校验规则
const rules = bankFormRules;

/**
 * 上传前校验文件
 */
function handleBeforeUpload(data: {
  file: UploadFileInfo;
  fileList: UploadFileInfo[];
}) {
  const file = data.file;
  const fileName = file.file?.name || file.name || "未知文件";

  // 文件类型校验
  const allowedTypes = [".docx", ".pdf", ".txt"];
  const fileExtension = fileName
    .toLowerCase()
    .substring(fileName.lastIndexOf("."));
  if (!allowedTypes.includes(fileExtension)) {
    message.error(
      `不支持的文件格式，请选择 ${allowedTypes.join("、")} 格式的文件`
    );
    return false;
  }

  // 文件大小校验（20MB）
  const maxSize = 20 * 1024 * 1024;
  if (file.file && file.file.size > maxSize) {
    message.error("文件大小不能超过 20MB");
    return false;
  }

  return true;
}

/**
 * 自定义上传请求（阻止默认上传行为）
 */
function handleCustomRequest(options: UploadCustomRequestOptions) {
  // 阻止默认上传，文件将在表单提交时统一处理
  options.onFinish();
}

/**
 * 重置表单
 */
function handleReset() {
  const hasData =
    form.value.name || form.value.description || form.value.file.length > 0;
  if (!hasData) {
    message.info("表单已是初始状态");
    return;
  }

  if (window.confirm("⚠️ 确定重置表单？所有数据将丢失")) {
    // 重置表单数据，文件列表通过v-model自动同步到BaseUpload组件
    form.value.name = "";
    form.value.description = "";
    form.value.file = [];
    message.info("表单已重置");
  }
}

/**
 * 提交处理
 */
function handleSubmit() {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      const firstError =
        Object.values(errors)
          .flat()
          .find((err) => err.message)?.message || "请检查表单输入";
      message.error(firstError);
      return;
    }

    submitting.value = true;
    try {
      if (!form.value.file[0]?.file) {
        message.error("请选择有效的文件");
        return;
      }

      const formData = new FormData();
      formData.append("name", form.value.name);
      formData.append("description", form.value.description);
      formData.append("file", form.value.file[0].file as Blob);

      await uploadAPI.uploadFile(formData);

      message.success("上传成功 🎉");
      // 上传成功后重置表单，文件列表通过v-model自动同步到BaseUpload组件
      form.value.name = "";
      form.value.description = "";
      form.value.file = [];
    } catch (err: unknown) {
      if (err && typeof err === "object" && "response" in err) {
        const axiosErr = err as { response: { data?: string } };
        message.error(axiosErr.response?.data || "上传失败，请重试");
      } else {
        message.error("上传失败，请重试");
      }
    } finally {
      submitting.value = false;
    }
  });
}

defineExpose({
  submit: handleSubmit,
  reset: handleReset,
  getFormData: () => ({ ...form.value }),
});
</script>

<style scoped>
/* 上传卡片样式 */
.upload-card {
  max-width: 600px;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

/* 移动端优化 */
@media (max-width: 639px) {
  .upload-card {
    border-radius: var(--border-radius-md);
    max-width: 100%;
  }
}
</style>
