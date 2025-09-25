/**
 * 导航菜单配置模块
 *
 * 本模块定义了应用主导航菜单的配置，包括菜单项的标签、图标、路由键名等。
 * 支持国际化、权限控制和动态显示/隐藏功能。
 *
 * ## 功能特性
 * - 🌍 **国际化支持**: 菜单标签支持多语言动态切换
 * - 🔐 **权限控制**: 支持基于权限的菜单项显示控制
 * - 🎨 **图标系统**: 使用Iconify图标库，支持丰富的图标选择
 * - 📱 **响应式设计**: 适配不同屏幕尺寸的导航需求
 * - 🔧 **可扩展性**: 易于添加新的菜单项和功能
 *
 * @module MenuOptions
 * @author EasyTiWu Team
 * @since 1.0.0
 *
 * @example
 * ```typescript
 * import { mainMenuOptions } from '@/config/menuOptions'
 *
 * // 在导航组件中使用
 * <n-menu :options="mainMenuOptions" />
 *
 * // 动态过滤菜单项
 * const visibleMenus = mainMenuOptions.filter(item => !item.meta?.hidden)
 * ```
 */

// 通用菜单项类型定义
interface MenuOption {
  label: string | (() => string);
  key: string;
  icon?: () => any;
  children?: MenuOption[];
}

import {h} from "vue";
import i18n from "../i18n";
import {BookIcon, ChartIcon, SettingIcon} from "tdesign-icons-vue-next";

/**
 * 菜单项元信息接口
 *
 * 定义菜单项的额外元数据，用于控制菜单的显示行为和权限验证。
 * 这些元信息不会直接影响菜单的渲染，而是用于业务逻辑判断。
 *
 * @interface MenuItemMeta
 * @since 1.0.0
 *
 * @example
 * ```typescript
 * const adminMenu: ExtendedMenuOption = {
 *   label: '管理员面板',
 *   key: 'admin',
 *   meta: {
 *     hidden: false,
 *     permission: ['admin', 'super_admin']
 *   }
 * }
 * ```
 */
interface MenuItemMeta {
  /** 是否隐藏该菜单项，用于动态控制菜单显示 */
  hidden?: boolean;
  /** 访问该菜单项所需的权限列表，用于权限控制 */
  permission?: string[];
}

/**
 * 扩展的菜单选项类型
 *
 * 基于Naive UI的MenuOption类型，添加了自定义的元信息字段。
 * 保持与原生MenuOption的完全兼容性，同时提供额外的业务功能。
 *
 * @typedef {MenuOption & { meta?: MenuItemMeta }} ExtendedMenuOption
 * @since 1.0.0
 *
 * @example
 * ```typescript
 * const menuItem: ExtendedMenuOption = {
 *   label: () => t('menu.dashboard'),
 *   key: 'dashboard',
 *   icon: () => h('i', { class: 'i-dashboard' }),
 *   meta: {
 *     permission: ['user']
 *   }
 * }
 * ```
 */
export type ExtendedMenuOption = MenuOption & {
  meta?: MenuItemMeta;
};

/**
 * 主导航菜单配置
 *
 * 定义应用的主要导航菜单项，包括上传、题库、统计和设置四个核心功能模块。
 * 每个菜单项都支持国际化，图标使用Ionicons图标库。
 *
 * ## 菜单项说明
 * - **上传**: 题目文件上传功能，支持批量导入题库
 * - **题库**: 题库管理功能，查看和管理已导入的题库
 * - **统计**: 数据统计功能，查看答题统计和分析报告
 * - **设置**: 应用设置功能，包括主题、语言等个性化配置
 *
 * @constant {ExtendedMenuOption[]} mainMenuOptions
 * @since 1.0.0
 *
 * @example
 * ```typescript
 * // 在导航组件中使用
 * import { mainMenuOptions } from '@/config/menuOptions'
 *
 * // 基础使用
 * <n-menu :options="mainMenuOptions" v-model:value="activeKey" />
 *
 * // 带权限过滤的使用
 * const filteredMenus = computed(() => {
 *   return mainMenuOptions.filter(item => {
 *     if (item.meta?.hidden) return false
 *     if (item.meta?.permission) {
 *       return hasPermission(item.meta.permission)
 *     }
 *     return true
 *   })
 * })
 * ```
 */
export const mainMenuOptions: ExtendedMenuOption[] = [
  {
    label: () => i18n.global.t("navigation.bank"),
    key: "bank",
    icon: () => h(BookIcon, { size: "18px" }),
  },
  {
    label: () => i18n.global.t("navigation.statistics"),
    key: "statistics",
    icon: () => h(ChartIcon, { size: "18px" }),
  },
  {
    label: () => i18n.global.t("navigation.settings"),
    key: "settings",
    icon: () => h(SettingIcon, { size: "18px" }),
  },
];
