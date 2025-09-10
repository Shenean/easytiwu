import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Upload from '../pages/Upload.vue'
import Bank from '../pages/Bank.vue'
import Content from '../pages/Content.vue'

const routes: RouteRecordRaw[] = [
    { path: '/', redirect: '/upload' },
    { path: '/upload', name: 'upload', component: Upload },
    { path: '/bank', name: 'bank', component: Bank },
    { path: '/content/:bankId/:type', name: 'content', component: Content, props: true }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router