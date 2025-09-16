/**
 * 响应式断点配置
 * 基于 Naive UI 设计规范和移动优先的设计原则
 */

// 断点定义（像素值）
export const BREAKPOINTS = {
  xs: 0, // 超小屏幕（手机竖屏）
  sm: 640, // 小屏幕（手机横屏）
  md: 768, // 中等屏幕（平板竖屏）
  lg: 1024, // 大屏幕（平板横屏/小桌面）
  xl: 1280, // 超大屏幕（桌面）
  xxl: 1536, // 超超大屏幕（大桌面）
} as const;

// 断点类型定义
export type BreakpointKey = keyof typeof BREAKPOINTS;

// 媒体查询字符串生成
export const createMediaQuery = (
  breakpoint: BreakpointKey,
  type: "min" | "max" = "min"
): string => {
  const value = BREAKPOINTS[breakpoint];
  return `(${type}-width: ${value}px)`;
};

// 常用媒体查询
export const MEDIA_QUERIES = {
  mobile: createMediaQuery("sm", "max"), // 移动端：< 640px
  tablet: createMediaQuery("md"), // 平板及以上：>= 768px
  desktop: createMediaQuery("lg"), // 桌面及以上：>= 1024px
  largeDesktop: createMediaQuery("xl"), // 大桌面及以上：>= 1280px

  // 范围查询
  mobileOnly: `(max-width: ${BREAKPOINTS.sm - 1}px)`,
  tabletOnly: `(min-width: ${BREAKPOINTS.md}px) and (max-width: ${
    BREAKPOINTS.lg - 1
  }px)`,
  desktopOnly: `(min-width: ${BREAKPOINTS.lg}px)`,
} as const;

// 栅格系统配置
export const GRID_CONFIG = {
  // 每个断点的列数
  columns: {
    xs: 4,
    sm: 8,
    md: 12,
    lg: 16,
    xl: 20,
    xxl: 24,
  },

  // 间距配置（基于 8px 基础单位）
  gutters: {
    xs: 8, // 1 * 8px
    sm: 12, // 1.5 * 8px
    md: 16, // 2 * 8px
    lg: 20, // 2.5 * 8px
    xl: 24, // 3 * 8px
    xxl: 32, // 4 * 8px
  },
} as const;

// 容器最大宽度
export const CONTAINER_MAX_WIDTHS = {
  sm: 540,
  md: 720,
  lg: 960,
  xl: 1140,
  xxl: 1320,
} as const;

// 导出默认配置
export default {
  breakpoints: BREAKPOINTS,
  mediaQueries: MEDIA_QUERIES,
  gridConfig: GRID_CONFIG,
  containerMaxWidths: CONTAINER_MAX_WIDTHS,
};
