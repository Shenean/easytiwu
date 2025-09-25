<template>
  <div>
    <t-header class="navbar">
      <t-space class="navbar-content" align="center">
        <div class="logo">
          <img src="/EasyTiwu.png" alt="EasyTiwu" />
        </div>
        <t-head-menu
          :value="currentRouteName"
          theme="light"
          @change="handleNavigate"
        >
          <t-menu-item value="bank">
            <template #icon>
              <BookIcon />
            </template>
            {{ t("navigation.bank") }}
          </t-menu-item>
          <t-menu-item value="statistics">
            <template #icon>
              <ChartIcon />
            </template>
            {{ t("navigation.statistics") }}
          </t-menu-item>
          <t-menu-item value="settings">
            <template #icon>
              <SettingIcon />
            </template>
            {{ t("navigation.settings") }}
          </t-menu-item>
        </t-head-menu>
      </t-space>
    </t-header>
    <t-divider />
  </div>
</template>

<script setup lang="ts">
import {ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {BookIcon, ChartIcon, SettingIcon} from "tdesign-icons-vue-next";

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const currentRouteName = ref(route.name?.toString() || "bank");

watch(
  () => route.name,
  (newName) => {
    currentRouteName.value = newName?.toString() || "bank";
  }
);

const handleNavigate = (active: string) => {
  const keyStr = active.toString();
  if (keyStr === currentRouteName.value) return;
  router
    .push({ name: keyStr })
    .then(() => {
      currentRouteName.value = keyStr;
    })
    .catch((err) => {
      if (err.name !== "NavigationDuplicated") console.error(err);
    });
};
</script>

<style scoped>
/* 保留最小布局调整，避免图标和菜单拥挤 */
.navbar-content {
  width: 100%;
  justify-content: space-between;
}

.logo img {
  height: 32px;
  object-fit: contain;
  user-select: none;
}

@media (max-width: 768px) {
  .logo img {
    height: 28px;
  }
}
</style>
