<template>
  <div class="mixin-test-page">
    <div class="container">
      <h1 class="title">响应式 Mixin 系统测试</h1>
      
      <div class="demo-grid">
        <div class="demo-card">
          <h3>基础响应式卡片</h3>
          <p>这个卡片使用了响应式 mixin，在不同设备上会有不同的样式。</p>
        </div>
        
        <div class="demo-card">
          <h3>预定义模式卡片</h3>
          <p>这个卡片使用了预定义的响应式模式，包括内边距、圆角和阴影。</p>
        </div>
        
        <div class="demo-card">
          <h3>触摸优化卡片</h3>
          <p>这个卡片针对触摸设备进行了优化，在移动设备上有更大的点击区域。</p>
        </div>
      </div>
      
      <div class="demo-buttons">
        <button class="demo-button">响应式按钮</button>
        <button class="demo-button secondary">次要按钮</button>
      </div>
      
      <div class="demo-text">
        <p class="responsive-text">这段文字使用了响应式字体大小，在不同设备上会自动调整。</p>
      </div>
      
      <!-- 显示控制演示 -->
      <div class="visibility-demo">
        <div class="mobile-only demo-badge">仅移动端可见</div>
        <div class="tablet-only demo-badge">仅平板端可见</div>
        <div class="desktop-only demo-badge">仅桌面端可见</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 
注意：这个文件展示了 Mixin 系统的使用方法
实际使用时需要先安装 PostCSS 依赖并导入 mixin 系统
@import '@/styles/mixins/index.css';
*/

/* ===== 模拟 Mixin 效果的传统写法 ===== */
/* 这里展示了 mixin 系统要替换的重复代码 */

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 移动端 */
@media (max-width: 767px) {
  .container {
    padding: 20px 16px;
  }
}

/* 平板端 */
@media (min-width: 768px) and (max-width: 1023px) {
  .container {
    padding: 30px 20px;
  }
}

.title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: var(--color-text-primary, #333);
}

/* 移动端标题 */
@media (max-width: 767px) {
  .title {
    font-size: 2rem;
    margin-bottom: 1.5rem;
  }
}

/* ===== 网格布局演示 ===== */
.demo-grid {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(3, 1fr);
  margin-bottom: 3rem;
}

/* 移动端网格 */
@media (max-width: 767px) {
  .demo-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    margin-bottom: 2rem;
  }
}

