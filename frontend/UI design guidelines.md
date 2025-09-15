# 🎨 Naive UI 设计规范 v1.0

> 基于 [Naive UI](https://www.naiveui.com/) v2.x 的标准化前端设计系统规范  
> 适用于 Vue 3 + TypeScript 项目  
> 由设计与前端团队共同制定，确保视觉一致性与开发效率

## 一、设计原则

1. **一致性（Consistency）**
   - 所有页面使用统一的组件、间距、颜色、字体。
   - 避免自定义样式覆盖，优先使用 Naive UI 原生 API。
2. **简洁性（Simplicity）**
   - 减少视觉噪音，突出核心内容。
   - 避免过度装饰，保持界面清爽。
3. **可用性（Usability）**
   - 交互符合用户直觉，反馈及时明确。
   - 支持键盘导航与屏幕阅读器（无障碍）。
4. **可扩展性（Scalability）**
   - 组件设计支持主题切换、国际化、响应式。
   - 结构清晰，便于新增模块或功能。
5. **性能优先（Performance First）**
   - 避免过度嵌套组件，减少不必要的 re-render。
   - 使用 `n-config-provider` 统一配置，避免重复设置。

## 二、主题与颜色系统

### 2.1 主题配置方式

使用 `n-config-provider` 全局配置主题：

```vue
<template>
  <n-config-provider :theme="theme" :locale="zhCN" :date-locale="dateZhCN">
    <App />
  </n-config-provider>
</template>

<script setup>
import { darkTheme, zhCN, dateZhCN } from "naive-ui";

const theme = ref(null); // 或 darkTheme 启用暗色模式
</script>
```

### 2.2 标准色板（基于 Naive UI 默认色系）

| 类型           | 色值（HEX）               | 用途说明                           |
| -------------- | ------------------------- | ---------------------------------- |
| Primary        | `#2080F0`                 | 主要操作按钮、链接、强调色         |
| Success        | `#18A058`                 | 成功状态、完成操作                 |
| Warning        | `#F0A020`                 | 警告提示、需注意操作               |
| Error          | `#F04040`                 | 错误状态、危险操作                 |
| Info           | `#2080F0`                 | 信息提示（同 Primary）             |
| Text Primary   | `#2C2C2C`                 | 主要文本颜色（深色模式为 #F5F5F5） |
| Text Secondary | `#666666`                 | 次要文本、描述文字                 |
| Border         | `#D9D9D9`                 | 边框、分割线                       |
| Background     | `#FFFFFF`                 | 页面背景（深色模式为 #1E1E1E）     |
| Hover          | `rgba(32, 128, 240, 0.1)` | 按钮/卡片悬停背景色                |

> ✅ 所有颜色应通过 `useThemeVars()` 或 CSS 变量 `var(--n-color)` 引用，避免硬编码。

---

## 三、排版系统 Typography

### 3.1 字体栈

```css
font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
  "Helvetica Neue", Arial, "Noto Sans", sans-serif, "Apple Color Emoji",
  "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
```

> Naive UI 默认使用系统字体，无需额外引入。

### 3.2 字号与字重

| 类型           | 字号（px） | 字重 | 使用场景           |
| -------------- | ---------- | ---- | ------------------ |
| Display Large  | 32         | 700  | 页面标题、大屏展示 |
| Display Medium | 24         | 700  | 模块标题           |
| Title Large    | 20         | 600  | 卡片/区块标题      |
| Title Medium   | 16         | 600  | 按钮文字、导航标题 |
| Body Large     | 16         | 400  | 正文、表单标签     |
| Body Medium    | 14         | 400  | 普通文本、辅助信息 |
| Body Small     | 12         | 400  | 注释、时间、标签   |
| Caption        | 12         | 500  | 表格列头、小提示   |

> 使用 `n-p`, `n-h1`~`n-h6`, `n-text` 等组件控制排版，避免自定义 `<div style="font-size:...">`

---

## 四、布局与间距系统

### 4.1 栅格系统（Grid）

使用 `n-grid`, `n-gi` 布局响应式页面：

```vue
<n-grid x-gap="16" y-gap="16" cols="1 s:2 m:3 l:4" responsive="screen">
  <n-gi><div class="demo-grid-item">1</div></n-gi>
  <n-gi><div class="demo-grid-item">2</div></n-gi>
</n-grid>
```

> ✅ 推荐断点：`s: 768px`, `m: 1024px`, `l: 1280px`

### 4.2 间距规范（Spacing）

使用 `8px` 基准倍数：

| 名称 | 值（px） | 使用场景                   |
| ---- | -------- | -------------------------- |
| XS   | 4        | 图标与文字间距、微小内边距 |
| SM   | 8        | 表单项间距、按钮内边距     |
| MD   | 16       | 模块间距、卡片内边距       |
| LG   | 24       | 页面区块间距               |
| XL   | 32       | 页面主容器上下边距         |
| XXL  | 48       | 大屏页头/页脚间距          |

> 在组件中使用 `class="m-b-16 p-l-8"` 或通过 `style` 设置，推荐封装 CSS 类：

```css
.m-b-8 {
  margin-bottom: 8px;
}
.p-16 {
  padding: 16px;
}
```

---

## 五、组件使用规范

### 5.1 按钮 Button

- **类型**：`primary`（主操作）、`default`（次要）、`text`（文字链）、`dashed`（特殊操作）
- **尺寸**：`small`、`medium`（默认）、`large`
- **状态**：禁用使用 `disabled`，危险操作使用 `type="error"`

```vue
<n-button type="primary" size="large">提交</n-button>
<n-button text>取消</n-button>
```

> ⚠️ 避免在同一个操作区域使用多个 `primary` 按钮。

---

### 5.2 表单 Form

- 使用 `n-form`, `n-form-item`, `n-input` 等组合
- 必填项标注 `:required="true"` 并加 `*` 号（通过 label 插槽）
- 错误提示使用 `validation-status` + `feedback`

```vue
<n-form-item label="用户名" :required="true" path="name">
  <n-input v-model:value="formValue.name" placeholder="请输入" />
</n-form-item>
```

> ✅ 推荐使用 `async-validator` 进行表单校验

---

### 5.3 表格 Table

- 列配置使用 `columns` 数组，避免模板内写逻辑
- 分页使用 `n-pagination`，与表格联动
- 操作列固定在右侧，使用 `n-button text` 类型

```vue
<n-data-table :columns="columns" :data="data" :pagination="pagination" remote />
```

> ✅ 开启 `scroll-x` 避免列过多溢出，固定关键列（如操作列）

---

### 5.4 弹窗 Modal / Drawer

- **Modal**：用于重要确认、短表单
- **Drawer**：用于复杂表单、多步骤操作、侧边信息展示
- 标题使用 `title` 属性，操作按钮右对齐
- 禁止嵌套弹窗（特殊情况需 UX 审批）

```vue
<n-modal v-model:show="showModal" preset="dialog" title="确认删除？">
  <template #action>
    <n-button @click="showModal = false">取消</n-button>
    <n-button type="error" @click="handleDelete">删除</n-button>
  </template>
</n-modal>
```

---

### 5.5 导航与菜单

- 顶部导航：`n-menu` + `mode="horizontal"`
- 侧边菜单：`n-menu` + `mode="vertical"`，嵌套使用 `n-submenu`
- 路由联动使用 `n-menu` 的 `value` 与 `router` 属性

```vue
<n-menu
  v-model:value="activeKey"
  mode="horizontal"
  :options="menuOptions"
  :render-label="renderMenuLabel"
/>
```

> ✅ 菜单项图标使用 `Naive UI Icon` 或 `@vicons/ionicons5` 等官方推荐图标库

---

### 5.6 通知与反馈

| 类型       | 组件                      | 使用场景                 |
| ---------- | ------------------------- | ------------------------ |
| 成功提示   | `window.$message.success` | 操作成功反馈             |
| 警告提示   | `window.$message.warning` | 非阻断性提醒             |
| 错误提示   | `window.$message.error`   | 操作失败、校验错误       |
| 加载状态   | `n-spin` / `n-skeleton`   | 数据加载中               |
| 全局通知   | `window.$notification`    | 系统级消息、跨页面提醒   |
| 确认对话框 | `window.$dialog.confirm`  | 删除、发布等高危操作确认 |

> ⚠️ 避免滥用通知，同一操作只提示一次

---

## 六、交互与动效规范

### 6.1 动画原则

- 使用 Naive UI 内置过渡动画（如 `n-collapse-transition`）
- 页面切换使用 `n-page-header` + `n-breadcrumb` + `fade/zoom` 过渡
- 避免自定义复杂动画，保持轻量

### 6.2 加载状态

- 按钮加载：`n-button :loading="true"`
- 区块加载：`n-skeleton` 占位
- 全屏加载：`n-spin` + `wrapper` 模式

```vue
<n-button :loading="submitting" type="primary">提交中...</n-button>
```

---

## 七、响应式设计

- 移动端优先，使用 `n-grid` + `responsive="screen"`
- 隐藏非核心元素：`n-responsive-observe` 或 CSS `@media`
- 表单在移动端垂直堆叠，按钮全宽
- 图标在移动端可适当放大（1.2x）

---

## 八、无障碍与可访问性（a11y）

- 所有交互元素必须有 `aria-label` 或文字内容
- 表单控件必须关联 `label`（使用 `n-form-item` 自动绑定）
- 颜色对比度 ≥ 4.5:1（使用 [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/)）
- 支持键盘 Tab 导航、Enter/Space 触发
- 图片必须有 `alt` 属性

---

## 九、代码结构与工程规范

### 9.1 组件命名

- 页面组件：`PascalCase`，如 `UserList.vue`
- 业务组件：`TheHeader.vue`, `BaseTable.vue`
- 工具组件：`WithLoading.vue`, `FormWrapper.vue`

### 9.2 样式规范

- 使用 `<style scoped>` 或 CSS Modules
- 避免 `/deep/` 或 `:global()`，除非必要
- 自定义样式前缀：`.app-xxx {}`

### 9.3 国际化

- 使用 `n-config-provider` + `vue-i18n`
- 文案通过 `$t('key')` 调用，避免硬编码中文

```vue
<n-button>{{ $t('common.submit') }}</n-button>
```

---

## 十、设计资产与交付物

| 类型           | 格式/工具             | 说明                     |
| -------------- | --------------------- | ------------------------ |
| 设计稿         | Figma / Sketch        | 标注颜色、间距、组件状态 |
| 组件库文档     | Storybook / VitePress | 展示组件用法与 Props     |
| Token 变量     | JSON / SCSS           | 与设计稿同步的设计令牌   |
| 交互原型       | Axure / ProtoPie      | 复杂交互动效演示         |
| 无障碍审计报告 | Axe / Lighthouse      | 定期生成并修复           |

---

## 十一、版本与更新机制

- 每季度 review 一次规范文档
- 组件变更需同步更新设计系统 Figma 文件
- 新组件引入需经过前端 + UX 双审
- 使用 Changeset 或 Changelog 管理更新日志

---

## 附录 A：常用全局配置示例

```ts
// theme.ts
import { createTheme } from "naive-ui";

export const customTheme = createTheme({
  common: {
    primaryColor: "#2080F0FF",
    primaryColorHover: "#3090FFFF",
    primaryColorPressed: "#1070E0FF",
  },
  Button: {
    borderRadius: "4px",
  },
});
```

```ts
// main.ts
import { createApp } from "vue";
import { createDiscreteApi } from "naive-ui";

const { message, notification, dialog, loadingBar } = createDiscreteApi([
  "message",
  "notification",
  "dialog",
  "loadingBar",
]);

app.config.globalProperties.$message = message;
app.config.globalProperties.$notification = notification;
app.config.globalProperties.$dialog = dialog;
app.config.globalProperties.$loadingBar = loadingBar;
```

---

## 附录 B：禁用行为清单 ❌

- 禁止直接修改 `node_modules/naive-ui` 样式
- 禁止在业务组件中使用 `!important`
- 禁止脱离栅格系统手动 `float` / `position: absolute` 布局
- 禁止在同一个页面使用多个主题配置
- 禁止忽略表单校验与错误提示

---

## 附录 C：推荐工具链

- UI 设计：Figma（使用 Naive UI Design Kit）
- 代码格式化：Prettier + ESLint + Stylelint
- 组件文档：VitePress + @vitepress/plugin-naive-ui
- 主题切换：naive-ui-theme-editor（官方主题编辑器）
- 性能监控：Vue DevTools + Lighthouse
