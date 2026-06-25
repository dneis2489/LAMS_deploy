import { Activity, LineChart as LineChartIcon, RefreshCcw, SlidersHorizontal } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { LineChartPanel } from "../components/charts/LineChartPanel";
import { Alert } from "../components/ui/Alert";
import { EmptyState } from "../components/ui/EmptyState";
import { Kpi } from "../components/ui/Kpi";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { SegmentedControl } from "../components/ui/SegmentedControl";
import { SingleSelectDropdown } from "../components/ui/SingleSelectDropdown";
import { metricOptions, periodOptions } from "../constants/options";
import { useStatistics } from "../hooks/useStatistics";
import type { StatisticMetric, StatisticPeriod } from "../types";
import type { ChartModel } from "../utils/charts";

type UniqueUsersTableProps = {
  title: string;
  subtitle: string;
  model: ChartModel;
  loading: boolean;
  period: StatisticPeriod;
};

function UniqueUsersTable({ title, subtitle, model, loading, period }: UniqueUsersTableProps) {
  const [selectedPeriod, setSelectedPeriod] = useState("");
  const availablePeriods = useMemo(() => {
    const periods = new Map<string, number>();

    model.data.forEach((point) => {
      const label = String(point.label ?? "—");
      const sort = Number(point.sort ?? 0);
      periods.set(label, Math.max(periods.get(label) ?? Number.NEGATIVE_INFINITY, sort));
    });

    return [...periods.entries()]
      .sort((left, right) => right[1] - left[1])
      .map(([label]) => label);
  }, [model.data]);
  const rows = model.data
    .filter((point) => !selectedPeriod || String(point.label ?? "—") === selectedPeriod)
    .flatMap((point) => {
    const users = Array.isArray(point.users)
      ? point.users.filter((user): user is string => typeof user === "string")
      : [];

    return users.map((user) => ({
      period: String(point.label ?? "—"),
      user
    }));
    });

  useEffect(() => {
    if (selectedPeriod && !availablePeriods.includes(selectedPeriod)) {
      setSelectedPeriod("");
    }
  }, [availablePeriods, selectedPeriod]);

  return (
    <section className="panel unique-users-table-panel">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">{subtitle}</p>
          <h2>{title}</h2>
        </div>
      </div>

      {loading ? (
        <LoadingBlock />
      ) : (
        <>
          <div className="unique-users-table-filter">
            <SingleSelectDropdown
              label="Период"
              placeholder="Все периоды"
              options={[
                { value: "", label: "Все периоды" },
                ...availablePeriods.map((periodValue) => ({
                  value: periodValue,
                  label: formatTablePeriod(periodValue, period)
                }))
              ]}
              value={selectedPeriod}
              onChange={setSelectedPeriod}
            />
          </div>

          {rows.length === 0 ? (
            <EmptyState text="Нет данных о пользователях за выбранный период" />
          ) : (
            <div className="table-scroll unique-users-table-scroll">
              <table className="data-table unique-users-table">
                <thead>
                  <tr>
                    <th>Период</th>
                    <th>Пользователь</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row, index) => (
                    <tr key={`${row.period}-${row.user}-${index}`}>
                      <td>{formatTablePeriod(row.period, period)}</td>
                      <td className="mono">{row.user}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </section>
  );
}

function formatTablePeriod(value: string, period: StatisticPeriod) {
  return period === "hour" ? value : value.replace(/(?:,\s*|\s+)\d{2}:\d{2}$/, "");
}

export function StatisticsPage() {
  const statistics = useStatistics();
  const selectedMetricLabel = metricOptions.find((item) => item.value === statistics.metric)?.label ?? "";

  const methodChart = (
    <LineChartPanel
      title="Динамика выбранного метода"
      subtitle={statistics.selectedAction || "Метод не выбран"}
      model={statistics.methodChart}
      loading={statistics.loading}
      controls={
        <div className="detail-controls method-chart-controls">
          <SingleSelectDropdown
            label="Микросервис"
            placeholder="Выберите микросервис"
            options={statistics.methodRows.map((item) => ({
              value: item.microserviceName,
              label: item.microserviceName
            }))}
            value={statistics.selectedMicroservice}
            onChange={statistics.setSelectedMicroservice}
          />
          <SingleSelectDropdown
            label="Метод"
            placeholder="Выберите метод"
            options={(statistics.selectedMicroserviceData?.actionList ?? []).map((item) => ({
              value: item.action,
              label: item.action
            }))}
            value={statistics.selectedAction}
            onChange={statistics.setSelectedAction}
          />
          {statistics.metric === "status" && (
            <SingleSelectDropdown
              label="Статус"
              placeholder="Все статусы"
              options={[
                { value: "", label: "Все статусы" },
                ...statistics.methodStatusOptions.map((statusCode) => ({
                  value: String(statusCode),
                  label: `HTTP ${statusCode}`
                }))
              ]}
              value={statistics.selectedMethodStatus}
              onChange={statistics.setSelectedMethodStatus}
            />
          )}
        </div>
      }
    />
  );

  const totalChart = (
    <LineChartPanel
      title="Общая динамика"
      subtitle={selectedMetricLabel}
      model={statistics.totalChart}
      loading={statistics.loading}
      controls={
        statistics.metric === "status" ? (
          <div className="total-status-control">
            <SingleSelectDropdown
              label="Статус"
              placeholder="Все статусы"
              options={[
                { value: "", label: "Все статусы" },
                ...statistics.totalStatusOptions.map((statusCode) => ({
                  value: String(statusCode),
                  label: `HTTP ${statusCode}`
                }))
              ]}
              value={statistics.selectedTotalStatus}
              onChange={statistics.setSelectedTotalStatus}
            />
          </div>
        ) : (
          <div className="chart-controls-placeholder" aria-hidden="true" />
        )
      }
    />
  );

  return (
    <div className="view-stack">
      <section className="panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">Графики</p>
            <h2>Статистика системы</h2>
          </div>
          <button type="button" className="button ghost" onClick={statistics.reload}>
            <RefreshCcw size={16} />
            Обновить
          </button>
        </div>

        <div className="control-row">
          <SegmentedControl
            label="Метрика"
            options={metricOptions}
            value={statistics.metric}
            onChange={(value) => statistics.setMetric(value as StatisticMetric)}
          />
          <SegmentedControl
            label="Период"
            options={periodOptions}
            value={statistics.period}
            onChange={(value) => statistics.setPeriod(value as StatisticPeriod)}
          />
        </div>

        <Alert tone="error">{statistics.error}</Alert>

        <div className="kpi-grid">
          <Kpi icon={LineChartIcon} label="Точек в общем ряду" value={String(statistics.totalChart.data.length)} />
          <Kpi icon={SlidersHorizontal} label="Микросервисов" value={String(statistics.methodRows.length)} />
          <Kpi icon={Activity} label="Методов" value={String(statistics.methodCount)} />
        </div>
      </section>

      {statistics.metric === "unique" ? (
        <div className="statistics-unique-layout">
          <div className="statistics-unique-column">
            <UniqueUsersTable
              title="Пользователи выбранного метода"
              subtitle={statistics.selectedAction || "Метод не выбран"}
              model={statistics.methodChart}
              loading={statistics.loading}
              period={statistics.period}
            />
            <UniqueUsersTable
              title="Все уникальные пользователи"
              subtitle={selectedMetricLabel}
              model={statistics.totalChart}
              loading={statistics.loading}
              period={statistics.period}
            />
          </div>
          <div className="statistics-unique-column">
            {methodChart}
            {totalChart}
          </div>
        </div>
      ) : (
        <div className="statistics-charts-grid">
          {methodChart}
          {totalChart}
        </div>
      )}
    </div>
  );
}
