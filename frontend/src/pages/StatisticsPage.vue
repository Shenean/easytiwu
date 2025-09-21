<template>
  <PageContainer :title="t('statistics.title')" :show-card="false" container-class="statistics-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container" style="
        height: 400px;
        display: flex;
        align-items: center;
        justify-content: center;
      ">
      <n-spin size="large">
        <template #description>
          {{ t("statistics.loading") }}
        </template>
      </n-spin>
    </div>

    <!-- 内容区域 -->
    <template v-if="!loading">
      <!-- 错误提示 -->
      <n-alert v-if="error" type="error" :title="error" style="margin-bottom: 16px" />

      <!-- 统计数据内容 -->
      <div v-else-if="stats">
        <!-- 总览统计 -->
        <n-card class="overview-summary-card" size="small">
          <n-grid :cols="2" :x-gap="24" :y-gap="24" class="overview-grid">
            <n-grid-item>
              <div class="overview-item">
                <n-statistic :label="t('statistics.bankTotal')" :value="formatNumber(stats.bankTotal)" />
              </div>
            </n-grid-item>
            <n-grid-item>
              <div class="overview-item">
                <n-statistic :label="t('statistics.questionTotal')" :value="formatNumber(stats.questionTotal)" />
              </div>
            </n-grid-item>
          </n-grid>
        </n-card>

        <!-- 题型统计表格 -->
        <div class="type-stats-section">
          <n-card class="stats-table-card" size="small">
            <template #header>
              <h3 class="table-title">{{ t('statistics.typeStatistics') }}</h3>
            </template>
            <n-data-table :columns="tableColumns" :data="typeStats" :pagination="false" :bordered="false" size="small"
              class="stats-table" />
          </n-card>
        </div>
      </div>

      <!-- 空状态 -->
      <n-empty v-else :description="t('statistics.noData')" size="small" />
    </template>

    <!-- 刷新按钮 -->
    <template #action>
      <n-button @click="refreshData" :loading="loading" type="primary" size="small">
        {{ t("statistics.refresh") }}
      </n-button>
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, h, onMounted, ref} from "vue";
import type {DataTableColumns} from "naive-ui";
import {NDataTable, NGrid, NGridItem, NTag} from "naive-ui";
import {useI18n} from "vue-i18n";

import {statisticsAPI} from "../api/config";
import PageContainer from "../components/common/PageContainer.vue";


import type {ApiResponse, StatisticsOverview, TypeStats,} from "../types/statistics";

/**
 * 数字格式化工具函数
 *
 * 将数字转换为本地化格式的字符串，支持千分位分隔符。
 * 提供防御性编程，安全处理 null 和 undefined 值。
 *
 * @param {number | null | undefined} value - 需要格式化的数字值
 * @returns {string} 格式化后的字符串，如 "1,234" 或 "0"
 *
 * @example
 * ```typescript
 * formatNumber(1234)      // "1,234"
 * formatNumber(null)      // "0"
 * formatNumber(undefined) // "0"
 * formatNumber(0)         // "0"
 * ```
 */
const formatNumber = (value: number | null | undefined): string => {
  return (value ?? 0).toLocaleString();
};

/** 国际化翻译函数 */
const { t } = useI18n();

/**
 * 题型显示名称映射函数
 *
 * 将后端返回的题型标识符转换为用户友好的显示名称。
 * 支持国际化，根据当前语言环境返回对应的题型名称。
 *
 * @param {string} type - 题型标识符
 * @returns {string} 本地化的题型显示名称
 *
 * @example
 * ```typescript
 * getTypeDisplayName('single')      // "单选题"
 * getTypeDisplayName('multiple')    // "多选题"
 * getTypeDisplayName('unknown')     // "unknown" (fallback)
 * ```
 */
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

// ==================== 响应式状态管理 ====================

/** 统计数据状态 */
const stats = ref<StatisticsOverview | null>(null);

/** 加载状态 */
const loading = ref(false);

/** 错误状态 */
const error = ref<string | null>(null);

// 题型统计表格数据
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

// 表格列定义
const tableColumns: DataTableColumns = [
  {
    title: t('statistics.questionType'),
    key: 'type',
    width: 120,
    render: (row: any) => {
      return row.type;
    }
  },
  {
    title: t('statistics.totalQuestions'),
    key: 'total',
    width: 100,
    align: 'center',
    render: (row: any) => {
      return formatNumber(row.total);
    }
  },
  {
    title: t('statistics.completed'),
    key: 'completed',
    width: 100,
    align: 'center',
    render: (row: any) => {
      return formatNumber(row.completed);
    }
  },
  {
    title: t('statistics.completionRate'),
    key: 'percentage',
    width: 120,
    align: 'center',
    render: (row: any) => {
      const percentage = row.percentage;
      const type = percentage >= 80 ? 'success' : percentage >= 60 ? 'warning' : 'error';
      return h(NTag, { type, size: 'small' }, { default: () => `${percentage}%` });
    }
  },
  {
    title: t('statistics.accuracy'),
    key: 'accuracy',
    width: 100,
    align: 'center',
    render: (row: any) => {
      const accuracy = row.accuracy;
      const type = accuracy >= 80 ? 'success' : accuracy >= 60 ? 'warning' : 'error';
      return h(NTag, { type, size: 'small' }, { default: () => `${accuracy}%` });
    }
  }
];

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
  background: linear-gradient(135deg,
      var(--n-color) 0%,
      var(--n-color-embedded) 100%);
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

/* 题型统计表格区域 */
.type-stats-section {
  margin-top: 0;
}

.stats-table-card {
  border: none;
  border-radius: var(--card-border-radius);
  box-shadow: var(--card-unified-shadow);
  transition: all 0.3s ease;
  background: var(--n-color);
  width: 100%;
  max-width: var(--card-content-max-width);
  margin: 0 auto;
}

.stats-table-card:hover {
  box-shadow: var(--card-unified-shadow-hover);
}

.table-title {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--n-text-color-1);
}

.stats-table {
  width: 100%;
}

/* 表格响应式优化 */
@media (max-width: 768px) {
  .stats-table-card {
    margin: 0;
    border-radius: var(--card-border-radius);
  }

  :deep(.n-data-table) {
    font-size: var(--font-size-sm);
  }

  :deep(.n-data-table .n-data-table-th) {
    padding: var(--spacing-2) var(--spacing-1);
    font-size: var(--font-size-xs);
  }

  :deep(.n-data-table .n-data-table-td) {
    padding: var(--spacing-2) var(--spacing-1);
  }
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

/* 卡片内边距 */
:deep(.overview-summary-card .n-card__content) {
  padding: var(--card-padding-desktop);
}

:deep(.stats-table-card .n-card__content) {
  padding: var(--card-padding-md);
}

/* 表格样式优化 */
:deep(.n-data-table) {
  border-radius: var(--card-border-radius);
}

:deep(.n-data-table .n-data-table-th) {
  background-color: var(--n-color-embedded);
  font-weight: 600;
  color: var(--n-text-color-1);
}

:deep(.n-data-table .n-data-table-td) {
  border-bottom: 1px solid var(--n-divider-color);
}

:deep(.n-data-table .n-data-table-tr:hover .n-data-table-td) {
  background-color: var(--n-color-hover);
}

/* 加载状态样式 */
:deep(.n-spin-content) {
  min-height: var(--spacing-50);
}
</style>
