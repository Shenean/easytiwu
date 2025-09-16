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
    const isDark = actualTheme.value === "dark";
    
    const baseOverrides: GlobalThemeOverrides = {
      common: {
        // 主色调 - 基于设计令牌的标准蓝色
        primaryColor: "#2080f0",
        primaryColorHover: "#3090ff",
        primaryColorPressed: "#1070e0",
        primaryColorSuppl: "#80c2ff",

        // 状态颜色 - 与设计令牌保持一致
        successColor: "#18a058",
        successColorHover: "#4ade80",
        successColorPressed: "#16a34a",
        
        warningColor: "#f0a020",
        warningColorHover: "#fbbf24",
        warningColorPressed: "#d97706",
        
        errorColor: "#f04040",
        errorColorHover: "#f87171",
        errorColorPressed: "#dc2626",
        
        infoColor: "#2080f0",
        infoColorHover: "#3090ff",
        infoColorPressed: "#1070e0",

        // 文本颜色 - 基于设计令牌
        textColorBase: isDark ? "#f5f5f5" : "#2c2c2c",
        textColor1: isDark ? "#f5f5f5" : "#2c2c2c",
        textColor2: isDark ? "#d1d5db" : "#666666",
        textColor3: isDark ? "#9ca3af" : "#999999",
        textColorDisabled: isDark ? "#6b7280" : "#cccccc",

        // 背景颜色
        bodyColor: isDark ? "#1e1e1e" : "#ffffff",
        cardColor: isDark ? "#262626" : "#ffffff",
        modalColor: isDark ? "#262626" : "#ffffff",
        popoverColor: isDark ? "#262626" : "#ffffff",
        tableHeaderColor: isDark ? "#1f1f1f" : "#fafafa",
        
        // 边框颜色
        borderColor: isDark ? "#404040" : "#d9d9d9",
        dividerColor: isDark ? "#404040" : "#e5e5e5",
        
        // 悬停和焦点效果
        hoverColor: isDark ? "rgba(255, 255, 255, 0.05)" : "rgba(32, 128, 240, 0.1)",
        pressedColor: isDark ? "rgba(255, 255, 255, 0.1)" : "rgba(32, 128, 240, 0.15)",
        
        // 输入框相关
        inputColor: isDark ? "#262626" : "#ffffff",
        inputColorDisabled: isDark ? "#1a1a1a" : "#f5f5f5",
        
        // 阴影
        boxShadow1: isDark 
          ? "0 1px 2px -2px rgba(0, 0, 0, 0.8), 0 3px 6px 0 rgba(0, 0, 0, 0.6), 0 5px 12px 4px rgba(0, 0, 0, 0.4)"
          : "0 1px 2px -2px rgba(0, 0, 0, 0.08), 0 3px 6px 0 rgba(0, 0, 0, 0.06), 0 5px 12px 4px rgba(0, 0, 0, 0.04)",
        boxShadow2: isDark
          ? "0 3px 6px -4px rgba(0, 0, 0, 0.8), 0 6px 16px 0 rgba(0, 0, 0, 0.6), 0 9px 28px 8px rgba(0, 0, 0, 0.4)"
          : "0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 9px 28px 8px rgba(0, 0, 0, 0.05)",
        boxShadow3: isDark
          ? "0 6px 16px -9px rgba(0, 0, 0, 0.8), 0 9px 28px 0 rgba(0, 0, 0, 0.6), 0 12px 48px 16px rgba(0, 0, 0, 0.4)"
          : "0 6px 16px -9px rgba(0, 0, 0, 0.08), 0 9px 28px 0 rgba(0, 0, 0, 0.05), 0 12px 48px 16px rgba(0, 0, 0, 0.03)",
      },
      
      // 按钮组件特定覆盖
      Button: {
        borderRadiusTiny: "4px",
        borderRadiusSmall: "6px",
        borderRadiusMedium: "8px",
        borderRadiusLarge: "10px",
      },
      
      // 卡片组件特定覆盖
      Card: {
        borderRadius: "12px",
        paddingMedium: "20px",
        paddingLarge: "24px",
      },
      
      // 输入框组件特定覆盖
      Input: {
        borderRadius: "8px",
        heightMedium: "40px",
        heightLarge: "48px",
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
    
    // 为body添加主题类名，便于CSS选择器使用
    document.body.className = document.body.className.replace(/theme-\w+/g, '');
    document.body.classList.add(`theme-${theme}`);

    // 更新CSS自定义属性以支持主题切换
    const root = document.documentElement;
    if (theme === "dark") {
      // 深色主题的CSS变量覆盖
      root.style.setProperty("--app-bg-color", "#1e1e1e");
      root.style.setProperty("--app-text-color", "#f5f5f5");
      root.style.setProperty("--app-border-color", "#404040");
      root.style.setProperty("--app-card-bg", "#262626");
      root.style.setProperty("--app-hover-color", "rgba(255, 255, 255, 0.05)");
    } else {
      // 浅色主题的CSS变量覆盖
      root.style.setProperty("--app-bg-color", "#ffffff");
      root.style.setProperty("--app-text-color", "#2c2c2c");
      root.style.setProperty("--app-border-color", "#d9d9d9");
      root.style.setProperty("--app-card-bg", "#ffffff");
      root.style.setProperty("--app-hover-color", "rgba(32, 128, 240, 0.1)");
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
