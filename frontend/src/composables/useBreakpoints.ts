/**
 * 响应式断点管理 Composable
 * 
 * 提供基于窗口宽度的响应式断点检测功能，支持实时监听窗口大小变化。
 * 使用统一的断点配置，确保与 CSS 媒体查询保持一致。
 * 
 * @module useBreakpoints
 * @author EasyTiWu Team
 * @since 1.0.0
 */

import {computed, onMounted, onUnmounted, ref} from 'vue'
import {type BreakpointKey, BREAKPOINTS} from '@/config/breakpoints'

/**
 * 导出统一的断点配置
 * 与 CSS 中的断点保持一致，确保 JavaScript 和样式的断点判断统一
 */
export const breakpoints = BREAKPOINTS

/**
 * 响应式断点管理 Hook
 * 
 * 提供实时的断点状态检测，自动监听窗口大小变化并更新断点状态。
 * 所有断点状态都是响应式的，可以直接在模板或计算属性中使用。
 * 
 * @returns {Object} 包含各种断点状态和工具方法的对象
 * 
 * @example
 * ```typescript
 * import { useBreakpoints } from '@/composables/useBreakpoints'
 * 
 * const { isMobile, isDesktop, getCurrentBreakpoint } = useBreakpoints()
 * 
 * // 在模板中使用
 * // <div v-if="isMobile">移动端内容</div>
 * 
 * // 在计算属性中使用
 * const gridCols = computed(() => isMobile.value ? 1 : 3)
 * 
 * // 获取当前断点名称
 * console.log(getCurrentBreakpoint()) // 'md'
 * ```
 */
