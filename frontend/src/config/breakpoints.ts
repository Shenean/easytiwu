/**
 * 响应式断点配置模块
 * 
 * 本模块定义了整个应用的响应式断点系统，基于现代Web设计最佳实践：
 * 
 * ## 设计原则
 * - **移动优先**: 从最小屏幕开始设计，逐步增强
 * - **内容驱动**: 断点基于内容需求而非设备类型
 * - **一致性**: 与CSS媒体查询和组件库保持一致
 * - **可扩展性**: 支持未来新设备尺寸的扩展
 * 
 * ## 断点体系
 * 采用6级断点系统，覆盖从手机到大屏显示器的所有场景：
 * - xs (0px+): 超小屏幕，主要是手机竖屏
 * - sm (640px+): 小屏幕，手机横屏和小平板
 * - md (768px+): 中等屏幕，平板竖屏
 * - lg (1024px+): 大屏幕，平板横屏和小桌面
 * - xl (1280px+): 超大屏幕，标准桌面显示器
 * - xxl (1536px+): 超超大屏幕，大桌面和4K显示器
 * 
 * @module BreakpointsConfig
 * @author EasyTiWu Team
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * import { BREAKPOINTS, createMediaQuery, MEDIA_QUERIES } from '@/config/breakpoints'
 * 
 * // 使用断点值
 * const isMobile = window.innerWidth < BREAKPOINTS.md
 * 
 * // 生成媒体查询
 * const tabletQuery = createMediaQuery('md')
 * 
 * // 使用预定义媒体查询
 * const mq = window.matchMedia(MEDIA_QUERIES.mobile)
 * ```
 */

/**
 * 响应式断点定义
 * 
 * 定义了应用中使用的所有断点值（以像素为单位）。
 * 这些值与CSS媒体查询和组件库的断点保持一致，确保设计系统的统一性。
 * 
 * 断点选择基于：
 * - 常见设备屏幕尺寸的统计数据
 * - 内容布局的自然断点
 * - 设计系统的最佳实践
 * 
 * @constant {Object} BREAKPOINTS
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 检查屏幕尺寸
 * if (window.innerWidth >= BREAKPOINTS.lg) {
 *   // 桌面布局逻辑
 * }
 * 
 * // 在CSS-in-JS中使用
 * const styles = {
 *   [`@media (min-width: ${BREAKPOINTS.md}px)`]: {
 *     display: 'flex'
 *   }
 * }
 * ```
 */
export const BREAKPOINTS = {
  /** 超小屏幕起始点 (0px+) - 手机竖屏，最小支持尺寸 */
  xs: 0,
  /** 小屏幕起始点 (640px+) - 手机横屏，大手机竖屏 */
  sm: 640,
  /** 中等屏幕起始点 (768px+) - 平板竖屏，小平板设备 */
  md: 768,
  /** 大屏幕起始点 (1024px+) - 平板横屏，小桌面显示器 */
  lg: 1024,
  /** 超大屏幕起始点 (1280px+) - 标准桌面显示器 */
  xl: 1280,
  /** 超超大屏幕起始点 (1536px+) - 大桌面显示器，4K屏幕 */
  xxl: 1536,
} as const;

/**
 * 断点键名类型定义
 * 
 * 提供断点名称的类型约束，确保类型安全。
 * 用于函数参数、组件属性等需要断点名称的场景。
 * 
 * @typedef {('xs'|'sm'|'md'|'lg'|'xl'|'xxl')} BreakpointKey
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * function getColumnsForBreakpoint(bp: BreakpointKey): number {
 *   return GRID_CONFIG.columns[bp]
 * }
 * ```
 */
export type BreakpointKey = keyof typeof BREAKPOINTS;

