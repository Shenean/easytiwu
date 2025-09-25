import {DialogPlugin, MessagePlugin, NotifyPlugin} from 'tdesign-vue-next'
import {getTheme, setTheme, toggleTheme} from './theme'

export interface ConfigTestResult {
  success: boolean
  message: string
  details?: any
}

export const testTDesignComponents = (): ConfigTestResult => {
  try {
    const hasMessagePlugin = typeof MessagePlugin !== 'undefined'
    const hasNotifyPlugin = typeof NotifyPlugin !== 'undefined'
    const hasDialogPlugin = typeof DialogPlugin !== 'undefined'
    
    if (!hasMessagePlugin || !hasNotifyPlugin || !hasDialogPlugin) {
      return {
        success: false,
        message: 'TDesign 插件导入失败',
        details: {
          MessagePlugin: hasMessagePlugin,
          NotifyPlugin: hasNotifyPlugin,
          DialogPlugin: hasDialogPlugin
        }
      }
    }
    
    return {
      success: true,
      message: 'TDesign 组件导入成功'
    }
  } catch (error) {
    return {
      success: false,
      message: 'TDesign 组件测试失败',
      details: error
    }
  }
}

export const testThemeSystem = (): ConfigTestResult => {
  try {
    const initialTheme = getTheme()
    
    setTheme('dark')
    const darkTheme = getTheme()
    
    setTheme('light')
    const lightTheme = getTheme()
    
    const toggledTheme = toggleTheme()
    
    setTheme(initialTheme)
    
    if (darkTheme !== 'dark' || lightTheme !== 'light') {
      return {
        success: false,
        message: '主题切换功能异常',
        details: {
          darkTheme,
          lightTheme,
          toggledTheme
        }
      }
    }
    
    return {
      success: true,
      message: '主题系统工作正常'
    }
  } catch (error) {
    return {
      success: false,
      message: '主题系统测试失败',
      details: error
    }
  }
}

export const testCSSVariables = (): ConfigTestResult => {
  try {
    const root = document.documentElement
    const computedStyle = getComputedStyle(root)
    
    const brandColor = computedStyle.getPropertyValue('--td-brand-color')
    const fontSize = computedStyle.getPropertyValue('--td-font-size-base')
    const radius = computedStyle.getPropertyValue('--td-radius-default')
    
    if (!brandColor || !fontSize || !radius) {
      return {
        success: false,
        message: 'TDesign CSS 变量未正确加载',
        details: {
          brandColor,
          fontSize,
          radius
        }
      }
    }
    
    return {
      success: true,
      message: 'CSS 变量系统正常'
    }
  } catch (error) {
    return {
      success: false,
      message: 'CSS 变量测试失败',
      details: error
    }
  }
}

export const runAllTests = (): ConfigTestResult[] => {
  return [
    testTDesignComponents(),
    testThemeSystem(),
    testCSSVariables()
  ]
}

export const logTestResults = (): void => {
  const results = runAllTests()
  
  console.group('🔧 TDesign 配置测试结果')
  
  results.forEach((result, index) => {
    const icon = result.success ? '✅' : '❌'
    console.log(`${icon} 测试 ${index + 1}: ${result.message}`)
    
    if (result.details) {
      console.log('详细信息:', result.details)
    }
  })
  
  const allPassed = results.every(result => result.success)
  const summary = allPassed ? '🎉 所有测试通过！' : '⚠️ 部分测试失败，请检查配置'
  
  console.log('\n' + summary)
  console.groupEnd()
}