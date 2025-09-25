import {createApp} from "vue";
import router from "./router";
import App from "./App.vue";
import "vfonts/Lato.css";
import "vfonts/FiraCode.css";
import "./styles/index.css";
import "tdesign-vue-next/es/style/index.css";
import {DialogPlugin, MessagePlugin, NotifyPlugin} from "tdesign-vue-next";
import i18n from "./i18n";
import {initTheme} from "./utils/theme";

initTheme();

const app = createApp(App);

app.config.globalProperties.$message = MessagePlugin;
app.config.globalProperties.$notification = NotifyPlugin;
app.config.globalProperties.$dialog = DialogPlugin;

app.use(router);
app.use(i18n);

app.mount("#app");

export { MessagePlugin as message, NotifyPlugin as notification, DialogPlugin as dialog };