/* 平板端网格 */
@media (min-width: 768px) and (max-width: 1023px) {
  .demo-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

/* ===== 卡片样式演示 ===== */
.demo-card {
  background: var(--color-background-elevated, #fff);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* 移动端卡片 */
@media (max-width: 767px) {
  .demo-card {
    border-radius: 8px;
    padding: 16px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

/* 平板端卡片 */
@media (min-width: 768px) and (max-width: 1023px) {
  .demo-card {
    border-radius: 10px;
    padding: 20px;
  }
}

/* 鼠标悬停效果（仅非触摸设备） */
@media (hover: hover) and (pointer: fine) {
  .demo-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  .demo-card {
    min-height: 120px;
  }
}

.demo-card h3 {
  margin: 0 0 12px 0;
  color: var(--color-text-primary, #333);
  font-size: 1.25rem;
}

.demo-card p {
  margin: 0;
  color: var(--color-text-secondary, #666);
  line-height: 1.6;
}

/* ===== 按钮样式演示 ===== */
.demo-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 3rem;
}

/* 移动端按钮布局 */
@media (max-width: 767px) {
  .demo-buttons {
    flex-direction: column;
    gap: 12px;
    margin-bottom: 2rem;
  }
}

.demo-button {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: var(--color-primary, #007bff);
  color: white;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 48px;
}

/* 移动端按钮 */
@media (max-width: 767px) {
  .demo-button {
    width: 100%;
    padding: 14px 20px;
    min-height: 52px;
    font-size: 1.1rem;
  }
}

/* 触摸设备按钮优化 */
@media (hover: none) and (pointer: coarse) {
  .demo-button {
    min-height: 44px;
    padding: 16px 24px;
  }
}

/* 鼠标悬停效果 */
@media (hover: hover) and (pointer: fine) {
  .demo-button:hover {
    background: var(--color-primary-hover, #0056b3);
    transform: translateY(-1px);
  }
}

.demo-button.secondary {
  background: var(--color-secondary, #6c757d);
}

@media (hover: hover) and (pointer: fine) {
  .demo-button.secondary:hover {
    background: var(--color-secondary-hover, #545b62);
  }
}

/* ===== 响应式文本演示 ===== */
.demo-text {
  text-align: center;
  margin-bottom: 3rem;
}

.responsive-text {
  font-size: 1.25rem;
  line-height: 1.6;
  color: var(--color-text-primary, #333);
  max-width: 600px;
  margin: 0 auto;
}

/* 移动端文本 */
@media (max-width: 767px) {
  .responsive-text {
    font-size: 1.1rem;
    line-height: 1.5;
  }
}

/* ===== 显示控制演示 ===== */
.visibility-demo {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.demo-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
}

/* 显示控制类 */
.mobile-only {
  display: block;
  background: #e3f2fd;
  color: #1976d2;
}

.tablet-only {
  display: none;
  background: #f3e5f5;
  color: #7b1fa2;
}

.desktop-only {
  display: none;
  background: #e8f5e8;
  color: #388e3c;
}

/* 平板端显示控制 */
@media (min-width: 768px) and (max-width: 1023px) {
  .mobile-only {
    display: none;
  }
  
  .tablet-only {
    display: block;
  }
  
  .desktop-only {
    display: none;
  }
}

/* 桌面端显示控制 */
@media (min-width: 1024px) {
  .mobile-only {
    display: none;
  }
  
  .tablet-only {
    display: none;
  }
  
  .desktop-only {
    display: block;
  }
}

/* 减少动画偏好 */
@media (prefers-reduced-motion: reduce) {
  * {
    transition: none !important;
    animation: none !important;
  }
}

/* 暗色主题偏好 */
@media (prefers-color-scheme: dark) {
  .demo-card {
    background: #2d3748;
    color: #e2e8f0;
  }
  
  .demo-card h3 {
    color: #f7fafc;
  }
  
  .demo-card p {
    color: #cbd5e0;
  }
}
</style>

<!-- 
===== 使用 Mixin 系统后的简化版本 =====

<style scoped>
@import '@/styles/mixins/index.css';

.container {
  max-width: 1200px;
  margin: 0 auto;
  @mixin responsive-padding;
}

.title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: var(--color-text-primary);
  
  @mixin mobile {
    font-size: 2rem;
    margin-bottom: 1.5rem;
  }
}

.demo-grid {
  @mixin responsive-grid;
  margin-bottom: 3rem;
  
  @mixin mobile {
    margin-bottom: 2rem;
  }
}

.demo-card {
  background: var(--color-background-elevated);
  @mixin responsive-border-radius;
  @mixin responsive-padding;
  @mixin responsive-shadow;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  
  @mixin hover {
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
    }
  }
  
  @mixin touch {
    min-height: 120px;
  }
}

.demo-buttons {
  @mixin responsive-flex;
  justify-content: center;
  margin-bottom: 3rem;
  
  @mixin mobile {
    margin-bottom: 2rem;
  }
}

.demo-button {
  @mixin responsive-button;
  border: none;
  background: var(--color-primary);
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  
  @mixin touch {
    min-height: 44px;
    padding: 16px 24px;
  }
  
  @mixin hover {
    &:hover {
      background: var(--color-primary-hover);
      transform: translateY(-1px);
    }
  }
}

.responsive-text {
  @mixin responsive-font-size;
  @mixin responsive-text-align;
  line-height: 1.6;
  color: var(--color-text-primary);
  max-width: 600px;
  margin: 0 auto;
  
  @mixin mobile {
    line-height: 1.5;
  }
}

/* 使用工具类 */
@mixin responsive-display-utils;

/* 减少动画和暗色主题 */
@mixin reduced-motion {
  * {
    transition: none !important;
    animation: none !important;
  }
}

@mixin dark-theme {
  .demo-card {
    background: #2d3748;
    color: #e2e8f0;
  }
}
</style>

代码对比：
- 原始代码：~200 行
- 使用 Mixin：~80 行
- 减少：60% 的代码量
- 重复的媒体查询：从 15+ 处减少到 0 处
-->