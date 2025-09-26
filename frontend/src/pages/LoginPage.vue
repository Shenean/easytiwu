<template>
  <div class="login-page">
    <div class="login-container">
      <t-card class="login-card" :bordered="false" :shadow="true">
        <template #title>
          <div class="card-header">
            <h2 class="card-title">{{ t("auth.login.title") }}</h2>
            <p class="card-subtitle">{{ t("auth.login.subtitle") }}</p>
          </div>
        </template>
        
        <t-form
          ref="formRef"
          :data="formData"
          :rules="rules"
          :label-width="0"
          @submit="onSubmit"
        >
          <t-form-item name="email">
            <t-input 
              v-model="formData.email" 
              :placeholder="t('auth.login.emailPlaceholder')"
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
              :placeholder="t('auth.login.passwordPlaceholder')"
              type="password"
              size="large"
            >
              <template #prefix-icon>
                <t-icon name="lock-on" />
              </template>
            </t-input>
          </t-form-item>
          
          <t-form-item>
            <t-checkbox v-model="rememberMe">{{ t("auth.login.rememberMe") }}</t-checkbox>
          </t-form-item>
          
          <t-form-item>
            <t-button 
              type="submit" 
              theme="primary"
              size="large" 
              :loading="loading"
              block
            >
              {{ t("auth.login.submit") }}
            </t-button>
          </t-form-item>
        </t-form>
        
        <div class="login-footer">
          <span>{{ t("auth.login.noAccount") }}</span>
          <t-link theme="primary" @click="goToRegister">{{ t("auth.login.registerNow") }}</t-link>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import type {FormInstanceFunctions, FormRule, SubmitContext} from "tdesign-vue-next";
import {useMessage} from "../utils/message";
import {authAPI, type UserLoginDTO} from "../api/auth";

const { t } = useI18n();
const router = useRouter();
const message = useMessage();

const formRef = ref<FormInstanceFunctions | null>(null);
const loading = ref(false);

const formData = reactive<UserLoginDTO>({
  email: "",
  password: ""
});

const rememberMe = ref(false);

const rules = {
  email: [
    { required: true, message: t("auth.login.emailRequired"), trigger: "blur" },
    { email: { ignoreBracket: true }, message: t("auth.login.emailInvalid"), trigger: "blur" }
  ],
  password: [
    { required: true, message: t("auth.login.passwordRequired"), trigger: "blur" },
    { min: 6, message: t("auth.login.passwordMinLength"), trigger: "blur" },
    { max: 32, message: t("auth.login.passwordMaxLength"), trigger: "blur" }
  ]
} as Record<string, Array<FormRule>>;

const onSubmit = (e: SubmitContext) => {
  if (e.validateResult !== true) return;
  
  loading.value = true;
  
  authAPI.login(formData)
    .then(response => {
      const { data } = response;
      
      if (data.success) {
        message.success(t("auth.login.success"));
        // 保存用户信息到本地存储（如果选择了记住我）
        if (rememberMe.value) {
          localStorage.setItem("user-info", JSON.stringify(data.data));
        }
        // 跳转到主页
        router.push("/");
      } else {
        message.error(data.message || t("auth.login.failed"));
      }
    })
    .catch(error => {
      console.error("Login error:", error);
      message.error(t("auth.login.failed"));
    })
    .finally(() => {
      loading.value = false;
    });
};

const goToRegister = () => {
  router.push("/register");
};
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-card {
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

.login-footer {
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
