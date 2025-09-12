import type { MenuOption } from 'naive-ui'
import { h } from 'vue'

export const mainMenuOptions: MenuOption[] = [
  { label: '上传题库', key: 'upload', icon: () => h('i', { class: 'i-ion-cloud-upload-outline' }) },
  { label: '题库列表', key: 'bank', icon: () => h('i', { class: 'i-ion-library-outline' }) },
  { label: '数据统计', key: 'statistics', icon: () => h('i', { class: 'i-ion-stats-chart-outline' }) },
  { label: '设置', key: 'settings', icon: () => h('i', { class: 'i-ion-settings-outline' }) }
]
