import type { LogFilters, StatisticMetric, StatisticPeriod } from "../types";

export const metricOptions: Array<{ value: StatisticMetric; label: string }> = [
  { value: "count", label: "Запросы" },
  { value: "status", label: "Статусы" },
  { value: "duration", label: "Длительность" },
  { value: "unique", label: "Уникальные пользователи" }
];

export const periodOptions: Array<{ value: StatisticPeriod; label: string }> = [
  { value: "hour", label: "Час" },
  { value: "day", label: "День" },
  { value: "month", label: "Месяц" }
];

export const emptyLogFilters: LogFilters = {
  micros: [],
  action: [],
  requestStatus: [],
  startDate: "",
  endDate: "",
  withoutResponse: false
};
