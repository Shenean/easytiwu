<template>
  <PageContainer
    :title="t('statistics.title')"
    :show-card="false"
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
      <t-loading size="large" :text="t('statistics.loading')" />
    </div>

    <!-- 内容区域 -->
    <template v-if="!loading">
      <t-space direction="vertical" size="medium" class="statistics-content">
        <!-- 错误提示 -->
        <t-alert v-if="error" theme="error" :message="error" />

        <!-- 统计数据内容 -->
        <template v-else-if="stats">
          <!-- 总览统计 -->
          <t-card class="overview-summary-card" size="small" :bordered="false">
            <t-row
              :gutter="{ xs: 16, sm: 16, md: 24, lg: 24, xl: 32, xxl: 32 }"
              class="overview-grid"
            >
              <t-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" :xxl="12">
                <div class="overview-item">
                  <t-space direction="vertical" size="small" align="center">
                    <div class="statistic-value">
                      {{ formatNumber(stats.bankTotal) }}
                    </div>
                    <div class="statistic-label">
                      {{ t("statistics.bankTotal") }}
                    </div>
                  </t-space>
                </div>
              </t-col>
              <t-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" :xxl="12">
                <div class="overview-item">
                  <t-space direction="vertical" size="small" align="center">
                    <div class="statistic-value">
                      {{ formatNumber(stats.questionTotal) }}
                    </div>
                    <div class="statistic-label">
                      {{ t("statistics.questionTotal") }}
                    </div>
                  </t-space>
                </div>
              </t-col>
            </t-row>
          </t-card>

          <!-- 题型统计表格 -->
          <t-card class="stats-table-card" size="small" :bordered="false">
            <template #header>
              <h4 class="table-title">{{ t("statistics.typeStatistics") }}</h4>
            </template>
            <t-table
              :columns="tableColumns"
              :data="typeStats"
              :pagination="{ pageSize: 8, showJumper: false, showPageSize: false }"
              :row-key="'type'"
              :bordered="false"
              size="small"
              class="stats-table"
            />
          </t-card>
        </template>

        <!-- 空状态 -->
        <t-empty v-else :description="t('statistics.noData')" size="small" />
      </t-space>
    </template>

    <!-- 刷新按钮 -->
    <template #action>
      <t-button
        @click="refreshData"
        :loading="loading"
        theme="primary"
        size="small"
      >
        {{ t("statistics.refresh") }}
      </t-button>
    </template>
  </PageContainer>
</template>

<script setup lang="ts">
import {computed, h, onMounted, ref} from "vue";
import type {TableCol} from "tdesign-vue-next";
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
const tableColumns: TableCol[] = [
  {
    title: t("statistics.questionType"),
    colKey: "type",
    width: 120,
    render: (row: any) => {
      return row.type;
    },
  },
  {
    title: t("statistics.totalQuestions"),
    colKey: "total",
    width: 100,
    align: "center",
    render: (row: any) => {
      return formatNumber(row.total);
    },
  },
  {
    title: t("statistics.completed"),
    colKey: "completed",
    width: 100,
    align: "center",
    render: (row: any) => {
      return formatNumber(row.completed);
    },
  },
  {
    title: t("statistics.completionRate"),
    colKey: "percentage",
    width: 120,
    align: "center",
    render: (row: any) => {
      const percentage = row.percentage;
      const type =
        percentage >= 80 ? "success" : percentage >= 60 ? "warning" : "error";
      return h(
        "t-tag",
        { theme: type, size: "small" },
        { default: () => `${percentage}%` }
      );
    },
  },
  {
    title: t("statistics.accuracy"),
    colKey: "accuracy",
    width: 100,
    align: "center",
    render: (row: any) => {
      const accuracy = row.accuracy;
      const type =
        accuracy >= 80 ? "success" : accuracy >= 60 ? "warning" : "error";
      return h(
        "t-tag",
        { theme: type, size: "small" },
        { default: () => `${accuracy}%` }
      );
    },
  },
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
  background: linear-gradient(
    135deg,
    var(--td-brand-color) 0%,
    var(--td-brand-color-light) 100%
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

.statistics-content {
  width: 100%;
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
  background: var(--td-brand-color);
  width: 100%;
  max-width: var(--card-content-max-width);
  margin: 0 auto;
}

.stats-table-card:hover {
  box-shadow: var(--card-unified-shadow-hover);
}

.table-title {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--td-text-color-primary);
}

.stats-table {
  width: 100%;
}

/* 自定义统计数字样式 */
.statistic-value {
  font-weight: 700;
  color: var(--td-brand-color);
  font-size: 20px;
  line-height: 1.2;
}

.statistic-label {
  color: var(--td-text-color-secondary);
  font-weight: 600;
  font-size: 12px;
}

/* 表格响应式优化 */
@media (max-width: 768px) {
  .stats-table-card {
    margin: 0;
    border-radius: var(--card-border-radius);
  }

  :deep(.t-table) {
    font-size: var(--font-size-sm);
  }

  :deep(.t-table .t-table__header th) {
    padding: var(--spacing-1) var(--spacing-xs);
    font-size: var(--font-size-xs);
  }

  :deep(.t-table .t-table__body td) {
    padding: var(--spacing-1) var(--spacing-xs);
  }
}

/* 卡片内边距 */
:deep(.overview-summary-card .t-card__body) {
  padding: var(--spacing-md);
}

:deep(.stats-table-card .t-card__body) {
  padding: var(--spacing-sm) var(--spacing-md);
}

/* 表格样式优化 */
:deep(.t-table) {
  border-radius: var(--card-border-radius);
}

:deep(.t-table .t-table__header th) {
  background-color: var(--td-bg-color-container);
  font-weight: 600;
  color: var(--td-text-color-primary);
}

:deep(.t-table .t-table__body td) {
  border-bottom: 1px solid var(--td-border-level-1-color);
}

:deep(.t-table .t-table__body tr:hover td) {
  background-color: var(--td-bg-color-container-hover);
}
</style>
