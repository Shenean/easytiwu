import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Upload from '../pages/Upload.vue'
import Bank from '../pages/Bank.vue'

const routes: RouteRecordRaw[] = [
    { path: '/', redirect: '/upload' },
    { path: '/upload', name: 'upload', component: Upload },
    { path: '/bank', name: 'bank', component: Bank }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router