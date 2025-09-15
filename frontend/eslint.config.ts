// eslint.config.js
import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";
import {defineConfig} from "eslint/config";

export default defineConfig([
  // 1. JS 基础规则
  {
    files: ["**/*.{js,mjs,cjs,ts,mts,cts,vue}"],
    ...js.configs.recommended,
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
      },
    },
  },

  // 2. TypeScript 规则
  ...tseslint.configs.recommended,

  // 3. Vue 3 推荐规则（比 essential 更完整）
  ...pluginVue.configs["flat/vue3-recommended"],

  // 4. 为 .vue 文件中的 <script> 块指定 TS 解析器
  {
    files: ["**/*.vue"],
    languageOptions: {
      parserOptions: {
        parser: tseslint.parser,
      },
    },
  },

  // 5. 【可选】禁用 .d.ts 文件的 ESLint 检查（避免类型定义报错）
  {
    files: ["**/*.d.ts"],
    rules: {
      "no-unused-vars": "off",
      "no-undef": "off",
    },
  },

  // 6. 【可选】自定义规则覆盖
  {
    rules: {
      // 例如：允许 .vue 文件中使用 console.log（开发时）
      "no-console": process.env.NODE_ENV === "production" ? "error" : "off",
      "vue/multi-word-component-names": "off",
      "@typescript-eslint/no-unused-vars": [
        "warn",
        { argsIgnorePattern: "^_", varsIgnorePattern: "^_" },
      ],
    },
  },
]);
