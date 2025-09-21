<template>
  <PageContainer
    :title="t('settings.title')"
    :show-card="false"
    container-class="settings-container"
  >
    <div class="setting-section">
      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <SettingsOutline />
            </n-icon>
            <h3 class="setting-title">{{ t("settings.theme.title") }}</h3>
          </div>
        </div>

        <div class="theme-toggle-container">
          <n-select
            v-model:value="currentTheme"
            :options="themeOptions"
            @update:value="setTheme"
            size="small"
            style="width: 120px"
          />
        </div>
      </div>
    </div>

    <div class="setting-section">
      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <Language />
            </n-icon>
            <h3 class="setting-title">{{ t("settings.language.title") }}</h3>
          </div>
        </div>
        <div class="language-toggle-container">
          <n-select
            v-model:value="currentLanguage"
            :options="languageOptions"
            @update:value="setLanguage"
            size="small"
            style="width: 120px"
          />
        </div>
      </div>
    </div>

    <div class="setting-section small">
      <div class="setting-header">
        <n-icon size="20" class="setting-icon">
          <SettingsOutline />
        </n-icon>
        <h3 class="setting-title">{{ t("settings.other") }}</h3>
      </div>
      <div class="setting-description">{{ t("settings.moreFeatures") }}</div>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import {inject, onMounted, onUnmounted, ref} from "vue";
import {useMessage} from "../utils/message";
import {useI18n} from "vue-i18n";
import {getCurrentLocale, getFullLocale, getSimpleLocale, setLocale} from "../i18n";
import PageContainer from "../components/common/PageContainer.vue";
import {Language, SettingsOutline} from "@vicons/ionicons5";

type Theme = "light" | "dark" | "system";
type Language = "zh" | "en";

const message = useMessage();
const { t } = useI18n();
const currentTheme = ref<Theme>("system");
const currentLanguage = ref<Language>("zh");

// 语言选项
const languageOptions = [
  {
    label: t('settings.language.chinese'),
    value: 'zh' as Language
  },
  {
    label: t('settings.language.english'), 
    value: 'en' as Language
  }
];

// 主题选项
const themeOptions = [
  {
    label: t('settings.theme.light'),
    value: 'light' as Theme
  },
  {
    label: t('settings.theme.dark'),
    value: 'dark' as Theme
  },
  {
    label: t('settings.theme.system'),
    value: 'system' as Theme
  }
];

const setGlobalTheme = inject<(theme: Theme) => void>("setGlobalTheme");
const getGlobalTheme = inject<() => Theme>("getGlobalTheme");

onMounted(() => {
  if (getGlobalTheme) {
    currentTheme.value = getGlobalTheme();
  } else {
    currentTheme.value =
      (localStorage.getItem("app-theme") as Theme) || "system";
  }

  // 初始化语言设置
  const currentLocale = getCurrentLocale();
  currentLanguage.value = getSimpleLocale(currentLocale);
  
  // 监听语言切换事件
  const localeChangeHandler = (event: Event) => {
    const customEvent = event as CustomEvent;
    const { locale } = customEvent.detail;
    currentLanguage.value = getSimpleLocale(locale);
  };
  window.addEventListener('locale-changed', localeChangeHandler as EventListener);
  
  // 清理事件监听器
  onUnmounted(() => {
    window.removeEventListener('locale-changed', localeChangeHandler as EventListener);
  });
});

function setTheme(theme: Theme) {
  currentTheme.value = theme;
  localStorage.setItem("app-theme", currentTheme.value);
  if (setGlobalTheme) setGlobalTheme(currentTheme.value);
  
  const themeTexts = {
    light: t("settings.theme.light"),
    dark: t("settings.theme.dark"),
    system: t("settings.theme.system"),
  };
  message.success(t("settings.switchedTo", { theme: themeTexts[theme] }));
}

