export const formatNumber = (value: number | null | undefined): string => {
  return (value ?? 0).toLocaleString();
};

export const formatPercentage = (
  correct: number | null | undefined,
  total: number | null | undefined
): string => {
  const c = correct ?? 0;
  const t = total ?? 0;

  if (t === 0) return "0%";

  const pct = (c / t) * 100;
  return `${pct.toFixed(1)}%`;
};
