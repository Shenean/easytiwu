import {createApp} from 'vue'
import router from './router'
import App from './App.vue'
import 'vfonts/Lato.css'
import 'vfonts/FiraCode.css'
import './styles/index.css'
import naive from 'naive-ui'

// 创建应用实例
const app = createApp(App)

// 注册插件
app.use(naive)
app.use(router)

// 挂载应用
app.mount('#app')