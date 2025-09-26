<template>
  <div class="footer-wrapper">
    <div class="footer-content">
      <t-row :gutter="{ xs: 16, sm: 24, md: 32 }" class="footer-sections">
        <t-col :xs="24" :sm="24" :md="8" class="footer-section">
          <div class="footer-brand">
            <h3 class="brand-title">易提物</h3>
            <p class="brand-description">让提物变得更简单，让生活更便捷</p>
          </div>
        </t-col>

        <t-col :xs="12" :sm="12" :md="8" class="footer-section">
          <div class="footer-links">
            <h4 class="section-title">快速链接</h4>
            <ul class="link-list">
              <li><a href="#" class="footer-link">关于我们</a></li>
              <li><a href="#" class="footer-link">帮助中心</a></li>
              <li><a href="#" class="footer-link">隐私政策</a></li>
              <li><a href="#" class="footer-link">服务条款</a></li>
            </ul>
          </div>
        </t-col>

        <t-col :xs="12" :sm="12" :md="8" class="footer-section">
          <div class="footer-contact">
            <h4 class="section-title">联系我们</h4>
            <div class="contact-info">
              <div class="contact-item">
                <t-icon name="mail" size="16px" />
                <span>support@easytiwu.com</span>
              </div>
              <div class="contact-item">
                <t-icon name="internet" size="16px" />
                <span>www.easytiwu.com</span>
              </div>
            </div>
          </div>
        </t-col>
      </t-row>

      <t-divider class="footer-divider" />

      <div class="footer-bottom">
        <div class="copyright">
          <span>© {{ currentYear }} 易提物科技有限公司. 保留所有权利</span>
        </div>
        <div class="footer-meta">
          <span class="version">版本: v1.0.0</span>
          <t-space size="small" class="social-links">
            <a href="#" class="social-link" title="GitHub">
              <t-icon name="logo-github" size="18px" />
            </a>
            <a href="#" class="social-link" title="微信">
              <t-icon name="logo-wechat" size="18px" />
            </a>
          </t-space>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed} from "vue";

const currentYear = computed(() => new Date().getFullYear());
</script>

<style scoped>
.footer-wrapper {
  background: linear-gradient(
    135deg,
    var(--td-bg-color-container) 0%,
    var(--td-bg-color-container-hover) 100%
  );
  border-top: 1px solid var(--td-border-level-1-color);
  padding: var(--td-comp-paddingTB-xl, 32px) 0;
  margin-top: auto;
}

.footer-content {
  max-width: var(--breakpoint-xl, 1200px);
  margin: 0 auto;
  padding: 0 var(--td-comp-paddingLR-l, 16px);
}

.footer-sections {
  margin-bottom: var(--td-comp-margin-l, 24px);
}

.footer-section {
  margin-bottom: var(--td-comp-margin-m, 16px);
}

.footer-brand .brand-title {
  font-size: var(--td-font-size-title-medium, 20px);
  font-weight: 600;
  color: var(--td-text-color-primary);
  margin: 0 0 var(--td-comp-margin-s, 8px) 0;
  background: linear-gradient(
    45deg,
    var(--td-brand-color),
    var(--td-brand-color-light)
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-brand .brand-description {
  font-size: var(--td-font-size-body-medium, 14px);
  color: var(--td-text-color-secondary);
  line-height: 1.6;
  margin: 0;
}

.section-title {
  font-size: var(--td-font-size-title-small, 16px);
  font-weight: 600;
  color: var(--td-text-color-primary);
  margin: 0 0 var(--td-comp-margin-m, 16px) 0;
}

.link-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.link-list li {
  margin-bottom: var(--td-comp-margin-s, 8px);
}

.footer-link {
  color: var(--td-text-color-secondary);
  text-decoration: none;
  font-size: var(--td-font-size-body-medium, 14px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.footer-link:hover {
  color: var(--td-brand-color);
  transform: translateX(4px);
}

.footer-link::before {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(
    90deg,
    var(--td-brand-color),
    var(--td-brand-color-light)
  );
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.footer-link:hover::before {
  width: 100%;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: var(--td-comp-margin-s, 8px);
}

.contact-item {
  display: flex;
  align-items: center;
  gap: var(--td-comp-margin-s, 8px);
  color: var(--td-text-color-secondary);
  font-size: var(--td-font-size-body-medium, 14px);
}

.contact-item .t-icon {
  color: var(--td-brand-color);
  flex-shrink: 0;
}

.footer-divider {
  margin: var(--td-comp-margin-l, 24px) 0 var(--td-comp-margin-m, 16px) 0;
  border-color: var(--td-border-level-2-color);
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--td-comp-margin-m, 16px);
}

.copyright {
  color: var(--td-text-color-placeholder);
  font-size: var(--td-font-size-body-small, 12px);
}

.footer-meta {
  display: flex;
  align-items: center;
  gap: var(--td-comp-margin-m, 16px);
}

.version {
  color: var(--td-text-color-placeholder);
  font-size: var(--td-font-size-body-small, 12px);
  font-family: var(
    --td-font-family-mono,
    "SF Mono",
    Monaco,
    "Cascadia Code",
    "Roboto Mono",
    Consolas,
    "Courier New",
    monospace
  );
}

.social-links {
  display: flex;
  gap: var(--td-comp-margin-s, 8px);
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--td-radius-circle);
  background: var(--td-bg-color-container-hover);
  color: var(--td-text-color-secondary);
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid var(--td-border-level-1-color);
}

.social-link:hover {
  background: var(--td-brand-color);
  color: var(--td-text-color-anti);
  border-color: var(--td-brand-color);
  transform: translateY(-2px);
  box-shadow: var(--td-shadow-2);
}

@media (max-width: 768px) {
  .footer-wrapper {
    padding: var(--td-comp-paddingTB-l, 24px) 0;
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
    gap: var(--td-comp-margin-s, 8px);
  }

  .footer-meta {
    flex-direction: column;
    gap: var(--td-comp-margin-s, 8px);
  }
}

@media (max-width: 480px) {
  .footer-brand .brand-title {
    font-size: var(--td-font-size-title-small, 16px);
  }

  .section-title {
    font-size: var(--td-font-size-body-large, 14px);
  }

  .social-link {
    width: 32px;
    height: 32px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .footer-link,
  .footer-link::before,
  .social-link {
    transition: none;
  }

  .footer-link:hover {
    transform: none;
  }

  .social-link:hover {
    transform: none;
  }
}
</style>
