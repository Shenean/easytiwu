/**
 * 数字格式化工具函数模块
 * 
 * 提供各种数字格式化功能，包括千分位分隔符、百分比计算等。
 * 所有函数都具有空值安全性，能够正确处理 null 和 undefined 值。
 * 
 * @module NumberUtils
 * @author EasyTiWu Team
 * @since 1.0.0
 */

/**
 * 格式化数字为带千分位分隔符的字符串
 * 
 * 使用浏览器的本地化 API 来格式化数字，自动根据用户的语言环境
 * 添加适当的千分位分隔符（如中文环境下的逗号）。
 * 
 * @param {number | null | undefined} value - 要格式化的数字
 * @returns {string} 格式化后的字符串
 * 
 * @example
 * ```typescript
 * formatNumber(1234567)     // "1,234,567"
 * formatNumber(1234.56)     // "1,234.56"
 * formatNumber(null)        // "0"
 * formatNumber(undefined)   // "0"
 * ```
 */
export const formatNumber = (value: number | null | undefined): string => {
  return (value ?? 0).toLocaleString()
}

/**
 * 计算并格式化百分比
 * 
 * 根据正确数量和总数量计算百分比，并格式化为带百分号的字符串。
 * 具有除零保护，当总数为0时返回"0%"。
 * 
 * @param {number | null | undefined} correct - 正确的数量
 * @param {number | null | undefined} total - 总数量
 * @returns {string} 格式化的百分比字符串，保留一位小数
 * 
 * @example
 * ```typescript
 * formatPercentage(85, 100)    // "85.0%"
 * formatPercentage(2, 3)       // "66.7%"
 * formatPercentage(0, 0)       // "0%"
 * formatPercentage(null, 100)  // "0%"
 * formatPercentage(50, null)   // "0%"
 * ```
 */
export const formatPercentage = (correct: number | null | undefined, total: number | null | undefined): string => {
  const c = correct ?? 0
  const t = total ?? 0
  
  // 防止除零错误
  if (t === 0) return '0%'
  
  const pct = (c / t) * 100
  // 保留一位小数，确保显示精度
  return `${pct.toFixed(1)}%`
}
