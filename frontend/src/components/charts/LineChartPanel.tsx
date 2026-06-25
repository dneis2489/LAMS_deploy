import {
  CartesianGrid,
  Legend,
  Line,
  LineChart as RechartsLineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";
import { EmptyState } from "../ui/EmptyState";
import { LoadingBlock } from "../ui/LoadingBlock";
import type { ChartModel } from "../../utils/charts";
import type { ReactNode } from "react";

type TooltipPayloadItem = {
  color?: string;
  name?: string;
  value?: unknown;
  payload?: Record<string, unknown>;
};

type LineChartPanelProps = {
  title: string;
  subtitle: string;
  model: ChartModel;
  loading: boolean;
  controls?: ReactNode;
};

function ChartTooltip({
  active,
  label,
  payload
}: {
  active?: boolean;
  label?: string | number;
  payload?: TooltipPayloadItem[];
}) {
  if (!active || !payload || payload.length === 0) {
    return null;
  }

  return (
    <div className="chart-tooltip">
      <div className="chart-tooltip-title">{label}</div>
      <div className="chart-tooltip-values">
        {payload.map((item) => (
          <div key={`${item.name}-${String(item.value)}`} className="chart-tooltip-value">
            <span className="chart-tooltip-marker" style={{ backgroundColor: item.color }} />
            <span>{item.name}</span>
            <strong>{String(item.value ?? "-")}</strong>
          </div>
        ))}
      </div>
    </div>
  );
}

export function LineChartPanel({ title, subtitle, model, loading, controls }: LineChartPanelProps) {
  function anomalyDot(lineKey: string) {
    return function Dot(props: { cx?: number; cy?: number; payload?: Record<string, unknown> }) {
      const { cx, cy, payload } = props;

      if (!payload?.[`${lineKey}_anomaly`] || cx === undefined || cy === undefined) {
        return null;
      }

      return (
        <circle
          cx={cx}
          cy={cy}
          r={5}
          fill="#dc2626"
          stroke="#ffffff"
          strokeWidth={2}
        />
      );
    };
  }

  return (
    <section className="panel chart-panel">
      <div className="panel-heading">
        <div>
          <p className="eyebrow">{subtitle}</p>
          <h2>{title}</h2>
        </div>
      </div>

      {controls && <div className="chart-controls">{controls}</div>}

      {loading ? (
        <LoadingBlock />
      ) : model.data.length === 0 ? (
        <EmptyState text="Нет данных для графика" />
      ) : (
        <div className="chart-frame">
          <ResponsiveContainer width="100%" height="100%">
            <RechartsLineChart data={model.data} margin={{ top: 12, right: 18, bottom: 4, left: 0 }}>
              <CartesianGrid stroke="#e5e7eb" strokeDasharray="4 4" />
              <XAxis dataKey="label" tick={{ fontSize: 12 }} minTickGap={24} />
              <YAxis tick={{ fontSize: 12 }} width={54} />
              <Tooltip
                content={<ChartTooltip />}
              />
              <Legend />
              {model.lines.map((line) => (
                <Line
                  key={line.key}
                  type="monotone"
                  dataKey={line.key}
                  name={line.name}
                  stroke={line.color}
                  strokeWidth={2}
                  strokeDasharray={line.dashed ? "6 5" : undefined}
                  dot={anomalyDot(line.key)}
                  connectNulls
                />
              ))}
            </RechartsLineChart>
          </ResponsiveContainer>
        </div>
      )}
    </section>
  );
}
