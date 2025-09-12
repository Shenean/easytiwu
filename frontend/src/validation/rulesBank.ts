import type { FormRules } from 'naive-ui'
import type { UploadFileInfo } from 'naive-ui'

export const bankFormRules: FormRules = {
  name: [
    { required: true, message: '题库名不能为空', trigger: 'blur' },
    { max: 15, message: '最多 15 字', trigger: 'input' }
  ],
  description: [
    { max: 30, message: '最多 30 字', trigger: 'input' }
  ],
  file: [
    {
      required: true,
      message: '请上传文件',
      trigger: 'change',
      validator: (_rule: any, value: UploadFileInfo[]) => {
        if (!value || value.length === 0) {
          return new Error('请上传文件')
        }
        return true
      }
    }
  ]
}