function setLanguage(language: Language) {
  currentLanguage.value = language;
  const fullLocale = getFullLocale(language);
  setLocale(fullLocale);
  
  const languageTexts = {
    zh: t('settings.language.chinese'),
    en: t('settings.language.english'),
  };
  message.success(
    t("settings.language.switched", { language: languageTexts[language] })
  );
}
</script>

<style scoped>
.settings-container {
  max-width: var(--container-max-width-sm);
  margin: 0 auto;
  padding: var(--spacing-5);
  width: 100%;
  box-sizing: border-box;
}

.settings-card {
  border-radius: var(--card-border-radius-desktop);
  box-shadow: var(--card-shadow-medium);
  border: 1px solid var(--color-black-05);
  width: 100%;
  box-sizing: border-box;
}

.setting-section {
  padding: var(--spacing-4) var(--spacing-5);
  border-bottom: 1px dashed var(--n-border-color);
}

.setting-section.small {
  border-bottom: none;
  padding-bottom: var(--spacing-3);
}

/* 设置行布局 */
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-5);
}

.setting-info {
  flex: 1;
}

.setting-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
  margin-bottom: var(--spacing-1);
}

.setting-icon {
  color: var(--color-primary);
}

.setting-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--n-text-color);
}

.setting-description {
  color: var(--n-text-color-2);
  font-size: var(--font-size-xs);
  margin: 0;
}

/* 主题切换按钮 */
.theme-toggle-container {
  flex-shrink: 0;
}

/* 语言切换容器 */
.language-toggle-container {
  flex-shrink: 0;
}



