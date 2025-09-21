import {createApp} from "vue";
import router from "./router";
import App from "./App.vue";
import "vfonts/Lato.css";
import "vfonts/FiraCode.css";
import "./styles/index.css";
import {createDiscreteApi, dateZhCN, zhCN} from "naive-ui";
import i18n from "./i18n";

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

const app = createApp(App);

app.config.globalProperties.$message = message;
app.config.globalProperties.$notification = notification;
app.config.globalProperties.$dialog = dialog;
app.config.globalProperties.$loadingBar = loadingBar;

app.use(router);
app.use(i18n);

app.mount("#app");

export { message, notification, dialog, loadingBar };
