import {computed, inject, onMounted, provide, readonly, ref, type Ref, watch,} from "vue";

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

  // TDesign 主题配置
  const tdesignTheme = computed(() => {
    return actualTheme.value;
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

    document.body.className = document.body.className.replace(/theme-\w+/g, "");
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
    tdesignTheme,
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
