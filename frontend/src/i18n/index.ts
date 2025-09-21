import {createI18n} from "vue-i18n";
import zhCN from "./locales/zh-CN.json";
import enUS from "./locales/en-US.json";

// 支持的语言列表
export const SUPPORT_LOCALES = ["zh-CN", "en-US"] as const;
export type SupportLocale = (typeof SUPPORT_LOCALES)[number];

// 默认语言
export const DEFAULT_LOCALE: SupportLocale = "zh-CN";

// 语言包映射
const messages = {
  "zh-CN": zhCN,
  "en-US": enUS,
};

// 获取浏览器语言
function getBrowserLocale(): SupportLocale {
  const browserLang = navigator.language || navigator.languages[0];

  // 匹配支持的语言
  for (const locale of SUPPORT_LOCALES) {
    if (browserLang.startsWith(locale.split("-")[0])) {
      return locale;
    }
  }

  return DEFAULT_LOCALE;
}

// 获取存储的语言设置
function getStoredLocale(): SupportLocale {
  const stored = localStorage.getItem("app-language") as SupportLocale;
  if (stored && SUPPORT_LOCALES.includes(stored)) {
    return stored;
  }
  return getBrowserLocale();
}

// 创建 i18n 实例
export const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: getStoredLocale(),
  fallbackLocale: DEFAULT_LOCALE,
  messages,
  globalInjection: true, // 全局注入 $t 函数
  silentTranslationWarn: true, // 静默翻译警告
  silentFallbackWarn: true, // 静默回退警告
});

// 切换语言函数
export function setLocale(locale: SupportLocale) {
  if (SUPPORT_LOCALES.includes(locale)) {
    i18n.global.locale.value = locale;
    localStorage.setItem("app-language", locale);
    document.documentElement.lang = locale;

    // 触发自定义事件，通知其他组件语言已切换
    window.dispatchEvent(
      new CustomEvent("locale-changed", { detail: { locale } })
    );
  }
}

// 获取当前语言
export function getCurrentLocale(): SupportLocale {
  return i18n.global.locale.value as SupportLocale;
}

// 获取语言显示名称
export function getLocaleDisplayName(locale: SupportLocale): string {
  const displayNames: Record<SupportLocale, string> = {
    "zh-CN": i18n.global.t("settings.language.chinese"),
    "en-US": i18n.global.t("settings.language.english"),
  };
  return displayNames[locale] || locale;
}

// 简化的语言映射（用于SettingsPage等组件）
export function getSimpleLocale(locale: SupportLocale): "zh" | "en" {
  return locale.startsWith("zh") ? "zh" : "en";
}

// 从简化语言获取完整locale
export function getFullLocale(simpleLang: "zh" | "en"): SupportLocale {
  return simpleLang === "zh" ? "zh-CN" : "en-US";
}

export default i18n;
