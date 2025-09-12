import { createApp } from 'vue'
import router from './router'
import App from './App.vue'

const app = createApp(App)

import naive from 'naive-ui'
app.use(naive)
app.use(router)
app.mount('#app')