/**
 * 主题管理 Composable
 * 提供统一的主题切换和管理功能
 * 符合UI设计规范的主题系统实现
 */

import {computed, inject, onMounted, provide, readonly, ref, type Ref, watch,} from "vue";
import {darkTheme, type GlobalThemeOverrides} from "naive-ui";

export type ThemeMode = "light" | "dark" | "auto";

/**
 * 主题管理 Hook
 * @returns 主题相关的状态和方法
 */
export function useTheme() {
  // 主题状态管理
  const currentTheme = ref<ThemeMode>("light");
  const systemPrefersDark = ref(false);

  // 计算实际应用的主题
  const actualTheme = computed(() => {
    if (currentTheme.value === "auto") {
      return systemPrefersDark.value ? "dark" : "light";
    }
    return currentTheme.value;
  });

  // Naive UI 主题配置
  const naiveTheme = computed(() => {
    return actualTheme.value === "dark" ? darkTheme : null;
  });

  // 主题覆盖配置 - 使用UI设计规范推荐的标准色板
  const themeOverrides = computed<GlobalThemeOverrides>(() => {
    const baseOverrides: GlobalThemeOverrides = {
      common: {
        // 主色系 - 蓝色
        primaryColor: "#2080F0",
        primaryColorHover: "#3090FF",
        primaryColorPressed: "#1070E0",
        primaryColorSuppl: "#3090FF",

        // 功能色系
        successColor: "#18A058",
        warningColor: "#F0A020",
        errorColor: "#F04040",
        infoColor: "#2080F0",

        // 文本颜色
        textColorBase: actualTheme.value === "dark" ? "#F5F5F5" : "#2C2C2C",
        textColor2: "#666666",

        // 边框和背景
        borderColor: "#D9D9D9",
        bodyColor: actualTheme.value === "dark" ? "#1E1E1E" : "#FFFFFF",

        // 悬停效果
        hoverColor: "rgba(32, 128, 240, 0.1)",
      },
    };

    return baseOverrides;
  });

  // 监听系统主题变化
  const updateSystemTheme = () => {
    systemPrefersDark.value = window.matchMedia(
      "(prefers-color-scheme: dark)"
    ).matches;
  };

  // 设置全局主题
  const setTheme = (theme: ThemeMode) => {
    currentTheme.value = theme;
    localStorage.setItem("app-theme", theme);
    applyThemeToDocument();
  };

  // 获取当前主题
  const getTheme = () => {
    return currentTheme.value;
  };

  // 应用主题到文档
  const applyThemeToDocument = () => {
    const theme = actualTheme.value;
    document.documentElement.setAttribute("data-theme", theme);

    // 更新CSS自定义属性以支持主题切换
    const root = document.documentElement;
    if (theme === "dark") {
      root.style.setProperty("--app-bg-color", "#1E1E1E");
      root.style.setProperty("--app-text-color", "#F5F5F5");
    } else {
      root.style.setProperty("--app-bg-color", "#FFFFFF");
      root.style.setProperty("--app-text-color", "#2C2C2C");
    }
  };

  // 切换主题（在light和dark之间切换）
  const toggleTheme = () => {
    const newTheme = actualTheme.value === "dark" ? "light" : "dark";
    setTheme(newTheme);
  };

  // 初始化主题
  const initTheme = () => {
    // 从localStorage读取保存的主题
    const savedTheme =
      (localStorage.getItem("app-theme") as ThemeMode) || "light";
    currentTheme.value = savedTheme;

    // 初始化系统主题检测
    updateSystemTheme();

    // 监听系统主题变化
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    mediaQuery.addEventListener("change", updateSystemTheme);

    // 应用初始主题
    applyThemeToDocument();

    // 清理函数
    return () => {
      mediaQuery.removeEventListener("change", updateSystemTheme);
    };
  };

  // 监听主题变化
  watch(actualTheme, () => {
    applyThemeToDocument();
  });

  // 组件挂载时初始化
  onMounted(() => {
    initTheme();
  });

  return {
    // 状态
    currentTheme: readonly(currentTheme),
    actualTheme: readonly(actualTheme),
    systemPrefersDark: readonly(systemPrefersDark),

    // Naive UI 配置
    naiveTheme,
    themeOverrides,

    // 方法
    setTheme,
    getTheme,
    toggleTheme,
    initTheme,
  };
}

/**
 * 主题提供者 Hook
 * 用于在应用根组件中提供主题功能
 */
export function useThemeProvider() {
  const theme = useTheme();

  // 提供给子组件的方法
  const provideTheme = () => {
    provide("setGlobalTheme", theme.setTheme);
    provide("getGlobalTheme", theme.getTheme);
    provide("toggleTheme", theme.toggleTheme);
    provide("currentTheme", theme.currentTheme);
    provide("actualTheme", theme.actualTheme);
  };

  return {
    ...theme,
    provideTheme,
  };
}

/**
 * 主题注入 Hook
 * 用于在子组件中使用主题功能
 */
export function useThemeInject() {
  const setGlobalTheme = inject<(theme: ThemeMode) => void>("setGlobalTheme");
  const getGlobalTheme = inject<() => ThemeMode>("getGlobalTheme");
  const toggleTheme = inject<() => void>("toggleTheme");
  const currentTheme = inject<Ref<ThemeMode>>("currentTheme");
  const actualTheme = inject<Ref<"light" | "dark">>("actualTheme");

  return {
    setGlobalTheme,
    getGlobalTheme,
    toggleTheme,
    currentTheme,
    actualTheme,
  };
}