/**
 * 媒体查询字符串生成器
 * 
 * 根据断点名称和查询类型生成标准的CSS媒体查询字符串。
 * 支持最小宽度（min-width）和最大宽度（max-width）查询。
 * 
 * @param {BreakpointKey} breakpoint - 断点名称
 * @param {'min'|'max'} type - 查询类型，默认为'min'
 * @returns {string} CSS媒体查询字符串
 * 
 * @example
 * ```typescript
 * // 生成最小宽度查询
 * const minQuery = createMediaQuery('md') // "(min-width: 768px)"
 * 
 * // 生成最大宽度查询
 * const maxQuery = createMediaQuery('lg', 'max') // "(max-width: 1024px)"
 * 
 * // 在JavaScript中使用
 * const mediaQuery = window.matchMedia(createMediaQuery('md'))
 * if (mediaQuery.matches) {
 *   // 中等屏幕及以上的逻辑
 * }
 * ```
 */
export const createMediaQuery = (
  breakpoint: BreakpointKey,
  type: "min" | "max" = "min"
): string => {
  const value = BREAKPOINTS[breakpoint];
  return `(${type}-width: ${value}px)`;
};

/**
 * 预定义媒体查询集合
 * 
 * 提供常用的媒体查询字符串，简化响应式设计的实现。
 * 包括设备类型查询和精确范围查询，覆盖大部分使用场景。
 * 
 * @constant {Object} MEDIA_QUERIES
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 检测移动设备
 * const isMobile = window.matchMedia(MEDIA_QUERIES.mobile).matches
 * 
 * // 监听屏幕尺寸变化
 * const mq = window.matchMedia(MEDIA_QUERIES.desktop)
 * mq.addEventListener('change', (e) => {
 *   if (e.matches) {
 *     // 切换到桌面布局
 *   }
 * })
 * 
 * // 在CSS-in-JS中使用
 * const styles = {
 *   [MEDIA_QUERIES.tabletOnly]: {
 *     gridTemplateColumns: 'repeat(2, 1fr)'
 *   }
 * }
 * ```
 */
export const MEDIA_QUERIES = {
  /** 移动端设备查询 (< 640px) - 包括所有手机设备 */
  mobile: createMediaQuery("sm", "max"),
  /** 平板及以上设备查询 (>= 768px) - 平板和桌面设备 */
  tablet: createMediaQuery("md"),
  /** 桌面及以上设备查询 (>= 1024px) - 桌面显示器 */
  desktop: createMediaQuery("lg"),
  /** 大桌面及以上设备查询 (>= 1280px) - 大屏显示器 */
  largeDesktop: createMediaQuery("xl"),

  // ==================== 精确范围查询 ====================
  /** 仅移动端查询 (< 640px) - 只匹配手机设备 */
  mobileOnly: `(max-width: ${BREAKPOINTS.sm - 1}px)`,
  /** 仅平板查询 (768px - 1023px) - 只匹配平板设备 */
  tabletOnly: `(min-width: ${BREAKPOINTS.md}px) and (max-width: ${
    BREAKPOINTS.lg - 1
  }px)`,
  /** 仅桌面查询 (>= 1024px) - 只匹配桌面设备 */
  desktopOnly: `(min-width: ${BREAKPOINTS.lg}px)`,
} as const;

/**
 * 栅格系统配置
 * 
 * 定义了响应式栅格系统的核心参数，包括每个断点的列数和间距。
 * 基于12列栅格系统的扩展，提供更灵活的布局选项。
 * 
 * @constant {Object} GRID_CONFIG
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 获取当前断点的列数
 * const currentBreakpoint = getCurrentBreakpoint()
 * const columns = GRID_CONFIG.columns[currentBreakpoint]
 * 
 * // 计算响应式网格
 * const gridCols = computed(() => {
 *   if (isMobile.value) return GRID_CONFIG.columns.xs
 *   if (isTablet.value) return GRID_CONFIG.columns.md
 *   return GRID_CONFIG.columns.lg
 * })
 * ```
 */
