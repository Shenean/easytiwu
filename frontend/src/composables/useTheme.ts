/**
 * 主题管理系统 - 企业级主题解决方案
 * 
 * 这是一个功能完整的主题管理系统，为 EasyTiWu 前端应用提供：
 * 
 * ## 核心功能
 * - 🎨 **多主题支持**: 明亮主题、暗色主题、自动跟随系统
 * - 💾 **持久化存储**: 用户主题偏好自动保存到本地存储
 * - 🔄 **响应式切换**: 实时主题切换，无需刷新页面
 * - 🎯 **系统集成**: 自动检测并跟随系统主题偏好
 * - 🧩 **组件集成**: 完整的 Naive UI 主题配置和覆盖
 * - 📱 **跨组件通信**: 基于 provide/inject 的主题状态共享
 * 
 * ## 设计理念
 * - **一致性**: 确保整个应用的视觉一致性
 * - **可访问性**: 支持用户的视觉偏好和无障碍需求
 * - **性能优化**: 最小化主题切换的性能开销
 * - **开发友好**: 简单易用的 API 设计
 * 
 * ## 使用场景
 * - 企业级应用的主题管理
 * - 需要支持明暗主题的现代 Web 应用
 * - 重视用户体验和可访问性的项目
 * 
 * @author EasyTiWu Team
 * @version 1.0.0
 * @since 2024
 * 
 * @example
 * ```typescript
 * // 基础使用
 * import { useTheme } from '@/composables/useTheme'
 * 
 * const { 
 *   actualTheme, 
 *   setTheme, 
 *   toggleTheme, 
 *   naiveTheme, 
 *   themeOverrides 
 * } = useTheme()
 * 
 * // 切换主题
 * setTheme('dark')
 * toggleTheme()
 * 
 * // 在模板中使用
 * <n-config-provider :theme="naiveTheme" :theme-overrides="themeOverrides">
 *   <YourApp />
 * </n-config-provider>
 * ```
 */

import {computed, inject, onMounted, provide, readonly, ref, type Ref, watch,} from "vue";
import {darkTheme, type GlobalThemeOverrides} from "naive-ui";

