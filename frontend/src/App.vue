<template>
  <n-config-provider>
    <n-dialog-provider>
      <n-message-provider>
        <n-layout style="min-height: 100vh">
          <Navbar />
          <n-layout-content style="padding: 20px">
            <router-view />
          </n-layout-content>
        </n-layout>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import Navbar from './components/Navbar.vue'
import { RouterView } from 'vue-router'
</script>