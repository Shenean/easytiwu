<template>
  <PageContainer
    :title="t('statistics.title')"
    card-class="stats-card"
    container-class="statistics-container"
  >
    <!-- 加载状态 -->
    <div
      v-if="loading"
      class="loading-container"
      style="
        height: 400px;
        display: flex;
        align-items: center;
        justify-content: center;
      "
    >
      <n-spin size="large">
        <template #description>
          {{ t("statistics.loading") }}
        </template>
      </n-spin>
    </div>

    <!-- 内容区域 -->
    <template v-if="!loading">
      <!-- 错误提示 -->
      <n-alert
        v-if="error"
        type="error"
        :title="error"
        style="margin-bottom: 16px"
      />

      <!-- 统计数据内容 -->
      <div v-else-if="stats">
        <!-- 总览统计 -->
        <n-card class="overview-summary-card" size="small">
          <n-grid
            :cols="overviewGridCols"
            :x-gap="24"
            :y-gap="24"
            class="overview-grid"
          >
            <n-grid-item>
              <div class="overview-item">
                <n-statistic
                  :label="t('statistics.bankTotal')"
                  :value="formatNumber(stats.bankTotal)"
                />
              </div>
            </n-grid-item>
            <n-grid-item>
              <div class="overview-item">
                <n-statistic
                  :label="t('statistics.questionTotal')"
                  :value="formatNumber(stats.questionTotal)"
                />
              </div>
            </n-grid-item>
          </n-grid>
        </n-card>

        <!-- 题型统计 -->
        <div class="type-stats-section">
          <n-grid
            :cols="typeStatsGridCols"
            :x-gap="16"
            :y-gap="16"
            class="type-stats-grid"
          >
            <n-grid-item v-for="stat in typeStats" :key="stat.originalType">
              <n-card class="type-stat-card" size="small">
                <div class="type-stat-header">
                  <h4 class="type-title">{{ stat.type }}</h4>
                  <n-tag type="info" size="small" class="percentage-tag">
                    {{ stat.percentage }}%
                  </n-tag>
                </div>

                <div class="type-stat-content">
                  <n-grid
                    :cols="metricsGridCols"
                    :x-gap="16"
                    :y-gap="8"
                    class="stats-metrics"
                  >
                    <n-grid-item>
                      <div class="metric-item">
                        <div class="metric-value">{{ stat.total }}</div>
                        <div class="metric-label">
                          {{ t("statistics.totalQuestions") }}
                        </div>
                      </div>
                    </n-grid-item>
                    <n-grid-item>
                      <div class="metric-item">
                        <div class="metric-value">{{ stat.completed }}</div>
                        <div class="metric-label">
                          {{ t("statistics.completed") }}
                        </div>
                      </div>
                    </n-grid-item>
                    <n-grid-item>
                      <div class="metric-item">
                        <div class="metric-value">{{ stat.accuracy }}%</div>
                        <div class="metric-label">
                          {{ t("statistics.accuracy") }}
                        </div>
                      </div>
                    </n-grid-item>
                  </n-grid>
                </div>
              </n-card>
            </n-grid-item>
          </n-grid>
        </div>
      </div>

      <!-- 空状态 -->
      <n-empty v-else :description="t('statistics.noData')" size="small" />
    </template>

    <!-- 刷新按钮 -->
    <template #action>
      <n-button
        @click="refreshData"
        :loading="loading"
        type="primary"
        size="small"
      >
        {{ t("statistics.refresh") }}
      </n-button>
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import {NGrid, NGridItem} from "naive-ui";
import {useI18n} from "vue-i18n";

import {statisticsAPI} from "../api/config";
import PageContainer from "../components/common/PageContainer.vue";

import {useBreakpoints} from "../composables/useBreakpoints";
import type {ApiResponse, StatisticsOverview, TypeStats,} from "../types/statistics";

// 数字格式化函数，防御性处理null/undefined
const formatNumber = (value: number | null | undefined): string => {
  return (value ?? 0).toLocaleString();
};

const { t } = useI18n();

// 题型字段映射
const getTypeDisplayName = (type: string): string => {
  const typeMap: Record<string, string> = {
    single: t("statistics.questionTypes.single"),
    fill_blank: t("statistics.questionTypes.fillBlank"),
    true_false: t("statistics.questionTypes.trueFalse"),
    multiple: t("statistics.questionTypes.multiple"),
    short_answer: t("statistics.questionTypes.shortAnswer"),
  };
  return typeMap[type] || type;
};

// 响应式数据
const stats = ref<StatisticsOverview | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

// 响应式布局
const { isMobile, isTablet } = useBreakpoints();

// 网格列数计算
const overviewGridCols = computed(() => {
  if (isMobile.value) return 1;
  return 2;
});

const typeStatsGridCols = computed(() => {
  if (isMobile.value) return 1;
  if (isTablet.value) return 2;
  return 3;
});

const metricsGridCols = computed(() => {
  if (isMobile.value) return 1;
  return 3;
});

