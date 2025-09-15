// src/config/menuOptions.ts
import type {MenuOption} from "naive-ui";
import {h} from "vue";
import i18n from "../i18n";

// 定义元信息
interface MenuItemMeta {
  hidden?: boolean;
  permission?: string[];
}

// 扩展菜单项类型
export type ExtendedMenuOption = MenuOption & {
  meta?: MenuItemMeta;
};

// 菜单项配置
export const mainMenuOptions: ExtendedMenuOption[] = [
  {
    label: () => i18n.global.t('navigation.upload'),
    key: "upload",
    icon: () => h("i", { class: "i-ion-cloud-upload-outline" }),
  },
  {
    label: () => i18n.global.t('navigation.bank'),
    key: "bank",
    icon: () => h("i", { class: "i-ion-library-outline" }),
  },
  {
    label: () => i18n.global.t('navigation.statistics'),
    key: "statistics",
    icon: () => h("i", { class: "i-ion-stats-chart-outline" }),
  },
  {
    label: () => i18n.global.t('navigation.settings'),
    key: "settings",
    icon: () => h("i", { class: "i-ion-settings-outline" }),
  },
];
