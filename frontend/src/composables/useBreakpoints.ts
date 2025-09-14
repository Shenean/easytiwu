import {onMounted, onUnmounted, ref} from 'vue'

// 断点定义
export const breakpoints = {
  mobile: 480,
  tablet: 768,
  desktop: 1200
} as const

export function useBreakpoints() {
  const windowWidth = ref(0)
  
  // 响应式断点状态
  const isMobile = ref(false)
  const isTablet = ref(false)
  const isDesktop = ref(false)
  const isLarge = ref(false)
  
  // 更新断点状态
  const updateBreakpoints = () => {
    const width = window.innerWidth
    windowWidth.value = width
    
    isMobile.value = width <= breakpoints.mobile
    isTablet.value = width > breakpoints.mobile && width <= breakpoints.tablet
    isDesktop.value = width > breakpoints.tablet && width <= breakpoints.desktop
    isLarge.value = width > breakpoints.desktop
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
  const getCurrentBreakpoint = () => {
    if (isMobile.value) return 'mobile'
    if (isTablet.value) return 'tablet'
    if (isDesktop.value) return 'desktop'
    return 'large'
  }
  
  // 检查是否匹配指定断点
  const matchBreakpoint = (breakpoint: keyof typeof breakpoints | 'large') => {
    switch (breakpoint) {
      case 'mobile':
        return isMobile.value
      case 'tablet':
        return isTablet.value
      case 'desktop':
        return isDesktop.value
      case 'large':
        return isLarge.value
      default:
        return false
    }
  }
  
  // 检查是否大于等于指定断点
  const isAbove = (breakpoint: keyof typeof breakpoints) => {
    return windowWidth.value > breakpoints[breakpoint]
  }
  
  // 检查是否小于等于指定断点
  const isBelow = (breakpoint: keyof typeof breakpoints) => {
    return windowWidth.value <= breakpoints[breakpoint]
  }
  
  return {
    windowWidth,
    isMobile,
    isTablet,
    isDesktop,
    isLarge,
    getCurrentBreakpoint,
    matchBreakpoint,
    isAbove,
    isBelow,
    breakpoints
  }
}

// 导出类型
export type BreakpointName = keyof typeof breakpoints | 'large'
export type UseBreakpointsReturn = ReturnType<typeof useBreakpoints>