// 题型统计卡片数据
const typeStats = computed(() => {
  if (!stats.value?.byType) return [];

  return Object.entries(stats.value.byType).map(
    ([type, v]: [string, TypeStats]) => {
      const total = v.count ?? 0;
      const completed = v.completedCount ?? 0;
      const correct = v.correctCount ?? 0;
      const percentage = total > 0 ? Math.round((completed / total) * 100) : 0;
      const accuracy =
        completed > 0 ? Math.round((correct / completed) * 100) : 0;

      return {
        type: getTypeDisplayName(type),
        originalType: type,
        total,
        completed,
        correct,
        percentage,
        accuracy,
      };
    }
  );
});

// 获取统计数据
const fetchStatistics = async () => {
  try {
    loading.value = true;
    error.value = null;

    const response = await statisticsAPI.getOverview();

    // 可能的后端包装：{ success, data } 或 直接对象
    const body = response.data;
    if (body && typeof body === "object") {
      const apiResp = body as ApiResponse<StatisticsOverview>;
      if (apiResp.success && apiResp.data) {
        stats.value = apiResp.data;
      } else if (typeof body.bankTotal === "number") {
        // 若后端直接返回 overview 对象
        stats.value = body as StatisticsOverview;
      } else {
        throw new Error(apiResp.message || t("statistics.errors.fetchFailed"));
      }
    } else {
      throw new Error(t("statistics.errors.dataFormat"));
    }
  } catch (err: unknown) {
    console.error("获取统计数据失败:", err);

    if (err && typeof err === "object" && "response" in err) {
      const axiosErr = err as {
        response: { status: number; data?: { message?: string } };
        message?: string;
      };
      const status = axiosErr.response.status;
      const msg = axiosErr.response.data?.message || axiosErr.message;
      switch (status) {
        case 404:
          error.value = t("statistics.errors.serviceUnavailable");
          break;
        case 500:
          error.value = t("statistics.errors.serverError", { message: msg });
          break;
        case 503:
          error.value = t("statistics.errors.serviceTemporarilyUnavailable");
          break;
        default:
          error.value = t("statistics.errors.requestFailed", {
            status,
            message: msg,
          });
      }
    } else if (
      err &&
      typeof err === "object" &&
      ("code" in err || "message" in err)
    ) {
      const networkErr = err as { code?: string; message?: string };
      if (
        networkErr.code === "ECONNREFUSED" ||
        (networkErr.message && networkErr.message.includes("Network Error"))
      ) {
        error.value = t("statistics.errors.connectionFailed");
      } else {
        error.value = networkErr.message || t("statistics.errors.unknownError");
      }
    } else {
      error.value = t("statistics.errors.unknownError");
    }
  } finally {
    loading.value = false;
  }
};

// 刷新数据
const refreshData = () => {
  fetchStatistics();
};

// 组件挂载时获取数据
onMounted(() => {
  fetchStatistics();
});
</script>

<style scoped>
.statistics-container {
  max-width: var(--container-max-width-md);
  margin: 0 auto;
  padding: var(--spacing-md);
  width: 100%;
  box-sizing: border-box;
}

.stats-card {
  border-radius: var(--card-border-radius-desktop);
  box-shadow: var(--card-shadow-medium);
  border: none;
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  transition: box-shadow 0.3s ease;
  box-sizing: border-box;
}

.stats-card:hover {
  box-shadow: var(--card-shadow-medium);
}

/* 总览统计样式 */
.overview-summary-card {
  margin-bottom: var(--spacing-md);
  background: linear-gradient(
    135deg,
    var(--n-color) 0%,
    var(--n-color-embedded) 100%
  );
  border: none;
  border-radius: var(--card-border-radius);
  box-shadow: var(--card-unified-shadow);
  width: var(--card-standard-width);
  max-width: var(--card-content-max-width);
  margin: 0 auto var(--spacing-md) auto;
  transition: box-shadow 0.3s ease;
}

.overview-summary-card:hover {
  box-shadow: var(--card-unified-shadow-hover);
}

.overview-grid {
  margin: 0;
}

.overview-item {
  text-align: center;
  padding: var(--spacing-2);
}

/* 题型统计区域 */
.type-stats-section {
  margin-top: 0;
}

.type-stats-grid {
  margin: 0;
}

.type-stat-card {
  border: none;
  border-radius: var(--card-border-radius);
  box-shadow: var(--card-unified-shadow);
  transition: all 0.3s ease;
  background: var(--n-color);
  width: var(--card-standard-width);
  margin: 0 auto;
}

.type-stat-card:hover {
  box-shadow: var(--card-unified-shadow-hover);
  transform: translateY(calc(-1 * var(--spacing-1)));
}

.type-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-3);
  padding-bottom: var(--spacing-2);
  border-bottom: 1px solid var(--n-divider-color);
}

.type-title {
  margin: 0;
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--n-text-color-1);
}

.percentage-tag {
  font-weight: 600;
}

.type-stat-content {
  padding: 0;
}

