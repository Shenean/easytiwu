<template>
  <PageContainer
    :title="t('settings.title')"
    :show-card="false"
    container-class="settings-container"
  >
    <div class="settings-grid">
      <!-- 外观设置卡片 -->
      <t-card class="setting-card" :bordered="true" hover-shadow>
        <template #header>
          <div class="card-header">
            <t-icon name="palette" size="24px" class="card-icon" />
            <h3 class="card-title">{{ t("settings.appearance") }}</h3>
          </div>
        </template>
        
        <t-space direction="vertical" size="large">
          <!-- 主题设置 -->
          <div class="setting-item">
            <div class="setting-item-header">
              <div class="setting-item-info">
                <t-icon name="setting" size="18px" class="setting-item-icon" />
                <div class="setting-item-content">
                  <h4 class="setting-item-title">{{ t("settings.theme.title") }}</h4>
                  <p class="setting-item-description">{{ t("settings.theme.description") }}</p>
                </div>
              </div>
              <div class="setting-item-control">
                <t-select
                  v-model="currentTheme"
                  :options="themeOptions"
                  @change="setTheme"
                  size="medium"
                  style="width: 120px"
                />
              </div>
            </div>
          </div>

          <!-- 语言设置 -->
          <div class="setting-item">
            <div class="setting-item-header">
              <div class="setting-item-info">
                <t-icon name="translate" size="18px" class="setting-item-icon" />
                <div class="setting-item-content">
                  <h4 class="setting-item-title">{{ t("settings.language.title") }}</h4>
                  <p class="setting-item-description">{{ t("settings.language.description") }}</p>
                </div>
              </div>
              <div class="setting-item-control">
                <t-select
                  v-model="currentLanguage"
                  :options="languageOptions"
                  @change="setLanguage"
                  size="medium"
                  style="width: 120px"
                />
              </div>
            </div>
          </div>
        </t-space>
      </t-card>

      <!-- 其他设置卡片 -->
      <t-card class="setting-card" :bordered="true" hover-shadow>
        <template #header>
          <div class="card-header">
            <t-icon name="tools" size="24px" class="card-icon" />
            <h3 class="card-title">{{ t("settings.other") }}</h3>
          </div>
        </template>
        
        <div class="coming-soon">
          <t-icon name="time" size="48px" class="coming-soon-icon" />
          <h4 class="coming-soon-title">{{ t("settings.comingSoon") }}</h4>
          <p class="coming-soon-description">{{ t("settings.moreFeatures") }}</p>
        </div>
      </t-card>
    </div>
  </PageContainer>
</template>

<script setup lang="ts">
import {inject, onMounted, onUnmounted, ref} from "vue";
import {useMessage} from "../utils/message";
import {useI18n} from "vue-i18n";
import {getCurrentLocale, getFullLocale, getSimpleLocale, setLocale,} from "../i18n";
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
    label: t("settings.language.chinese"),
    value: "zh" as Language,
  },
  {
    label: t("settings.language.english"),
    value: "en" as Language,
  },
];

// 主题选项
const themeOptions = [
  {
    label: t("settings.theme.light"),
    value: "light" as Theme,
  },
  {
    label: t("settings.theme.dark"),
    value: "dark" as Theme,
  },
  {
    label: t("settings.theme.system"),
    value: "system" as Theme,
  },
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
  window.addEventListener(
    "locale-changed",
    localeChangeHandler as EventListener
  );

  // 清理事件监听器
  onUnmounted(() => {
    window.removeEventListener(
      "locale-changed",
      localeChangeHandler as EventListener
    );
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
    zh: t("settings.language.chinese"),
    en: t("settings.language.english"),
  };
  message.success(
    t("settings.language.switched", { language: languageTexts[language] })
  );
}
</script>

<style scoped>
.settings-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.setting-card {
  background: var(--td-bg-color-container);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid var(--td-border-level-1-color);
  overflow: hidden;
}

.setting-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-bottom: 1px solid var(--td-border-level-1-color);
  background: var(--td-bg-color-container-hover);
}

.card-icon {
  color: var(--td-brand-color);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--td-text-color-primary);
  margin: 0;
}

.setting-item {
  padding: 20px 24px;
}

.setting-item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.setting-item-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
}

.setting-item-icon {
  color: var(--td-brand-color);
  margin-top: 2px;
}

.setting-item-content {
  flex: 1;
}

.setting-item-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--td-text-color-primary);
  margin: 0 0 4px 0;
}

.setting-item-description {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  line-height: 1.5;
  margin: 0;
}

.setting-item-control {
  flex-shrink: 0;
}

.coming-soon {
  text-align: center;
  padding: 40px 20px;
  color: var(--td-text-color-placeholder);
}

.coming-soon-icon {
  color: var(--td-text-color-placeholder);
  margin-bottom: 16px;
}

.coming-soon-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--td-text-color-secondary);
  margin: 0 0 8px 0;
}

.coming-soon-description {
  font-size: 14px;
  color: var(--td-text-color-placeholder);
  margin: 0;
}

@media (max-width: 768px) {
  .settings-container {
    padding: 16px;
  }
  
  .settings-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .card-header {
    padding: 16px 20px;
  }
  
  .setting-item {
    padding: 16px 20px;
  }
  
  .setting-item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .setting-item-control {
    width: 100%;
  }
  
  .coming-soon {
    padding: 30px 15px;
  }
}

@media (max-width: 480px) {
  .settings-container {
    padding: 12px;
  }
  
  .settings-grid {
    gap: 12px;
  }
  
  .setting-card {
    border-radius: 8px;
  }
  
  .card-header {
    padding: 12px 16px;
  }
  
  .setting-item {
    padding: 12px 16px;
  }
  
  .card-title {
    font-size: 16px;
  }
}</style>
