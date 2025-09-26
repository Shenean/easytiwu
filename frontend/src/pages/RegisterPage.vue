<template>
  <div class="register-page">
    <div class="register-container">
      <t-card class="register-card" :bordered="false" :shadow="true">
        <template #title>
          <div class="card-header">
            <h2 class="card-title">{{ t("auth.register.title") }}</h2>
            <p class="card-subtitle">{{ t("auth.register.subtitle") }}</p>
          </div>
        </template>
        
        <t-form
          ref="formRef"
          :data="formData"
          :rules="rules"
          :label-width="0"
          @submit="onSubmit"
        >
          <t-form-item name="username">
            <t-input 
              v-model="formData.username" 
              :placeholder="t('auth.register.usernamePlaceholder')"
              size="large"
              clearable
            >
              <template #prefix-icon>
                <t-icon name="user" />
              </template>
            </t-input>
          </t-form-item>
          
          <t-form-item name="email">
            <t-input 
              v-model="formData.email" 
              :placeholder="t('auth.register.emailPlaceholder')"
              size="large"
              clearable
            >
              <template #prefix-icon>
                <t-icon name="mail" />
              </template>
            </t-input>
          </t-form-item>
          
          <t-form-item name="password">
            <t-input
              v-model="formData.password"
              :placeholder="t('auth.register.passwordPlaceholder')"
              type="password"
              size="large"
            >
              <template #prefix-icon>
                <t-icon name="lock-on" />
              </template>
            </t-input>
          </t-form-item>
          
          <t-form-item name="confirmPassword">
            <t-input
              v-model="formData.confirmPassword"
              :placeholder="t('auth.register.confirmPasswordPlaceholder')"
              type="password"
              size="large"
            >
              <template #prefix-icon>
                <t-icon name="lock-on" />
              </template>
            </t-input>
          </t-form-item>
          
          <t-form-item>
            <t-button 
              type="submit" 
              theme="primary"
              size="large" 
              :loading="loading"
              block
            >
              {{ t("auth.register.submit") }}
            </t-button>
          </t-form-item>
        </t-form>
        
        <div class="register-footer">
          <span>{{ t("auth.register.hasAccount") }}</span>
          <t-link theme="primary" @click="goToLogin">{{ t("auth.register.loginNow") }}</t-link>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, reactive, ref, watch} from "vue";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import type {FormInstanceFunctions, FormRule, SubmitContext} from "tdesign-vue-next";
import {useMessage} from "../utils/message";
import {authAPI, type UserRegisterDTO} from "../api/auth";

const { t } = useI18n();
const router = useRouter();
const message = useMessage();

const formRef = ref<FormInstanceFunctions | null>(null);
const loading = ref(false);

const formData = reactive<UserRegisterDTO & { confirmPassword: string }>({
  username: "",
  email: "",
  password: "",
  confirmPassword: ""
});

const rules = computed(() => ({
  username: [
    { required: true, message: t("auth.register.usernameRequired"), trigger: "blur" },
    { min: 2, message: t("auth.register.usernameMinLength"), trigger: "blur" },
    { max: 32, message: t("auth.register.usernameMaxLength"), trigger: "blur" }
  ],
  email: [
    { required: true, message: t("auth.register.emailRequired"), trigger: "blur" },
    { email: { ignoreBracket: true }, message: t("auth.register.emailInvalid"), trigger: "blur" },
    { max: 64, message: t("auth.register.emailMaxLength"), trigger: "blur" }
  ],
  password: [
    { required: true, message: t("auth.register.passwordRequired"), trigger: "blur" },
    { min: 6, message: t("auth.register.passwordMinLength"), trigger: "blur" },
    { max: 32, message: t("auth.register.passwordMaxLength"), trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: t("auth.register.confirmPasswordRequired"), trigger: "blur" },
    {
      validator: (val: string) => val === formData.password,
      message: t("auth.register.passwordMismatch"),
      trigger: ["blur", "change"]
    }
  ]
})) as unknown as Record<string, Array<FormRule>>;

// 监听密码字段变化，重新验证确认密码
watch(() => formData.password, () => {
  if (formData.confirmPassword && formRef.value) {
    formRef.value.validate({ fields: ['confirmPassword'] });
  }
});

const onSubmit = (e: SubmitContext) => {
  if (e.validateResult !== true) return;
  
  loading.value = true;
  
  authAPI.register({
    username: formData.username,
    email: formData.email,
    password: formData.password
  })
    .then(response => {
      const { data } = response;
      
      if (data.success) {
        message.success(t("auth.register.success"));
        // 注册成功后跳转到登录页面
        router.push("/login");
      } else {
        message.error(data.message || t("auth.register.failed"));
      }
    })
    .catch(error => {
      console.error("Register error:", error);
      message.error(t("auth.register.failed"));
    })
    .finally(() => {
      loading.value = false;
    });
};

const goToLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 400px;
}

.register-card {
  border-radius: var(--td-radius-extraLarge);
}

.card-header {
  text-align: center;
  margin-bottom: 24px;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: var(--td-text-color-primary);
}

.card-subtitle {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  margin: 8px 0 0;
}

.register-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--td-border-level-1-color);
}

:deep(.t-input__prefix) {
  margin-right: 8px;
}
</style>
