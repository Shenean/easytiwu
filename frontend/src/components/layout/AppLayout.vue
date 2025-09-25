<template>
  <t-layout class="app-layout" :style="layoutStyles">
    <t-header class="app-header">
      <slot name="header">
        <NavigationBar />
      </slot>
    </t-header>

    <t-content class="app-content" :style="contentStyles">
      <div :class="containerClass">
        <slot />
      </div>
    </t-content>

    <template v-if="$slots.footer">
      <t-divider style="margin: 0;" />
      <t-footer class="app-footer">
        <slot name="footer" />
      </t-footer>
    </template>
  </t-layout>
</template>

<script setup lang="ts">
import {computed} from "vue";
import NavigationBar from "../NavigationBar.vue";

interface Props {
  containerSize?: "sm" | "md" | "lg" | "xl" | "2xl" | "full";
  contentPadding?: "none" | "sm" | "md" | "lg";
  containerClass?: string;
}

const props = withDefaults(defineProps<Props>(), {
  containerSize: "xl",
  contentPadding: "sm",

  containerClass: "",
});

const layoutStyles = computed(() => ({
  minHeight: "100vh",
}));

const contentStyles = computed(() => {
  const paddingMap = {
    none: "0",
    sm: "var(--spacing-2)",
    md: "var(--spacing-3)",
    lg: "var(--spacing-4)",
  };

  return {
    padding: paddingMap[props.contentPadding],
    paddingTop: `calc(var(--nav-height) + ${paddingMap[props.contentPadding]})`,
    marginTop: "0",
  };
});

const containerClass = computed(() => {
  const classes = ["tdesign-container"];

  if (props.containerSize !== "full") {
    classes.push(`container-${props.containerSize}`);
  }

  if (props.containerClass) {
    classes.push(props.containerClass);
  }

  return classes.join(" ");
});
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background-color: var(--app-bg-color, var(--td-bg-color-page));
}

.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--nav-height);
  background-color: var(--td-bg-color-container);
  backdrop-filter: blur(var(--spacing-1));
  -webkit-backdrop-filter: blur(var(--spacing-1));
  box-shadow: var(--shadow-sm);
}

.app-content {
  flex: 1;
  transition: padding 0.3s ease;
}

.tdesign-container {
  width: 100%;
  margin: 0 auto;
  padding-left: var(--td-comp-paddingLR-l, 12px);
  padding-right: var(--td-comp-paddingLR-l, 12px);
  transition: max-width 0.3s ease, padding 0.3s ease;
}

.tdesign-container.container-sm {
  max-width: var(--breakpoint-sm, 576px);
}

.tdesign-container.container-md {
  max-width: var(--breakpoint-md, 768px);
}

.tdesign-container.container-lg {
  max-width: var(--breakpoint-lg, 992px);
}

.tdesign-container.container-xl {
  max-width: var(--breakpoint-xl, 1200px);
}

.tdesign-container.container-2xl {
  max-width: var(--breakpoint-xxl, 1400px);
}

.app-footer {
  background-color: var(--td-bg-color-container);
}
</style>
