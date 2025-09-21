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


  const themeOverrides = computed<GlobalThemeOverrides>(() => {
    const isDark = actualTheme.value === "dark";
    
    const baseOverrides: GlobalThemeOverrides = {
      common: {

        primaryColor: "#2080f0",          // 主色调
        primaryColorHover: "#3090ff",     // 悬停状态
        primaryColorPressed: "#1070e0",   // 按下状态
        primaryColorSuppl: "#80c2ff",     // 补充色


        successColor: "#18a058",          // 成功色
        successColorHover: "#4ade80",     // 成功色悬停
        successColorPressed: "#16a34a",   // 成功色按下
        
        warningColor: "#f0a020",          // 警告色
        warningColorHover: "#fbbf24",     // 警告色悬停
        warningColorPressed: "#d97706",   // 警告色按下
        
        errorColor: "#f04040",            // 错误色
        errorColorHover: "#f87171",       // 错误色悬停
        errorColorPressed: "#dc2626",     // 错误色按下
        
        infoColor: "#2080f0",             // 信息色
        infoColorHover: "#3090ff",        // 信息色悬停
        infoColorPressed: "#1070e0",      // 信息色按下


        textColorBase: isDark ? "#f5f5f5" : "#2c2c2c",      // 基础文本色
        textColor1: isDark ? "#f5f5f5" : "#2c2c2c",         // 主要文本色
        textColor2: isDark ? "#d1d5db" : "#666666",         // 次要文本色
        textColor3: isDark ? "#9ca3af" : "#999999",         // 辅助文本色
        textColorDisabled: isDark ? "#6b7280" : "#cccccc", // 禁用文本色


        bodyColor: isDark ? "#1e1e1e" : "#ffffff",          // 页面背景色
        cardColor: isDark ? "#262626" : "#ffffff",          // 卡片背景色
        modalColor: isDark ? "#262626" : "#ffffff",         // 模态框背景色
        popoverColor: isDark ? "#262626" : "#ffffff",       // 弹出层背景色
        tableHeaderColor: isDark ? "#1f1f1f" : "#fafafa",  // 表格头部背景色
        

        borderColor: isDark ? "#404040" : "#d9d9d9",        // 边框色
        dividerColor: isDark ? "#404040" : "#e5e5e5",       // 分割线色
        

        hoverColor: isDark ? "rgba(255, 255, 255, 0.05)" : "rgba(32, 128, 240, 0.1)",   // 悬停效果
        pressedColor: isDark ? "rgba(255, 255, 255, 0.1)" : "rgba(32, 128, 240, 0.15)", // 按下效果
        

        inputColor: isDark ? "#262626" : "#ffffff",         // 输入框背景色
        inputColorDisabled: isDark ? "#1a1a1a" : "#f5f5f5", // 禁用输入框背景色
        

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
      

      Button: {
        borderRadiusTiny: "4px",
        borderRadiusSmall: "6px",
        borderRadiusMedium: "8px",
        borderRadiusLarge: "10px",
      },
      

      Card: {
        borderRadius: "12px",
        paddingMedium: "20px",
        paddingLarge: "24px",
      },
      

      Input: {
        borderRadius: "8px",
        heightMedium: "40px",
        heightLarge: "48px",
      },
    };

    return baseOverrides;
  });


  const updateSystemTheme = () => {
    systemPrefersDark.value = window.matchMedia(
      "(prefers-color-scheme: dark)"
    ).matches;
  };


  const setTheme = (theme: ThemeMode) => {
    currentTheme.value = theme;
    localStorage.setItem("app-theme", theme);
    applyThemeToDocument();
  };


  const getTheme = () => {
    return currentTheme.value;
  };


  const applyThemeToDocument = () => {
    const theme = actualTheme.value;
    

    document.documentElement.setAttribute("data-theme", theme);
    

    document.body.className = document.body.className.replace(/theme-\w+/g, '');
    document.body.classList.add(`theme-${theme}`);


    const root = document.documentElement;
    if (theme === "dark") {
      root.classList.add("dark");
      root.style.setProperty("--app-bg-color", "#1e1e1e");
      root.style.setProperty("--app-text-color", "#f5f5f5");
      root.style.setProperty("--app-border-color", "#404040");
      root.style.setProperty("--app-card-bg", "#262626");
      root.style.setProperty("--app-hover-color", "rgba(255, 255, 255, 0.05)");
    } else {
      root.classList.remove("dark");
      root.style.setProperty("--app-bg-color", "#ffffff");
      root.style.setProperty("--app-text-color", "#2c2c2c");
      root.style.setProperty("--app-border-color", "#d9d9d9");
      root.style.setProperty("--app-card-bg", "#ffffff");
      root.style.setProperty("--app-hover-color", "rgba(32, 128, 240, 0.1)");
    }
  };


  const toggleTheme = () => {
    const newTheme = actualTheme.value === "dark" ? "light" : "dark";
    setTheme(newTheme);
  };


  const initTheme = () => {

    const savedTheme =
      (localStorage.getItem("app-theme") as ThemeMode) || "light";
    currentTheme.value = savedTheme;


    updateSystemTheme();


    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    mediaQuery.addEventListener("change", updateSystemTheme);


    applyThemeToDocument();


    return () => {
      mediaQuery.removeEventListener("change", updateSystemTheme);
    };
  };


  watch(actualTheme, () => {
    applyThemeToDocument();
  });


  onMounted(() => {
    initTheme();
  });

  return {
    currentTheme: readonly(currentTheme),
    actualTheme: readonly(actualTheme),
    systemPrefersDark: readonly(systemPrefersDark),
    naiveTheme,
    themeOverrides,
    setTheme,
    getTheme,
    toggleTheme,
    initTheme,
  };
}


export function useThemeProvider() {
  const theme = useTheme();


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
