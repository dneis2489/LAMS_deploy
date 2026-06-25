import type {
  CountRequestStatDTO,
  DurationStatDTO,
  MethodActionData,
  RequestStatusConvertDataForTotalDTO,
  StatisticMetric,
  UniqueUsersStatDTO
} from "../types";
import { formatDateTime, toTimestamp } from "./format";

export type ChartRow = Record<string, number | string | boolean | string[] | null>;

export type ChartLine = {
  key: string;
  name: string;
  color: string;
  dashed?: boolean;
};

export type ChartModel = {
  data: ChartRow[];
  lines: ChartLine[];
};

export const chartColors = [
  "#0b39e6",
  "#ff6b3a",
  "#1d222b",
  "#7a7f89",
  "#4361ee",
  "#f28c28",
  "#2f3540",
  "#a4a8b0"
];

export function buildTotalChart(metric: StatisticMetric, rows: unknown[] = [], statusCode?: number): ChartModel {
  if (metric === "status") {
    return buildStatusChart(rows as RequestStatusConvertDataForTotalDTO[], statusCode);
  }

  if (metric === "duration") {
    return {
      data: (rows as DurationStatDTO[])
        .map((row) => ({
          label: formatDateTime(row.date),
          sort: toTimestamp(row.date),
          minDuration: row.minDuration,
          avgDuration: row.avgDuration,
          maxDuration: row.maxDuration,
          avgPredictDuration: row.avgPredictDuration,
          maxDuration_anomaly: row.anomaly
        }))
        .sort(sortChartRows),
      lines: [
        { key: "minDuration", name: "Минимум", color: chartColors[0] },
        { key: "avgDuration", name: "Среднее", color: chartColors[1] },
        { key: "maxDuration", name: "Максимум", color: chartColors[3] },
        { key: "avgPredictDuration", name: "Прогноз", color: chartColors[2], dashed: true }
      ]
    };
  }

  if (metric === "unique") {
    const safeRows = (rows as UniqueUsersStatDTO[])
      .map((row) => ({
        label: formatDateTime(row.date),
        sort: toTimestamp(row.date),
        count: row.count,
        predict: row.predict,
        users: row.users ?? []
      }))
      .sort(sortChartRows);
    const lastActualSort = getLastActualSort(safeRows, "count");

    return {
      data: safeRows.map((row) => ({
        ...row,
        count: normalizeForecastOnlyActual(row.count, row.predict, Number(row.sort), lastActualSort)
      })),
      lines: [
        { key: "count", name: "Пользователи", color: chartColors[0] },
        { key: "predict", name: "Прогноз", color: chartColors[2], dashed: true }
      ]
    };
  }

  const safeRows = (rows as CountRequestStatDTO[])
    .map((row) => ({
        label: formatDateTime(row.date),
        sort: toTimestamp(row.date),
        count: row.count,
        predict: row.predict,
        count_anomaly: row.anomaly
      }))
    .sort(sortChartRows);
  const lastActualSort = getLastActualSort(safeRows, "count");

  return {
    data: safeRows.map((row) => ({
      ...row,
      count: normalizeForecastOnlyActual(row.count, row.predict, Number(row.sort), lastActualSort)
    })),
    lines: [
      { key: "count", name: "Запросы", color: chartColors[0] },
      { key: "predict", name: "Прогноз", color: chartColors[2], dashed: true }
    ]
  };
}

export function buildMethodChart(metric: StatisticMetric, action?: MethodActionData, statusCode?: number): ChartModel {
  if (!action) {
    return { data: [], lines: [] };
  }

  if (metric === "status") {
    return buildStatusChart(action.codeList ?? [], statusCode);
  }

  if (metric === "duration") {
    return {
      data: (action.statData ?? [])
        .map((row) => ({
          label: formatDateTime(row.date),
          sort: toTimestamp(row.date),
          minDuration: row.minDuration ?? null,
          avgDuration: row.avgDuration ?? null,
          maxDuration: row.maxDuration ?? null
        }))
        .sort(sortChartRows),
      lines: [
        { key: "minDuration", name: "Минимум", color: chartColors[0] },
        { key: "avgDuration", name: "Среднее", color: chartColors[1] },
        { key: "maxDuration", name: "Максимум", color: chartColors[3] }
      ]
    };
  }

  if (metric === "unique") {
    const safeRows = (action.statData ?? [])
      .map((row) => ({
          label: formatDateTime(row.date),
          sort: toTimestamp(row.date),
          count: row.count ?? null,
          predict: row.predict ?? null,
          users: row.users ?? []
        }))
      .sort(sortChartRows);
    const lastActualSort = getLastActualSort(safeRows, "count");

    return {
      data: safeRows.map((row) => ({
        ...row,
        count: normalizeForecastOnlyActual(row.count, row.predict, Number(row.sort), lastActualSort)
      })),
      lines: [
        { key: "count", name: "Пользователи", color: chartColors[0] },
        { key: "predict", name: "Прогноз", color: chartColors[2], dashed: true }
      ]
    };
  }

  return {
    data: (action.statData ?? [])
      .map((row) => ({
        label: formatDateTime(row.date),
        sort: toTimestamp(row.date),
        count: row.count ?? null
      }))
      .sort(sortChartRows),
    lines: [{ key: "count", name: "Запросы", color: chartColors[0] }]
  };
}

