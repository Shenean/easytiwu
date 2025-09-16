<template>
  <PageContainer
    :title="$t('settings.title')"
    card-class="settings-card"
    container-class="settings-container"
  >
    <div class="setting-section">
      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <i class="i-ion-moon"></i>
            </n-icon>
            <h3 class="setting-title">{{ $t("settings.darkTheme") }}</h3>
          </div>
        </div>

        <div class="theme-toggle-container">
          <n-button-group>
            <n-button
              :type="currentTheme === 'light' ? 'primary' : 'default'"
              @click="setTheme('light')"
              size="small"
              :ghost="currentTheme !== 'light'"
            >
              <template #icon>
                <n-icon>
                  <i class="i-ion-sunny"></i>
                </n-icon>
              </template>
              {{ $t('settings.lightTheme') }}
            </n-button>
            <n-button
              :type="currentTheme === 'dark' ? 'primary' : 'default'"
              @click="setTheme('dark')"
              size="small"
              :ghost="currentTheme !== 'dark'"
            >
              <template #icon>
                <n-icon>
                  <i class="i-ion-moon"></i>
                </n-icon>
              </template>
              {{ $t('settings.darkTheme') }}
            </n-button>
            <n-button
              :type="currentTheme === 'system' ? 'primary' : 'default'"
              @click="setTheme('system')"
              size="small"
              :ghost="currentTheme !== 'system'"
            >
              <template #icon>
                <n-icon>
                  <i class="i-ion-desktop-outline"></i>
                </n-icon>
              </template>
              {{ $t('settings.followSystem') }}
            </n-button>
          </n-button-group>
        </div>
      </div>
    </div>

    <div class="setting-section">
      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-header">
            <n-icon size="20" class="setting-icon">
              <i class="i-ion-language"></i>
            </n-icon>
            <h3 class="setting-title">{{ $t("settings.language.title") }}</h3>
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
          <i class="i-ion-settings-outline"></i>
        </n-icon>
        <h3 class="setting-title">{{ $t("settings.other") }}</h3>
      </div>
      <div class="setting-description">{{ $t("settings.moreFeatures") }}</div>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import {inject, onMounted, onUnmounted, ref} from "vue";
import {useMessage} from "../utils/message";
import {useI18n} from "vue-i18n";
import {getCurrentLocale, getFullLocale, getSimpleLocale, setLocale} from "../i18n";
import PageContainer from "../components/common/PageContainer.vue";

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
    light: t("settings.lightTheme"),
    dark: t("settings.darkTheme"),
    system: t("settings.followSystem"),
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
  }

  .setting-header {
    gap: var(--spacing-3); /* 12px */
    margin-bottom: var(--spacing-1); /* 4px */
  }

  .setting-icon {
    font-size: var(--font-size-lg);
  }

  .setting-title {
    font-size: var(--font-size-base);
    font-weight: 500;
    line-height: 1.4;
  }

  .setting-description {
    font-size: var(--font-size-sm);
    line-height: 1.4;
    margin-top: var(--spacing-1); /* 4px */
  }

  .theme-toggle-container {
    flex-shrink: 0;
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
  }

  .setting-header {
    gap: var(--spacing-2);
  }

  .setting-icon {
    font-size: var(--font-size-base);
  }

  .setting-title {
    font-size: var(--font-size-base);
    font-weight: 500;
  }

  .setting-description {
    font-size: var(--font-size-xs);
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