/* 移动端设置页面优化 */
@media (max-width: 1023px) {
  .settings-container {
    padding: var(--container-responsive-padding-tablet);
    max-width: 100%;
  }

  .settings-card {
    border-radius: var(--card-border-radius-tablet);
    margin: 0;
    box-shadow: var(--card-shadow-tablet);
  }

  .setting-section {
    padding: var(--spacing-4) var(--spacing-5);
  }

  .setting-section.small {
    padding: var(--spacing-3) var(--spacing-5);
  }

  .setting-row {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: var(--spacing-4);
    min-height: var(--spacing-14);
  }

  .setting-info {
    flex: 1;
    min-width: 0; /* 允许文本截断 */
  }

  .setting-header {
    gap: var(--spacing-3); /* 12px */
    margin-bottom: var(--spacing-1); /* 4px */
  }

  .setting-icon {
    font-size: var(--font-size-lg);
    flex-shrink: 0;
  }

  .setting-title {
    font-size: var(--font-size-base);
    font-weight: 500;
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .setting-description {
    font-size: var(--font-size-sm);
    line-height: 1.4;
    margin-top: var(--spacing-1); /* 4px */
  }

  .theme-toggle-container {
    flex-shrink: 0;
    min-width: 0;
  }

  /* 平板端按钮组优化 */
  .theme-toggle-container :deep(.n-button-group) {
    display: flex;
    flex-direction: row;
    gap: 0;
    width: auto;
  }

  .theme-toggle-container :deep(.n-button) {
    font-size: var(--font-size-xs);
    padding: 0 var(--spacing-2);
    min-width: auto;
    white-space: nowrap;
  }

  .theme-toggle-container :deep(.n-button .n-button__content) {
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .language-toggle-container {
    flex-shrink: 0;
  }



  /* 表单组件优化 */
  :deep(.n-radio-group) {
    width: 100%;
  }

  :deep(.n-radio) {
    width: 100%;
    min-height: var(--spacing-12); /* 48px */
    padding: var(--spacing-2) var(--spacing-3);
  }

  :deep(.n-radio__dot) {
    transform: scale(1.1);
  }

  :deep(.n-card__header) {
    padding: var(--spacing-5) var(--spacing-5) 0;
  }

  :deep(.n-card__content) {
    padding: 0;
  }
}

@media (max-width: 639px) {
  .settings-container {
    padding: var(--container-responsive-padding-mobile);
  }

  .settings-card {
    border-radius: var(--card-border-radius-mobile);
    box-shadow: var(--card-shadow-mobile);
  }

  .setting-section {
    padding: var(--spacing-4) var(--spacing-4); /* 16px 16px */
  }

  .setting-section.small {
    padding: var(--spacing-3) var(--spacing-4); /* 12px 16px */
  }

  .setting-row {
    gap: var(--spacing-3);
    min-height: var(--spacing-13); /* 52px */
    flex-wrap: nowrap;
    overflow: hidden;
  }

  .setting-info {
    flex: 1;
    min-width: 0;
    overflow: hidden;
  }

  .setting-header {
    gap: var(--spacing-2);
    overflow: hidden;
  }

  .setting-icon {
    font-size: var(--font-size-base);
    flex-shrink: 0;
  }

  .setting-title {
    font-size: var(--font-size-sm);
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .setting-description {
    font-size: var(--font-size-xs);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  /* 手机端按钮组优化 */
  .theme-toggle-container {
    flex-shrink: 0;
    min-width: 0;
    max-width: 40%;
  }

  .theme-toggle-container :deep(.n-button-group) {
    display: flex;
    flex-direction: row;
    gap: 0;
    width: 100%;
  }

  .theme-toggle-container :deep(.n-button) {
    font-size: 10px;
    padding: 0 var(--spacing-1);
    min-width: 0;
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
  }

  .theme-toggle-container :deep(.n-button .n-button__content) {
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 10px;
  }

  .language-toggle-container {
    flex-shrink: 0;
    min-width: 0;
    max-width: 35%;
  }

  .language-toggle-container :deep(.n-select) {
    min-width: 0;
  }

  .language-toggle-container :deep(.n-base-selection) {
    min-width: 0;
  }

  .language-toggle-container :deep(.n-base-selection-label) {
    overflow: hidden;
    text-overflow: ellipsis;
  }

  :deep(.n-card__header) {
    padding: var(--spacing-4) var(--spacing-4) 0;
  }

  :deep(.n-radio) {
    min-height: var(--spacing-11);
    padding: var(--spacing-2) var(--spacing-3); /* 8px 12px */
  }
}

/* 横屏模式优化 */
@media (max-width: 1023px) and (orientation: landscape) {
  .settings-container {
    padding: var(--spacing-2) var(--spacing-4);
  }

  .setting-section {
    padding: var(--spacing-3) var(--spacing-5);
  }

  .setting-row {
    min-height: var(--spacing-12);
  }


}

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .settings-container {
    padding: var(--spacing-1) var(--spacing-2);
  }

  .settings-card {
    border-radius: var(--spacing-2);
  }

  .setting-section {
    padding: var(--spacing-2) var(--spacing-3);
  }

  .setting-row {
    min-height: var(--spacing-10);
    padding: var(--spacing-2) 0;
    gap: var(--spacing-2);
  }

  .setting-info {
    flex: 1;
    min-width: 0;
  }

  .setting-title {
    font-size: 12px;
  }

  .setting-description {
    font-size: 10px;
  }

  /* 超小屏幕按钮组优化 */
  .theme-toggle-container {
    max-width: 45%;
  }

  .theme-toggle-container :deep(.n-button) {
    font-size: 9px;
    padding: 0 2px;
  }

  .theme-toggle-container :deep(.n-button .n-button__content) {
    font-size: 9px;
  }

  .language-toggle-container {
    max-width: 40%;
  }

  .language-toggle-container :deep(.n-base-selection-label) {
    font-size: 10px;
  }
}

/* 无障碍支持 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}



/* 触摸优化 */
@media (hover: none) and (pointer: coarse) {
  .setting-row {
    min-height: var(--spacing-14);
  }

  .setting-section {
    padding: var(--spacing-4) var(--spacing-5);
  }
}
</style>
