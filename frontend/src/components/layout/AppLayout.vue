<template>
  <t-layout class="app-layout" :style="layoutStyles">
    <!-- Header 导航栏区域 -->
    <t-header 
      v-if="showHeader"
      class="app-header" 
      :class="headerClasses" 
      :style="headerStyles"
    >
      <div class="header-container" :class="headerContainerClass">
        <slot name="header">
          <NavigationBar />
        </slot>
      </div>
    </t-header>

    <!-- Content 主体内容区域 -->
    <t-content
      class="app-content"
      :class="contentClasses"
      :style="contentStyles"
    >
      <div :class="containerClass">
        <slot />
      </div>
    </t-content>

    <!-- Footer 页脚区域 -->
    <t-footer
      v-if="showFooter"
      class="app-footer"
      :class="footerClasses"
      :style="footerStyles"
    >
      <div class="footer-container" :class="footerContainerClass">
        <slot name="footer">
          <Footer />
        </slot>
      </div>
    </t-footer>
  </t-layout>
</template>

<script setup lang="ts">
import {computed} from "vue";
import NavigationBar from "../NavigationBar.vue";
import Footer from "../Footer.vue";

interface Props {
  containerSize?: "sm" | "md" | "lg" | "xl" | "2xl" | "full";
  contentPadding?: "none" | "sm" | "md" | "lg";
  containerClass?: string;
  headerFixed?: boolean;
  headerHeight?: string;
  headerBackground?: string;
  headerShadow?: boolean;
  footerFixed?: boolean;
  footerHeight?: string;
  footerBackground?: string;
  showHeader?: boolean;
  showFooter?: boolean;
  responsive?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  containerSize: "xl",
  contentPadding: "sm",
  containerClass: "",
  headerFixed: true,
  headerHeight: "var(--nav-height, 64px)",
  headerBackground: "var(--td-bg-color-container)",
  headerShadow: true,
  footerFixed: false,
  footerHeight: "auto",
  footerBackground: "var(--td-bg-color-container)",
  showHeader: true,
  showFooter: true,
  responsive: true,
});

const layoutStyles = computed(() => ({
  minHeight: "100vh",
  display: "flex",
  flexDirection: "column",
}));

const headerClasses = computed(() => ({
  "header-fixed": props.headerFixed,
  "header-shadow": props.headerShadow,
  "header-responsive": props.responsive,
}));

const headerStyles = computed(() => ({
  height: props.headerHeight,
  backgroundColor: props.headerBackground,
}));

const headerContainerClass = computed(() => {
  const classes = ["header-content"];
  if (props.containerSize !== "full") {
    classes.push(`container-${props.containerSize}`);
  }
  return classes.join(" ");
});

const contentClasses = computed(() => ({
  "content-with-fixed-header": props.headerFixed,
  "content-with-fixed-footer": props.footerFixed,
  "content-responsive": props.responsive,
}));

const footerClasses = computed(() => ({
  "footer-fixed": props.footerFixed,
  "footer-responsive": props.responsive,
}));

const footerStyles = computed(() => ({
  height: props.footerHeight,
  backgroundColor: props.footerBackground,
}));

const footerContainerClass = computed(() => {
  const classes = ["footer-content"];
  if (props.containerSize !== "full") {
    classes.push(`container-${props.containerSize}`);
  }
  return classes.join(" ");
});