.stats-metrics {
  margin-bottom: 0;
}

.metric-item {
  text-align: center;
  padding: var(--spacing-2) var(--spacing-1); /* 8px 4px */
}

.metric-value {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--n-primary-color);
  margin-bottom: var(--spacing-1); /* 4px */
  line-height: 1.1;
}

.metric-label {
  font-size: var(--font-size-xs);
  color: var(--n-text-color-2);
  font-weight: 500;
  line-height: 1.2;
}

/* 统计数字样式优化 */
:deep(.n-statistic-value) {
  font-weight: 700;
  color: var(--n-primary-color);
  font-size: var(--font-size-2xl);
}

:deep(.n-statistic-label) {
  color: var(--n-text-color-2);
  font-weight: 600;
  font-size: var(--font-size-xs);
  margin-bottom: var(--spacing-1);
}

/* 紧凑卡片内边距 */
:deep(.overview-summary-card .n-card__content) {
  padding: var(--card-padding-desktop);
}

:deep(.type-stat-card .n-card__content) {
  padding: var(--card-padding-md);
}

/* 进度条样式优化 */
:deep(.n-progress .n-progress-graph .n-progress-graph-line-fill) {
  transition: all 0.3s ease;
}

/* 加载状态样式 */
:deep(.n-spin-content) {
  min-height: var(--spacing-50); /* 50 * 4px */
}

/* 移动端适配 */
@media (max-width: 1023px) {
  .statistics-container {
    padding: var(--container-responsive-padding-tablet);
    max-width: 100%;
  }

  .stats-card {
    border-radius: var(--card-border-radius-tablet);
    box-shadow: var(--card-shadow-tablet);
    width: 100%;
    max-width: 100%;
  }
}

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .statistics-container {
    padding: var(--spacing-1) var(--spacing-2);
  }

  .stats-card {
    border-radius: var(--spacing-2);
  }

  .overview-summary-card,
  .type-stat-card {
    padding: var(--spacing-2);
  }

  .grid {
    gap: var(--spacing-2);
  }

  .metric-item {
    padding: var(--spacing-2);
  }
}

/* 平板端布局优化 */
@media (max-width: var(--breakpoint-tablet)) {
  .overview-summary-card {
    width: 100%;
    box-shadow: var(--card-shadow-tablet);
  }

  .type-stat-card {
    width: 100%;
    box-shadow: var(--card-shadow-tablet);
  }

  .overview-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-3);
  }

  .overview-item {
    padding: var(--spacing-2);
  }

  .type-stats-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-2);
  }

  .stats-metrics {
    grid-template-columns: 1fr;
    gap: var(--spacing-2);
    margin-bottom: 0;
  }

  .metric-item {
    padding: var(--spacing-2);
    background: var(--n-color-embedded);
    border-radius: var(--border-radius-sm);
    border: 1px solid var(--n-border-color);
  }

  .metric-value {
    font-size: var(--font-size-base);
  }

  .type-stat-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-2); /* 8px */
    margin-bottom: var(--spacing-sm);
    padding-bottom: var(--spacing-2);
  }

  .type-title {
    font-size: var(--font-size-sm);
  }

  :deep(.overview-summary-card .n-card__content) {
    padding: var(--spacing-3);
  }

  :deep(.type-stat-card .n-card__content) {
    padding: var(--spacing-3);
  }

  :deep(.n-statistic-value) {
    font-size: var(--font-size-lg);
  }
}

@media (max-width: var(--breakpoint-mobile)) {
  .statistics-container {
    padding: var(--spacing-2) var(--spacing-3);
  }

  .stats-card {
    border-radius: var(--spacing-2);
    box-shadow: var(--card-shadow-medium);
    width: 100%;
    max-width: 100%;
  }

  .overview-summary-card {
    width: 100%;
    box-shadow: var(--card-shadow-medium);
  }

  .type-stat-card {
    width: 100%;
    box-shadow: var(--card-shadow-medium);
  }

  .overview-grid {
    gap: var(--spacing-2);
  }

  .overview-item {
    padding: var(--spacing-2);
  }

  .type-stats-grid {
    gap: var(--spacing-2);
  }

  .stats-metrics {
    gap: var(--spacing-2);
  }

  .metric-item {
    padding: var(--spacing-2);
  }

  .metric-value {
    font-size: var(--font-size-sm);
  }

  .metric-label {
    font-size: var(--font-size-xs);
  }

  .type-title {
    font-size: var(--font-size-sm);
  }

  .type-stat-header {
    margin-bottom: var(--spacing-2); /* 8px */
    padding-bottom: var(--spacing-1); /* 4px */
  }

  :deep(.overview-summary-card .n-card__content) {
    padding: var(--spacing-2);
  }

  :deep(.type-stat-card .n-card__content) {
    padding: var(--spacing-2);
  }

  :deep(.n-statistic-value) {
    font-size: var(--font-size-base);
  }

  :deep(.n-statistic-label) {
    font-size: var(--font-size-xs);
  }
}
</style>