/**
 * 主题模式类型定义
 * 
 * @typedef {('light'|'dark'|'auto')} ThemeMode
 * - `light`: 明亮主题
 * - `dark`: 暗色主题  
 * - `auto`: 自动跟随系统主题
 */
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

  /**
   * Naive UI 主题覆盖配置
   * 
   * 根据当前主题模式动态生成主题覆盖配置，确保在明暗主题间
   * 保持一致的视觉体验和品牌色彩。
   * 
   * 配置包括：
   * - 主色调系统（Primary Colors）
   * - 功能色彩（Success/Warning/Error/Info）
   * - 文本颜色层级（Text Colors）
   * - 背景色系统（Background Colors）
   * - 边框和分割线（Border & Divider）
   * - 交互效果（Hover & Pressed）
   * - 表单控件（Input Controls）
   * - 阴影系统（Box Shadows）
   */
  const themeOverrides = computed<GlobalThemeOverrides>(() => {
    const isDark = actualTheme.value === "dark";
    
    const baseOverrides: GlobalThemeOverrides = {
      common: {
        // ==================== 主色调系统 ====================
        // 基于设计系统的主色调，保持品牌一致性
        primaryColor: "#2080f0",          // 主色调
        primaryColorHover: "#3090ff",     // 悬停状态
        primaryColorPressed: "#1070e0",   // 按下状态
        primaryColorSuppl: "#80c2ff",     // 补充色

        // ==================== 功能色彩 ====================
        // 语义化颜色，传达不同的状态和意图
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

        // ==================== 文本颜色层级 ====================
        // 基于设计令牌的文本颜色系统，确保可读性
        textColorBase: isDark ? "#f5f5f5" : "#2c2c2c",      // 基础文本色
        textColor1: isDark ? "#f5f5f5" : "#2c2c2c",         // 主要文本色
        textColor2: isDark ? "#d1d5db" : "#666666",         // 次要文本色
        textColor3: isDark ? "#9ca3af" : "#999999",         // 辅助文本色
        textColorDisabled: isDark ? "#6b7280" : "#cccccc", // 禁用文本色

        // ==================== 背景色系统 ====================
        // 分层的背景色系统，营造层次感
        bodyColor: isDark ? "#1e1e1e" : "#ffffff",          // 页面背景色
        cardColor: isDark ? "#262626" : "#ffffff",          // 卡片背景色
        modalColor: isDark ? "#262626" : "#ffffff",         // 模态框背景色
        popoverColor: isDark ? "#262626" : "#ffffff",       // 弹出层背景色
        tableHeaderColor: isDark ? "#1f1f1f" : "#fafafa",  // 表格头部背景色
        
        // ==================== 边框和分割线 ====================
        // 用于分隔和定义界面元素边界
        borderColor: isDark ? "#404040" : "#d9d9d9",        // 边框色
        dividerColor: isDark ? "#404040" : "#e5e5e5",       // 分割线色
        
        // ==================== 交互效果 ====================
        // 提供视觉反馈的交互状态颜色
        hoverColor: isDark ? "rgba(255, 255, 255, 0.05)" : "rgba(32, 128, 240, 0.1)",   // 悬停效果
        pressedColor: isDark ? "rgba(255, 255, 255, 0.1)" : "rgba(32, 128, 240, 0.15)", // 按下效果
        
        // ==================== 表单控件 ====================
        // 输入框和表单元素的颜色配置
        inputColor: isDark ? "#262626" : "#ffffff",         // 输入框背景色
        inputColorDisabled: isDark ? "#1a1a1a" : "#f5f5f5", // 禁用输入框背景色
        
        // ==================== 阴影系统 ====================
        // 三级阴影系统，营造深度和层次
        boxShadow1: isDark 
          ? "0 1px 2px -2px rgba(0, 0, 0, 0.8), 0 3px 6px 0 rgba(0, 0, 0, 0.6), 0 5px 12px 4px rgba(0, 0, 0, 0.4)"   // 暗色模式一级阴影
          : "0 1px 2px -2px rgba(0, 0, 0, 0.08), 0 3px 6px 0 rgba(0, 0, 0, 0.06), 0 5px 12px 4px rgba(0, 0, 0, 0.04)", // 亮色模式一级阴影
        boxShadow2: isDark
          ? "0 3px 6px -4px rgba(0, 0, 0, 0.8), 0 6px 16px 0 rgba(0, 0, 0, 0.6), 0 9px 28px 8px rgba(0, 0, 0, 0.4)"   // 暗色模式二级阴影
          : "0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 9px 28px 8px rgba(0, 0, 0, 0.05)", // 亮色模式二级阴影
        boxShadow3: isDark
          ? "0 6px 16px -9px rgba(0, 0, 0, 0.8), 0 9px 28px 0 rgba(0, 0, 0, 0.6), 0 12px 48px 16px rgba(0, 0, 0, 0.4)"   // 暗色模式三级阴影
          : "0 6px 16px -9px rgba(0, 0, 0, 0.08), 0 9px 28px 0 rgba(0, 0, 0, 0.05), 0 12px 48px 16px rgba(0, 0, 0, 0.03)", // 亮色模式三级阴影
      },
      
      // ==================== 组件特定覆盖 ====================
      
      /**
       * 按钮组件样式覆盖
       * 统一按钮的圆角规范，营造现代化的视觉效果
       */
      Button: {
        borderRadiusTiny: "4px",    // 超小按钮圆角
        borderRadiusSmall: "6px",   // 小按钮圆角
        borderRadiusMedium: "8px",  // 中等按钮圆角
        borderRadiusLarge: "10px",  // 大按钮圆角
      },
      
      /**
       * 卡片组件样式覆盖
       * 增强卡片的视觉层次和内容间距
       */
      Card: {
        borderRadius: "12px",       // 卡片圆角，营造柔和感
        paddingMedium: "20px",      // 中等内边距
        paddingLarge: "24px",       // 大内边距
      },
      
      /**
       * 输入框组件样式覆盖
       * 优化输入框的视觉表现和用户体验
       */
      Input: {
        borderRadius: "8px",        // 输入框圆角
        heightMedium: "40px",       // 中等高度，提升点击体验
        heightLarge: "48px",        // 大高度，适合重要表单
      },
    };

    return baseOverrides;
  });

  // ==================== 系统主题监听 ====================
  
  /**
   * 监听系统主题变化
   * 
   * 当用户选择"auto"模式时，自动跟随系统主题变化。
   * 使用 matchMedia API 监听系统主题偏好设置的变化。
   */
  const updateSystemTheme = () => {
    systemPrefersDark.value = window.matchMedia(
      "(prefers-color-scheme: dark)"
    ).matches;
  };

  // ==================== 主题操作方法 ====================
  
  /**
   * 设置全局主题
   * 
   * 更新当前主题设置，并将选择持久化到本地存储。
   * 同时应用主题到文档元素，确保界面立即响应主题变化。
   * 
   * @param {ThemeMode} theme - 要设置的主题模式
   * 
   * @example
   * ```typescript
   * // 切换到暗色主题
   * setTheme('dark')
   * 
   * // 切换到亮色主题
   * setTheme('light')
   * 
   * // 跟随系统主题
   * setTheme('auto')
   * ```
   */
  const setTheme = (theme: ThemeMode) => {
    currentTheme.value = theme;
    localStorage.setItem("app-theme", theme);
    applyThemeToDocument();
  };

  /**
   * 获取当前主题模式
   * 
   * 返回用户当前选择的主题模式，而不是实际应用的主题。
   * 例如，当选择"auto"时，返回"auto"而不是"light"或"dark"。
   * 
   * @returns {ThemeMode} 当前主题模式
   * 
   * @example
   * ```typescript
   * const currentMode = getTheme()
   * console.log(currentMode) // 'light' | 'dark' | 'auto'
   * ```
   */
  const getTheme = () => {
    return currentTheme.value;
  };

  /**
   * 应用主题到文档
   * 
   * 将主题设置应用到DOM文档，包括：
   * 1. 设置data-theme属性供CSS选择器使用
   * 2. 添加主题类名到body元素
   * 3. 更新CSS自定义属性（CSS变量）
   * 
   * 这种多层次的主题应用确保了最大的兼容性和灵活性。
   * 
   * @private
   * @internal
   */
  const applyThemeToDocument = () => {
    const theme = actualTheme.value;
    
    // 设置文档根元素的主题属性
    document.documentElement.setAttribute("data-theme", theme);
    
    // 为body添加主题类名，便于CSS选择器使用
    document.body.className = document.body.className.replace(/theme-\w+/g, '');
    document.body.classList.add(`theme-${theme}`);

    // 为文档根元素添加/移除dark类，确保CSS变量正确应用
    const root = document.documentElement;
    if (theme === "dark") {
      root.classList.add("dark");
      // ==================== 深色主题CSS变量 ====================
      root.style.setProperty("--app-bg-color", "#1e1e1e");              // 应用背景色
      root.style.setProperty("--app-text-color", "#f5f5f5");            // 主要文本色
      root.style.setProperty("--app-border-color", "#404040");          // 边框色
      root.style.setProperty("--app-card-bg", "#262626");               // 卡片背景色
      root.style.setProperty("--app-hover-color", "rgba(255, 255, 255, 0.05)"); // 悬停效果色
    } else {
      root.classList.remove("dark");
      // ==================== 浅色主题CSS变量 ====================
      root.style.setProperty("--app-bg-color", "#ffffff");              // 应用背景色
      root.style.setProperty("--app-text-color", "#2c2c2c");            // 主要文本色
      root.style.setProperty("--app-border-color", "#d9d9d9");          // 边框色
      root.style.setProperty("--app-card-bg", "#ffffff");               // 卡片背景色
      root.style.setProperty("--app-hover-color", "rgba(32, 128, 240, 0.1)");
    }
  };

  /**
   * 切换主题
   * 
   * 在明暗主题之间快速切换，不支持"auto"模式。
   * 适合用于主题切换按钮的点击事件。
   * 
   * @example
   * ```typescript
   * // 在组件中使用
   * const { toggleTheme } = useTheme()
   * 
   * // 绑定到按钮点击事件
   * <button @click="toggleTheme">切换主题</button>
   * ```
   */
  const toggleTheme = () => {
    const newTheme = actualTheme.value === "dark" ? "light" : "dark";
    setTheme(newTheme);
  };

  /**
   * 初始化主题系统
   * 
   * 执行主题系统的完整初始化流程：
   * 1. 从本地存储恢复用户的主题偏好
   * 2. 检测系统主题偏好
   * 3. 设置系统主题变化监听器
   * 4. 应用初始主题到文档
   * 
   * @returns {Function} 清理函数，用于移除事件监听器
   * 
   * @private
   * @internal
   */
  const initTheme = () => {
    // 从localStorage读取保存的主题偏好，默认为浅色主题
    const savedTheme =
      (localStorage.getItem("app-theme") as ThemeMode) || "light";
    currentTheme.value = savedTheme;

    // 初始化系统主题检测
    updateSystemTheme();

    // 监听系统主题变化（用于auto模式）
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    mediaQuery.addEventListener("change", updateSystemTheme);

    // 应用初始主题到文档
    applyThemeToDocument();

    // 返回清理函数，用于组件卸载时移除监听器
    return () => {
      mediaQuery.removeEventListener("change", updateSystemTheme);
    };
  };

  // ==================== 响应式监听 ====================
  
  /**
   * 监听实际主题变化
   * 
   * 当实际应用的主题发生变化时（包括auto模式下的系统主题变化），
   * 自动应用新主题到文档。
   */
  watch(actualTheme, () => {
    applyThemeToDocument();
  });

  // ==================== 生命周期管理 ====================
  
  /**
   * 组件挂载时初始化主题系统
   * 
   * 确保在组件挂载后立即初始化主题，
   * 避免主题闪烁和不一致的问题。
   */
  onMounted(() => {
    initTheme();
  });

  // ==================== 返回值 ====================
  
  return {
    // ==================== 响应式状态 ====================
    /** 用户选择的主题模式（可能是'auto'） */
    currentTheme: readonly(currentTheme),
    /** 实际应用的主题（'light'或'dark'） */
    actualTheme: readonly(actualTheme),
    /** 系统是否偏好暗色主题 */
    systemPrefersDark: readonly(systemPrefersDark),

    // ==================== Naive UI 配置 ====================
    /** Naive UI 主题对象 */
    naiveTheme,
    /** Naive UI 主题覆盖配置 */
    themeOverrides,

    // ==================== 操作方法 ====================
    /** 设置主题模式 */
    setTheme,
    /** 获取当前主题模式 */
    getTheme,
    /** 切换明暗主题 */
    toggleTheme,
    /** 初始化主题系统（内部使用） */
    initTheme,
  };
}

