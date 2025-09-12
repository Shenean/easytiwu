<!-- src/components/common/ErrorAlert.vue -->
<template>
  <n-alert v-if="msg" type="error" :title="msg" show-icon />
</template>

<script setup lang="ts">
import { defineProps } from 'vue'
const props = defineProps<{ msg?: string | null }>()
const msg = props.msg ?? null
</script>