type StatusChartRow = {
  statusCode?: number;
  code?: number;
  requestStatus?: number;
  countsStatusCodeList?: Array<{ date: string; count: number; predict?: number | null; anomaly?: boolean }>;
  countStatusCodeList?: Array<{ date: string; count: number; predict?: number | null; anomaly?: boolean }>;
  statData?: Array<{ date: string; count: number; predict?: number | null; anomaly?: boolean }>;
  data?: Array<{ date: string; count: number; predict?: number | null; anomaly?: boolean }>;
};

function buildStatusChart(rows: StatusChartRow[] = [], selectedStatusCode?: number): ChartModel {
  const map = new Map<string, ChartRow>();
  const safeRows = (Array.isArray(rows) ? rows : []).filter((row) => {
    const statusCode = getStatusCode(row);
    return selectedStatusCode === undefined || statusCode === selectedStatusCode;
  });
  const lines = safeRows
    .flatMap((row, index) => {
      const statusCode = getStatusCode(row);

      if (statusCode === undefined) {
        return [];
      }

      const color = chartColors[index % chartColors.length];

      return [
        {
          key: `status_${statusCode}`,
          name: `HTTP ${statusCode}`,
          color
        },
        {
          key: `status_${statusCode}_predict`,
          name: `Прогноз HTTP ${statusCode}`,
          color,
          dashed: true
        }
      ];
    })
    .filter((line) => line.key);

  safeRows.forEach((row) => {
    const statusCode = getStatusCode(row);
    const points = getStatusPoints(row);
    const lastActualSort = getLastActualSort(
      points.map((point) => ({
        sort: toTimestamp(point.date),
        count: point.count,
        predict: point.predict ?? null
      })),
      "count"
    );

    if (statusCode === undefined || points.length === 0) {
      return;
    }

    points.forEach((point) => {
      const current =
        map.get(point.date) ??
        ({
          label: formatDateTime(point.date),
          sort: toTimestamp(point.date)
        } as ChartRow);

      current[`status_${statusCode}`] = normalizeForecastOnlyActual(
        point.count,
        point.predict ?? null,
        toTimestamp(point.date),
        lastActualSort
      );
      current[`status_${statusCode}_predict`] = point.predict ?? null;
      current[`status_${statusCode}_anomaly`] = Boolean(point.anomaly);
      map.set(point.date, current);
    });
  });

  return {
    data: [...map.values()].sort(sortChartRows),
    lines
  };
}

function getStatusCode(row: StatusChartRow) {
  return row.statusCode ?? row.code ?? row.requestStatus;
}

function getStatusPoints(row: StatusChartRow) {
  const points =
    row.countsStatusCodeList ??
    row.countStatusCodeList ??
    row.statData ??
    row.data ??
    [];

  return Array.isArray(points) ? points : [];
}

function sortChartRows(left: ChartRow, right: ChartRow) {
  return Number(left.sort ?? 0) - Number(right.sort ?? 0);
}

function getLastActualSort(rows: Array<Record<string, unknown>>, key: string) {
  return rows.reduce((lastSort, row) => {
    const value = Number(row[key] ?? 0);
    const sort = Number(row.sort ?? 0);

    return value > 0 ? Math.max(lastSort, sort) : lastSort;
  }, Number.NEGATIVE_INFINITY);
}

function normalizeForecastOnlyActual(
  actual: number | string | null | undefined,
  predict: number | string | null | undefined,
  sort: number,
  lastActualSort: number
) {
  const actualValue = actual == null ? null : Number(actual);
  const predictValue = predict == null ? null : Number(predict);

  if (actualValue === 0 && predictValue != null && predictValue > 0 && sort > lastActualSort) {
    return null;
  }

  return actualValue;
}
