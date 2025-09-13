/**
 * 统一栅格系统配置
 * 基于 Naive UI 的 n-grid 组件和自定义 ResponsiveGrid 组件
 */

// 响应式断点定义
export const BREAKPOINTS = {
  xs: 0,     // 超小屏幕 (手机竖屏)
  sm: 576,   // 小屏幕 (手机横屏)
  md: 768,   // 中等屏幕 (平板)
  lg: 992,   // 大屏幕 (桌面)
  xl: 1200,  // 超大屏幕 (大桌面)
  xxl: 1600  // 超超大屏幕
} as const

export type Breakpoint = keyof typeof BREAKPOINTS

// 响应式配置类型
export interface ResponsiveConfig<T> {
  xs?: T
  sm?: T
  md?: T
  lg?: T
  xl?: T
  xxl?: T
}

// 标准栅格配置
export const STANDARD_GRID_CONFIG = {
  cols: { xs: 1, sm: 2, md: 3, lg: 4, xl: 5 } as ResponsiveConfig<number>,
  gap: { xs: 8, sm: 12, md: 16, lg: 20, xl: 24 } as ResponsiveConfig<number>
}

// 紧凑栅格配置（用于较小的卡片或列表项）
export const COMPACT_GRID_CONFIG = {
  cols: { xs: 2, sm: 3, md: 4, lg: 5, xl: 6 } as ResponsiveConfig<number>,
  gap: { xs: 4, sm: 8, md: 12, lg: 16, xl: 20 } as ResponsiveConfig<number>
}

// 宽松栅格配置（用于大卡片或重要内容）
export const LOOSE_GRID_CONFIG = {
  cols: { xs: 1, sm: 1, md: 2, lg: 3, xl: 4 } as ResponsiveConfig<number>,
  gap: { xs: 12, sm: 16, md: 20, lg: 24, xl: 32 } as ResponsiveConfig<number>
}

// 统计卡片栅格配置
export const STATS_GRID_CONFIG = {
  cols: { xs: 1, sm: 2, md: 2, lg: 3, xl: 4 } as ResponsiveConfig<number>,
  gap: { xs: 8, sm: 12, md: 16, lg: 16, xl: 20 } as ResponsiveConfig<number>
}

// 答题卡栅格配置
export const ANSWER_GRID_CONFIG = {
  cols: { xs: 4, sm: 5, md: 6, lg: 8, xl: 10 } as ResponsiveConfig<number>,
  gap: { xs: 6, sm: 8, md: 10, lg: 12, xl: 16 } as ResponsiveConfig<number>
}

// 题库卡片栅格配置
export const BANK_GRID_CONFIG = {
  cols: { xs: 1, sm: 2, md: 2, lg: 3, xl: 4 } as ResponsiveConfig<number>,
  gap: { xs: 12, sm: 16, md: 16, lg: 20, xl: 24 } as ResponsiveConfig<number>
}

// 导航菜单栅格配置
export const MENU_GRID_CONFIG = {
  cols: { xs: 2, sm: 4, md: 4, lg: 4, xl: 4 } as ResponsiveConfig<number>,
  gap: { xs: 8, sm: 12, md: 16, lg: 16, xl: 16 } as ResponsiveConfig<number>
}

// 表单栅格配置
export const FORM_GRID_CONFIG = {
  cols: { xs: 1, sm: 1, md: 2, lg: 2, xl: 3 } as ResponsiveConfig<number>,
  gap: { xs: 16, sm: 16, md: 20, lg: 24, xl: 24 } as ResponsiveConfig<number>
}

// 工具函数：获取当前断点
export function getCurrentBreakpoint(): Breakpoint {
  const width = window.innerWidth
  
  if (width >= BREAKPOINTS.xxl) return 'xxl'
  if (width >= BREAKPOINTS.xl) return 'xl'
  if (width >= BREAKPOINTS.lg) return 'lg'
  if (width >= BREAKPOINTS.md) return 'md'
  if (width >= BREAKPOINTS.sm) return 'sm'
  return 'xs'
}

// 工具函数：获取响应式配置值
export function getResponsiveValue<T>(
  config: ResponsiveConfig<T>,
  breakpoint?: Breakpoint
): T | undefined {
  const currentBreakpoint = breakpoint || getCurrentBreakpoint()
  
  // 按优先级查找配置值
  const breakpoints: Breakpoint[] = ['xxl', 'xl', 'lg', 'md', 'sm', 'xs']
  const currentIndex = breakpoints.indexOf(currentBreakpoint)
  
  // 从当前断点开始，向下查找第一个有值的配置
  for (let i = currentIndex; i < breakpoints.length; i++) {
    const bp = breakpoints[i]
    if (config[bp] !== undefined) {
      return config[bp]
    }
  }
  
  return undefined
}