/**
 * 主题提供者 Hook
 * 
 * 用于在应用根组件中提供主题功能，通过 Vue 的 provide/inject 机制
 * 向整个组件树提供主题管理能力。
 * 
 * 适用场景：
 * - 应用的根组件或布局组件
 * - 需要向子组件提供主题功能的父组件
 * 
 * @returns {Object} 包含主题状态、配置和提供方法的对象
 * 
 * @example
 * ```typescript
 * // 在App.vue或布局组件中使用
 * import { useThemeProvider } from '@/composables/useTheme'
 * 
 * export default {
 *   setup() {
 *     const { provideTheme, naiveTheme, themeOverrides } = useThemeProvider()
 *     
 *     // 提供主题功能给子组件
 *     provideTheme()
 *     
 *     return {
 *       naiveTheme,
 *       themeOverrides
 *     }
 *   }
 * }
 * ```
 */
export function useThemeProvider() {
  const theme = useTheme();

  /**
   * 向子组件提供主题功能
   * 
   * 通过 Vue 的 provide API 向组件树提供主题管理方法和状态，
   * 使得任何子组件都可以通过 useThemeInject 访问主题功能。
   * 
   * @example
   * ```typescript
   * // 在setup函数中调用
   * provideTheme()
   * ```
   */
  const provideTheme = () => {
    provide("setGlobalTheme", theme.setTheme);     // 提供主题设置方法
    provide("getGlobalTheme", theme.getTheme);     // 提供主题获取方法
    provide("toggleTheme", theme.toggleTheme);     // 提供主题切换方法
    provide("currentTheme", theme.currentTheme);   // 提供当前主题状态
    provide("actualTheme", theme.actualTheme);     // 提供实际主题状态
  };

  return {
    ...theme,
    /** 向子组件提供主题功能的方法 */
    provideTheme,
  };
}