export const GRID_CONFIG = {
  /**
   * 每个断点的栅格列数配置
   * 
   * 采用递增的列数设计，随着屏幕尺寸增大提供更多布局选项：
   * - 移动端使用较少列数，确保内容可读性
   * - 桌面端使用更多列数，充分利用屏幕空间
   */
  columns: {
    /** 超小屏幕列数 (4列) - 适合简单的移动端布局 */
    xs: 4,
    /** 小屏幕列数 (8列) - 适合复杂的移动端布局 */
    sm: 8,
    /** 中等屏幕列数 (12列) - 经典的12列栅格系统 */
    md: 12,
    /** 大屏幕列数 (16列) - 适合复杂的桌面端布局 */
    lg: 16,
    /** 超大屏幕列数 (20列) - 适合复杂的大屏幕布局 */
    xl: 20,
    /** 超超大屏幕列数 (24列) - 适合4K显示器的超复杂布局 */
    xxl: 24,
  },

  /**
   * 栅格间距配置（像素值）
   * 
   * 定义了不同断点下栅格列之间的间距。
   * 基于8px设计令牌系统，确保间距的一致性和视觉和谐。
   * 
   * 间距设计原则：
   * - 移动端使用较小间距，节省屏幕空间
   * - 桌面端使用较大间距，提升视觉层次
   * - 所有值都是8的倍数，符合设计系统规范
   */
  gutters: {
    /** 超小屏幕间距 (8px) - 1倍基础间距，适合紧凑布局 */
    xs: 8,
    /** 小屏幕间距 (12px) - 1.5倍基础间距，平衡空间利用 */
    sm: 12,
    /** 中等屏幕间距 (16px) - 2倍基础间距，标准间距 */
    md: 16,
    /** 大屏幕间距 (20px) - 2.5倍基础间距，舒适阅读 */
    lg: 20,
    /** 超大屏幕间距 (24px) - 3倍基础间距，宽松布局 */
    xl: 24,
    /** 超超大屏幕间距 (32px) - 4倍基础间距，豪华间距 */
    xxl: 32,
  },
} as const;

/**
 * 容器最大宽度配置
 * 
 * 定义了不同断点下容器的最大宽度，防止内容在超大屏幕上过度拉伸。
 * 这些值确保了内容的可读性和视觉平衡，特别是在大屏显示器上。
 * 
 * 设计考虑：
 * - 基于最佳阅读宽度的研究
 * - 考虑不同内容类型的布局需求
 * - 与栅格系统和断点保持协调
 * 
 * @constant {Object} CONTAINER_MAX_WIDTHS
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 在CSS-in-JS中使用
 * const containerStyles = {
 *   maxWidth: CONTAINER_MAX_WIDTHS.lg,
 *   margin: '0 auto',
 *   padding: '0 16px'
 * }
 * 
 * // 响应式容器宽度
 * const getContainerWidth = (breakpoint: BreakpointKey) => {
 *   return CONTAINER_MAX_WIDTHS[breakpoint] || '100%'
 * }
 * ```
 */
export const CONTAINER_MAX_WIDTHS = {
  /** 小屏幕容器最大宽度 (540px) - 适合手机横屏内容 */
  sm: 540,
  /** 中等屏幕容器最大宽度 (720px) - 适合平板竖屏内容 */
  md: 720,
  /** 大屏幕容器最大宽度 (960px) - 适合小桌面显示器 */
  lg: 960,
  /** 超大屏幕容器最大宽度 (1140px) - 适合标准桌面显示器 */
  xl: 1140,
  /** 超超大屏幕容器最大宽度 (1320px) - 适合大桌面显示器 */
  xxl: 1320,
} as const;

/**
 * 断点配置的默认导出
 * 
 * 将所有断点相关的配置集中导出，便于统一管理和使用。
 * 适合需要一次性导入所有配置的场景。
 * 
 * @default
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * import breakpointsConfig from '@/config/breakpoints'
 * 
 * const { breakpoints, mediaQueries, gridConfig } = breakpointsConfig
 * 
 * // 或者解构使用
 * import { breakpoints, mediaQueries } from '@/config/breakpoints'
 * ```
 */
export default {
  /** 断点定义对象 */
  breakpoints: BREAKPOINTS,
  /** 媒体查询字符串集合 */
  mediaQueries: MEDIA_QUERIES,
  /** 栅格系统配置 */
  gridConfig: GRID_CONFIG,
  /** 容器最大宽度配置 */
  containerMaxWidths: CONTAINER_MAX_WIDTHS,
};
