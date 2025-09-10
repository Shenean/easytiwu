import { createApp } from 'vue'
import router from './router'
import 'vfonts/Lato.css'
import 'vfonts/FiraCode.css'
import App from './App.vue'

const app = createApp(App)

// 引入naive-ui
import naive from 'naive-ui'
app.use(naive)

// 使用路由
app.use(router)

app.mount('#app')