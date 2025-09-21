<template>
  <PageContainer :title="t('settings.title')" :show-card="false" container-class="settings-container">
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
          <n-select v-model:value="currentTheme" :options="themeOptions" @update:value="setTheme" size="small"
            style="width: 120px" />
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
          <n-select v-model:value="currentLanguage" :options="languageOptions" @update:value="setLanguage" size="small"
            style="width: 120px" />
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
</style>