export function useBreakpoints() {
  /** 当前窗口宽度的响应式引用 */
  const windowWidth = ref(0)
  
  // ==================== 精确断点状态 ====================
  
  /** 是否为超小屏幕 (< 576px) */
  const isXs = computed(() => windowWidth.value < breakpoints.sm)
  
  /** 是否为小屏幕 (576px - 768px) */
  const isSm = computed(() => windowWidth.value >= breakpoints.sm && windowWidth.value < breakpoints.md)
  
  /** 是否为中等屏幕 (768px - 992px) */
  const isMd = computed(() => windowWidth.value >= breakpoints.md && windowWidth.value < breakpoints.lg)
  
  /** 是否为大屏幕 (992px - 1200px) */
  const isLg = computed(() => windowWidth.value >= breakpoints.lg && windowWidth.value < breakpoints.xl)
  
  /** 是否为超大屏幕 (1200px - 1400px) */
  const isXl = computed(() => windowWidth.value >= breakpoints.xl && windowWidth.value < breakpoints.xxl)
  
  /** 是否为超超大屏幕 (>= 1400px) */
  const isXxl = computed(() => windowWidth.value >= breakpoints.xxl)
  
  
  // ==================== 语义化断点状态 ====================
  
  /** 是否为移动端设备 (< 768px) - 兼容旧版本 */
  const isMobile = computed(() => windowWidth.value < breakpoints.md)
  
  /** 是否为平板设备 (768px - 992px) - 兼容旧版本 */
  const isTablet = computed(() => windowWidth.value >= breakpoints.md && windowWidth.value < breakpoints.lg)
  
  /** 是否为桌面设备 (>= 992px) - 兼容旧版本 */
  const isDesktop = computed(() => windowWidth.value >= breakpoints.lg)
  
  /** 是否为大屏设备 (>= 1200px) - 兼容旧版本 */
  const isLarge = computed(() => windowWidth.value >= breakpoints.xl)
  
  // ==================== 内部工具方法 ====================
  
  /**
   * 更新窗口宽度
   * 从 window.innerWidth 获取最新的窗口宽度并更新响应式状态
   */
  const updateBreakpoints = () => {
    windowWidth.value = window.innerWidth
  }
  
  /**
   * 窗口大小变化事件处理器
   * 使用防抖优化性能，避免频繁触发更新
   */
  const handleResize = () => {
    updateBreakpoints()
  }
  
  // ==================== 生命周期管理 ====================
  
  onMounted(() => {
    // 组件挂载时立即获取当前窗口宽度
    updateBreakpoints()
    // 监听窗口大小变化事件
    window.addEventListener('resize', handleResize)
  })
  
  onUnmounted(() => {
    // 组件卸载时清理事件监听器，防止内存泄漏
    window.removeEventListener('resize', handleResize)
  })
  
  // ==================== 工具方法 ====================
  
  /**
   * 获取当前断点名称
   * 
   * 根据当前窗口宽度返回对应的断点标识符
   * 
   * @returns {BreakpointKey} 当前断点名称 ('xs' | 'sm' | 'md' | 'lg' | 'xl' | 'xxl')
   * 
   * @example
   * ```typescript
   * const currentBp = getCurrentBreakpoint()
   * console.log(currentBp) // 'md'
   * 
   * // 根据断点执行不同逻辑
   * if (currentBp === 'xs' || currentBp === 'sm') {
   *   // 移动端逻辑
   * }
   * ```
   */
  const getCurrentBreakpoint = (): BreakpointKey => {
    if (isXs.value) return 'xs'
    if (isSm.value) return 'sm'
    if (isMd.value) return 'md'
    if (isLg.value) return 'lg'
    if (isXl.value) return 'xl'
    return 'xxl'
  }
  
  /**
   * 检查是否匹配指定断点
   * 
   * 提供编程式的断点检查方法，适合在条件逻辑中使用
   * 
   * @param {BreakpointKey} breakpoint - 要检查的断点名称
   * @returns {boolean} 是否匹配指定断点
   * 
   * @example
   * ```typescript
   * // 检查是否为移动端
   * if (matchBreakpoint('xs') || matchBreakpoint('sm')) {
   *   // 移动端特殊处理
   * }
   * 
   * // 动态检查断点
   * const targetBreakpoint = 'lg'
   * if (matchBreakpoint(targetBreakpoint)) {
   *   // 大屏幕逻辑
   * }
   * ```
   */
  const matchBreakpoint = (breakpoint: BreakpointKey) => {
    switch (breakpoint) {
      case 'xs': return isXs.value
      case 'sm': return isSm.value
      case 'md': return isMd.value
      case 'lg': return isLg.value
      case 'xl': return isXl.value
      case 'xxl': return isXxl.value
      default: return false
    }
  }
  
  /**
   * 检查是否大于等于指定断点
   * 
   * 用于检查当前屏幕宽度是否达到或超过指定断点，
   * 适合实现"向上"的响应式逻辑。
   * 
   * @param {BreakpointKey} breakpoint - 要检查的断点名称
   * @returns {boolean} 是否大于等于指定断点
   * 
   * @example
   * ```typescript
   * // 检查是否为中等屏幕及以上
   * if (isAbove('md')) {
   *   // 显示桌面端布局
   * }
   * ```
   */
  const isAbove = (breakpoint: BreakpointKey) => {
    return windowWidth.value >= breakpoints[breakpoint]
  }
  
  /**
   * 检查是否小于指定断点
   * 
   * 用于检查当前屏幕宽度是否小于指定断点，
   * 适合实现"向下"的响应式逻辑。
   * 
   * @param {BreakpointKey} breakpoint - 要检查的断点名称
   * @returns {boolean} 是否小于指定断点
   * 
   * @example
   * ```typescript
   * // 检查是否为小屏幕
   * if (isBelow('md')) {
   *   // 显示移动端布局
   * }
   * ```
   */
  const isBelow = (breakpoint: BreakpointKey) => {
    return windowWidth.value < breakpoints[breakpoint]
  }
  
  /**
   * 检查是否在指定断点范围内
   * 
   * 用于检查当前屏幕宽度是否在两个断点之间，
   * 适合实现精确的断点范围判断。
   * 
   * @param {BreakpointKey} min - 最小断点（包含）
   * @param {BreakpointKey} max - 最大断点（不包含）
   * @returns {boolean} 是否在指定范围内
   * 
   * @example
   * ```typescript
   * // 检查是否为平板尺寸
   * if (isBetween('md', 'lg')) {
   *   // 平板专用逻辑
   * }
   * ```
   */
  const isBetween = (min: BreakpointKey, max: BreakpointKey) => {
    return windowWidth.value >= breakpoints[min] && windowWidth.value < breakpoints[max]
  }
  
  // ==================== 返回值 ====================
  
  return {
    // 响应式状态
    /** 当前窗口宽度（像素） */
    windowWidth,
    
    // 精确断点状态
    /** 是否为超小屏幕 (< 576px) */
    isXs,
    /** 是否为小屏幕 (576px - 768px) */
    isSm,
    /** 是否为中等屏幕 (768px - 992px) */
    isMd,
    /** 是否为大屏幕 (992px - 1200px) */
    isLg,
    /** 是否为超大屏幕 (1200px - 1400px) */
    isXl,
    /** 是否为超超大屏幕 (>= 1400px) */
    isXxl,
    
    // 语义化断点状态（兼容旧版本）
    /** 是否为移动端设备 (< 768px) */
    isMobile,
    /** 是否为平板设备 (768px - 992px) */
    isTablet,
    /** 是否为桌面设备 (>= 992px) */
    isDesktop,
    /** 是否为大屏设备 (>= 1200px) */
    isLarge,
    
    // 工具方法
    /** 获取当前断点名称 */
    getCurrentBreakpoint,
    /** 检查是否匹配指定断点 */
    matchBreakpoint,
    /** 检查是否大于等于指定断点 */
    isAbove,
    /** 检查是否小于指定断点 */
    isBelow,
    /** 检查是否在指定断点范围内 */
    isBetween,
    
    // 断点配置
    /** 断点配置对象 */
    breakpoints
  }
}

// 导出类型
/**
 * useBreakpoints Hook 的返回值类型
 * 
 * 提供完整的类型定义，便于在 TypeScript 项目中使用。
 * 包含所有断点状态、工具方法和配置的类型信息。
 * 
 * @example
 * ```typescript
 * import type { UseBreakpointsReturn } from '@/composables/useBreakpoints'
 * 
 * // 在组合式函数中使用
 * function useResponsiveLayout(): { breakpoints: UseBreakpointsReturn } {
 *   const breakpoints = useBreakpoints()
 *   return { breakpoints }
 * }
 * ```
 */
export type UseBreakpointsReturn = ReturnType<typeof useBreakpoints>