// 工具函数：创建响应式列数配置
export function createResponsiveCols(
  xs = 1, sm = 2, md = 3, lg = 4, xl = 5, xxl = 6
): ResponsiveConfig<number> {
  return { xs, sm, md, lg, xl, xxl }
}

// 工具函数：创建响应式间距配置
export function createResponsiveGap(
  xs = 8, sm = 12, md = 16, lg = 20, xl = 24, xxl = 32
): ResponsiveConfig<number> {
  return { xs, sm, md, lg, xl, xxl }
}

// 工具函数：判断是否为移动端
export function isMobile(): boolean {
  return window.innerWidth < BREAKPOINTS.md
}

// 工具函数：判断是否为平板端
export function isTablet(): boolean {
  return window.innerWidth >= BREAKPOINTS.md && window.innerWidth < BREAKPOINTS.lg
}

// 工具函数：判断是否为桌面端
export function isDesktop(): boolean {
  return window.innerWidth >= BREAKPOINTS.lg
}

// 工具函数：获取设备类型
export function getDeviceType(): 'mobile' | 'tablet' | 'desktop' {
  if (isMobile()) return 'mobile'
  if (isTablet()) return 'tablet'
  return 'desktop'
}

// 预定义的栅格配置映射
export const GRID_CONFIGS = {
  standard: STANDARD_GRID_CONFIG,
  compact: COMPACT_GRID_CONFIG,
  loose: LOOSE_GRID_CONFIG,
  stats: STATS_GRID_CONFIG,
  answer: ANSWER_GRID_CONFIG,
  bank: BANK_GRID_CONFIG,
  menu: MENU_GRID_CONFIG,
  form: FORM_GRID_CONFIG
} as const

export type GridConfigType = keyof typeof GRID_CONFIGS

// 工具函数：获取预定义栅格配置
export function getGridConfig(type: GridConfigType) {
  return GRID_CONFIGS[type]
}

// 媒体查询字符串生成器
export function createMediaQuery(breakpoint: Breakpoint): string {
  const minWidth = BREAKPOINTS[breakpoint]
  return `(min-width: ${minWidth}px)`
}

// CSS 类名生成器
export function createGridClasses(prefix = 'grid'): Record<string, string> {
  return {
    container: `${prefix}-container`,
    item: `${prefix}-item`,
    responsive: `${prefix}-responsive`,
    mobile: `${prefix}-mobile`,
    tablet: `${prefix}-tablet`,
    desktop: `${prefix}-desktop`
  }
}

// 默认导出
export default {
  BREAKPOINTS,
  STANDARD_GRID_CONFIG,
  COMPACT_GRID_CONFIG,
  LOOSE_GRID_CONFIG,
  STATS_GRID_CONFIG,
  ANSWER_GRID_CONFIG,
  BANK_GRID_CONFIG,
  MENU_GRID_CONFIG,
  FORM_GRID_CONFIG,
  GRID_CONFIGS,
  getCurrentBreakpoint,
  getResponsiveValue,
  createResponsiveCols,
  createResponsiveGap,
  isMobile,
  isTablet,
  isDesktop,
  getDeviceType,
  getGridConfig,
  createMediaQuery,
  createGridClasses
}

/**
 * 使用示例:
 * 
 * 1. 使用预定义配置:
 * ```vue
 * <script setup>
 * import { STANDARD_GRID_CONFIG } from '@/config/grid'
 * </script>
 * 
 * <template>
 *   <ResponsiveGrid v-bind="STANDARD_GRID_CONFIG">
 *     <ResponsiveGridItem>内容</ResponsiveGridItem>
 *   </ResponsiveGrid>
 * </template>
 * ```
 * 
 * 2. 自定义配置:
 * ```vue
 * <script setup>
 * import { createResponsiveCols, createResponsiveGap } from '@/config/grid'
 * 
 * const cols = createResponsiveCols(1, 2, 3, 4, 5)
 * const gap = createResponsiveGap(8, 12, 16, 20, 24)
 * </script>
 * 
 * <template>
 *   <ResponsiveGrid :cols="cols" :gap="gap">
 *     <ResponsiveGridItem>内容</ResponsiveGridItem>
 *   </ResponsiveGrid>
 * </template>
 * ```
 * 
 * 3. 响应式判断:
 * ```vue
 * <script setup>
 * import { isMobile, getDeviceType } from '@/config/grid'
 * 
 * const deviceType = getDeviceType()
 * const showMobileLayout = isMobile()
 * </script>
 * ```
 * 
 * 4. 获取配置值:
 * ```vue
 * <script setup>
 * import { getResponsiveValue, STANDARD_GRID_CONFIG } from '@/config/grid'
 * 
 * const currentCols = getResponsiveValue(STANDARD_GRID_CONFIG.cols)
 * const currentGap = getResponsiveValue(STANDARD_GRID_CONFIG.gap)
 * </script>
 * ```
 */