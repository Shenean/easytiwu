import {createRouter, createWebHistory, type RouteRecordRaw,} from "vue-router";
import BankPage from "../pages/BankPage.vue";
import ContentPage from "../pages/ContentPage.vue";
import StatisticsPage from "../pages/StatisticsPage.vue";
import SettingsPage from "../pages/SettingsPage.vue";
import LoginPage from "../pages/LoginPage.vue";
import RegisterPage from "../pages/RegisterPage.vue";

const routes: RouteRecordRaw[] = [
  { path: "/", redirect: "/bank" },
  { path: "/login", name: "login", component: LoginPage },
  { path: "/register", name: "register", component: RegisterPage },
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