const contentStyles = computed(() => {
  const paddingMap = {
    none: "0",
    sm: "var(--spacing-2, 8px)",
    md: "var(--spacing-3, 16px)",
    lg: "var(--spacing-4, 24px)",
  };

  const styles: Record<string, string> = {
    flex: "1",
    padding: paddingMap[props.contentPadding],
    transition: "padding 0.3s ease",
  };

  if (props.showHeader && props.headerFixed) {
    styles.paddingTop = `calc(${props.headerHeight} + ${
      paddingMap[props.contentPadding]
    })`;
  }

  if (props.showFooter && props.footerFixed) {
    styles.paddingBottom = `calc(${props.footerHeight} + ${
      paddingMap[props.contentPadding]
    })`;
  }

  return styles;
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
/* 主布局容器 */
.app-layout {
  min-height: 100vh;
  background-color: var(--app-bg-color, var(--td-bg-color-page));
  display: flex;
  flex-direction: column;
}

/* Header 导航栏样式 */
.app-header {
  position: relative;
  z-index: 1000;
  background-color: var(--td-bg-color-container);
  border-bottom: 1px solid var(--td-border-level-1-color);
  transition: all 0.3s ease;
}

.app-header.header-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
}

.app-header.header-shadow {
  box-shadow: var(--td-shadow-1, 0 2px 8px rgba(0, 0, 0, 0.1));
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.header-container {
  width: 100%;
  margin: 0 auto;
  padding: 0 var(--td-comp-paddingLR-l, 16px);
  display: flex;
  align-items: center;
  min-height: inherit;
}

.header-content {
  width: 100%;
  margin: 0 auto;
  padding-left: var(--td-comp-paddingLR-l, 16px);
  padding-right: var(--td-comp-paddingLR-l, 16px);
}

/* Content 主体内容样式 */
.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  transition: padding 0.3s ease;
}

.app-content.content-with-fixed-header {
  margin-top: 0;
}

.app-content.content-with-fixed-footer {
  margin-bottom: 0;
}

/* Footer 页脚样式 */
.app-footer {
  background-color: var(--td-bg-color-container);
  border-top: 1px solid var(--td-border-level-1-color);
  margin-top: auto;
  transition: all 0.3s ease;
}

.app-footer.footer-fixed {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
}

.footer-container {
  width: 100%;
  margin: 0 auto;
  padding: var(--td-comp-paddingTB-l, 16px) var(--td-comp-paddingLR-l, 16px);
}

.footer-content {
  width: 100%;
  margin: 0 auto;
  padding-left: var(--td-comp-paddingLR-l, 16px);
  padding-right: var(--td-comp-paddingLR-l, 16px);
}

/* 容器尺寸样式 */
.tdesign-container {
  width: 100%;
  margin: 0 auto;
  padding-left: var(--td-comp-paddingLR-l, 16px);
  padding-right: var(--td-comp-paddingLR-l, 16px);
  transition: max-width 0.3s ease, padding 0.3s ease;
}

.container-sm,
.header-content.container-sm,
.footer-content.container-sm {
  max-width: var(--breakpoint-sm, 576px);
}

.container-md,
.header-content.container-md,
.footer-content.container-md {
  max-width: var(--breakpoint-md, 768px);
}

.container-lg,
.header-content.container-lg,
.footer-content.container-lg {
  max-width: var(--breakpoint-lg, 992px);
}

.container-xl,
.header-content.container-xl,
.footer-content.container-xl {
  max-width: var(--breakpoint-xl, 1200px);
}

.container-2xl,
.header-content.container-2xl,
.footer-content.container-2xl {
  max-width: var(--breakpoint-xxl, 1400px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-responsive .header-container,
  .content-responsive .tdesign-container,
  .footer-responsive .footer-container {
    padding-left: var(--td-comp-paddingLR-s, 12px);
    padding-right: var(--td-comp-paddingLR-s, 12px);
  }

  .header-responsive .header-content,
  .footer-responsive .footer-content {
    padding-left: var(--td-comp-paddingLR-s, 12px);
    padding-right: var(--td-comp-paddingLR-s, 12px);
  }
}

@media (max-width: 480px) {
  .header-responsive .header-container,
  .content-responsive .tdesign-container,
  .footer-responsive .footer-container {
    padding-left: var(--td-comp-paddingLR-xs, 8px);
    padding-right: var(--td-comp-paddingLR-xs, 8px);
  }

  .header-responsive .header-content,
  .footer-responsive .footer-content {
    padding-left: var(--td-comp-paddingLR-xs, 8px);
    padding-right: var(--td-comp-paddingLR-xs, 8px);
  }
}

/* 无障碍和性能优化 */
@media (prefers-reduced-motion: reduce) {
  .app-header,
  .app-content,
  .app-footer,
  .tdesign-container {
    transition: none;
  }
}

/* 打印样式 */
@media print {
  .app-header.header-fixed,
  .app-footer.footer-fixed {
    position: static;
  }

  .app-header.header-shadow {
    box-shadow: none;
    backdrop-filter: none;
    -webkit-backdrop-filter: none;
  }
}
</style>
