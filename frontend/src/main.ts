import {createApp} from "vue";
import router from "./router";
import App from "./App.vue";
import "vfonts/Lato.css";
import "vfonts/FiraCode.css";
import "./styles/index.css";
import naive, {createDiscreteApi, dateZhCN, zhCN} from "naive-ui";
import i18n from "./i18n";

// 创建离散式 API 实例
const { message, notification, dialog, loadingBar } = createDiscreteApi(
  ["message", "notification", "dialog", "loadingBar"],
  {
    configProviderProps: {
      theme: null,
      locale: zhCN,
      dateLocale: dateZhCN,
    },
  }
);

// 创建应用实例
const app = createApp(App);

// 将离散式 API 挂载到全局属性
app.config.globalProperties.$message = message;
app.config.globalProperties.$notification = notification;
app.config.globalProperties.$dialog = dialog;
app.config.globalProperties.$loadingBar = loadingBar;

// 注册插件
app.use(naive);
app.use(router);
app.use(i18n);

// 挂载应用
app.mount("#app");

// 导出离散式 API 供组合式 API 使用
export { message, notification, dialog, loadingBar };