/**
 * 主题注入 Hook
 * 
 * 用于在子组件中注入并使用父组件提供的主题功能。
 * 必须与 useThemeProvider 配合使用。
 * 
 * 适用场景：
 * - 需要访问主题功能的子组件
 * - 主题切换按钮组件
 * - 需要根据主题状态调整样式的组件
 * 
 * @returns {Object} 注入的主题方法和状态
 * 
 * @example
 * ```typescript
 * // 在子组件中使用
 * import { useThemeInject } from '@/composables/useTheme'
 * 
 * export default {
 *   setup() {
 *     const { 
 *       setGlobalTheme, 
 *       toggleTheme, 
 *       currentTheme, 
 *       actualTheme 
 *     } = useThemeInject()
 *     
 *     // 切换到暗色主题
 *     const switchToDark = () => setGlobalTheme?.('dark')
 *     
 *     return {
 *       toggleTheme,
 *       currentTheme,
 *       actualTheme,
 *       switchToDark
 *     }
 *   }
 * }
 * ```
 */
export function useThemeInject() {
  const setGlobalTheme = inject<(theme: ThemeMode) => void>("setGlobalTheme");
  const getGlobalTheme = inject<() => ThemeMode>("getGlobalTheme");
  const toggleTheme = inject<() => void>("toggleTheme");
  const currentTheme = inject<Ref<ThemeMode>>("currentTheme");
  const actualTheme = inject<Ref<"light" | "dark">>("actualTheme");

  return {
    /** 设置全局主题的方法（可能为undefined） */
    setGlobalTheme,
    /** 获取全局主题的方法（可能为undefined） */
    getGlobalTheme,
    /** 切换主题的方法（可能为undefined） */
    toggleTheme,
    /** 当前主题状态（可能为undefined） */
    currentTheme,
    /** 实际主题状态（可能为undefined） */
    actualTheme,
  };
}
