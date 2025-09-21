import {createRouter, createWebHistory, type RouteRecordRaw,} from "vue-router";
import UploadPage from "../pages/UploadPage.vue";
import BankPage from "../pages/BankPage.vue";
import ContentPage from "../pages/ContentPage.vue";
import StatisticsPage from "../pages/StatisticsPage.vue";
import SettingsPage from "../pages/SettingsPage.vue";

const routes: RouteRecordRaw[] = [
  { path: "/", redirect: "/upload" },
  { path: "/upload", name: "upload", component: UploadPage },
  { path: "/bank", name: "bank", component: BankPage },
  {
    path: "/content/:bankId/:type",
    name: "content",
    component: ContentPage,
    props: true,
  },
  { path: "/statistics", name: "statistics", component: StatisticsPage },
  { path: "/settings", name: "settings", component: SettingsPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
