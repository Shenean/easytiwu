import {computed, onMounted, onUnmounted, ref} from 'vue'
import {type BreakpointKey, BREAKPOINTS} from '@/config/breakpoints'

// 使用统一的断点配置
export const breakpoints = BREAKPOINTS

export function useBreakpoints() {
  const windowWidth = ref(0)
  
  // 响应式断点状态（使用计算属性提高性能）
  const isXs = computed(() => windowWidth.value < breakpoints.sm)
  const isSm = computed(() => windowWidth.value >= breakpoints.sm && windowWidth.value < breakpoints.md)
  const isMd = computed(() => windowWidth.value >= breakpoints.md && windowWidth.value < breakpoints.lg)
  const isLg = computed(() => windowWidth.value >= breakpoints.lg && windowWidth.value < breakpoints.xl)
  const isXl = computed(() => windowWidth.value >= breakpoints.xl && windowWidth.value < breakpoints.xxl)
  const isXxl = computed(() => windowWidth.value >= breakpoints.xxl)
  
  // 兼容旧版本的断点状态
  const isMobile = computed(() => windowWidth.value < breakpoints.md)
  const isTablet = computed(() => windowWidth.value >= breakpoints.md && windowWidth.value < breakpoints.lg)
  const isDesktop = computed(() => windowWidth.value >= breakpoints.lg)
  const isLarge = computed(() => windowWidth.value >= breakpoints.xl)
  
  // 更新窗口宽度
  const updateBreakpoints = () => {
    windowWidth.value = window.innerWidth
  }
  
  // 监听窗口大小变化
  const handleResize = () => {
    updateBreakpoints()
  }
  
  onMounted(() => {
    updateBreakpoints()
    window.addEventListener('resize', handleResize)
  })
  
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
  })
  
  // 获取当前断点名称
  const getCurrentBreakpoint = (): BreakpointKey => {
    if (isXs.value) return 'xs'
    if (isSm.value) return 'sm'
    if (isMd.value) return 'md'
    if (isLg.value) return 'lg'
    if (isXl.value) return 'xl'
    return 'xxl'
  }
  
  // 检查是否匹配指定断点
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
  
  // 检查是否大于等于指定断点
  const isAbove = (breakpoint: BreakpointKey) => {
    return windowWidth.value >= breakpoints[breakpoint]
  }
  
  // 检查是否小于指定断点
  const isBelow = (breakpoint: BreakpointKey) => {
    return windowWidth.value < breakpoints[breakpoint]
  }
  
  // 检查是否在指定断点范围内
  const isBetween = (min: BreakpointKey, max: BreakpointKey) => {
    return windowWidth.value >= breakpoints[min] && windowWidth.value < breakpoints[max]
  }
  
  return {
    // 窗口宽度
    windowWidth,
    
    // 新的断点状态
    isXs,
    isSm,
    isMd,
    isLg,
    isXl,
    isXxl,
    
    // 兼容旧版本的断点状态
    isMobile,
    isTablet,
    isDesktop,
    isLarge,
    
    // 工具方法
    getCurrentBreakpoint,
    matchBreakpoint,
    isAbove,
    isBelow,
    isBetween,
    
    // 断点配置
    breakpoints
  }
}

// 导出类型
export type UseBreakpointsReturn = ReturnType<typeof useBreakpoints>