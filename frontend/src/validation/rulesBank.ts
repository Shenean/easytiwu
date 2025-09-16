/**
 * 题库表单验证规则模块
 * 
 * 本模块定义了题库相关表单的验证规则，使用Naive UI的FormRules类型。
 * 所有验证消息都支持国际化，通过i18n系统动态获取对应语言的错误提示。
 * 
 * @module BankValidationRules
 * @author EasyTiWu Team
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * import { bankFormRules } from '@/validation/rulesBank'
 * 
 * // 在表单组件中使用
 * const formRef = ref<FormInst>()
 * const formValue = ref({
 *   name: '',
 *   description: '',
 *   file: []
 * })
 * 
 * // 应用验证规则
 * <n-form ref="formRef" :model="formValue" :rules="bankFormRules">
 *   <n-form-item path="name" label="题库名称">
 *     <n-input v-model:value="formValue.name" />
 *   </n-form-item>
 * </n-form>
 * ```
 */

import type {FormRules, UploadFileInfo} from 'naive-ui'
import {i18n} from '../i18n'

/** 国际化翻译函数的快捷引用 */
const t = i18n.global.t

/**
 * 题库表单验证规则配置
 * 
 * 定义了题库创建和编辑表单的完整验证规则，包括：
 * - name: 题库名称验证（必填，最大长度15字符）
 * - description: 题库描述验证（可选，最大长度30字符）
 * - file: 文件上传验证（必填，自定义验证器确保文件存在）
 * 
 * 所有验证规则都支持国际化，错误消息会根据当前语言环境动态显示。
 * 
 * @constant {FormRules} bankFormRules
 * @since 1.0.0
 * 
 * @example
 * ```typescript
 * // 验证规则的触发时机：
 * // - name: blur（失去焦点）和 input（输入时）
 * // - description: input（输入时）
 * // - file: change（文件选择变化时）
 * 
 * // 使用示例
 * const isValid = await formRef.value?.validate()
 * if (isValid) {
 *   // 表单验证通过，可以提交
 *   console.log('表单数据有效')
 * }
 * ```
 */
export const bankFormRules: FormRules = {
  /** 
   * 题库名称验证规则
   * - 必填项：题库名称不能为空
   * - 长度限制：最大15个字符
   * - 触发时机：失去焦点时检查必填，输入时检查长度
   */
  name: [
    { required: true, message: () => t('validation.bankNameRequired'), trigger: 'blur' },
    { max: 15, message: () => t('validation.maxLength', { count: 15 }), trigger: 'input' }
  ],
  /** 
   * 题库描述验证规则
   * - 可选项：描述可以为空
   * - 长度限制：最大30个字符
   * - 触发时机：输入时实时检查长度
   */
  description: [
    { max: 30, message: () => t('validation.maxLength', { count: 30 }), trigger: 'input' }
  ],
  /** 
   * 文件上传验证规则
   * - 必填项：必须选择至少一个文件
   * - 自定义验证器：检查文件数组是否为空
   * - 触发时机：文件选择变化时
   */
  file: [
    {
      required: true,
      message: () => t('validation.fileRequired'),
      trigger: 'change',
      /**
       * 文件上传自定义验证器
       * 
       * 检查上传的文件列表是否为空，确保用户至少选择了一个文件。
       * 
       * @param {any} _rule - 验证规则对象（未使用）
       * @param {UploadFileInfo[]} value - 上传的文件信息数组
       * @returns {boolean | Error} 验证通过返回true，失败返回Error对象
       */
      validator: (_rule: any, value: UploadFileInfo[]) => {
        if (!value || value.length === 0) {
          return new Error(t('validation.fileRequired'))
        }
        return true
      }
    }
  ]
}
