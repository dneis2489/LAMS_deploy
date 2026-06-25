import { useState, type CSSProperties } from "react";
import { RefreshCcw } from "lucide-react";
import { GanttRow } from "../components/gantt/GanttRow";
import { Alert } from "../components/ui/Alert";
import { EmptyState } from "../components/ui/EmptyState";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { SegmentedControl } from "../components/ui/SegmentedControl";
import { useActivity } from "../hooks/useActivity";
import {
  formatGanttTick,
  ganttGranularityOptions,
  getGanttTickPosition,
  type GanttGranularity
} from "../utils/gantt";

export function ActivityPage() {
  const [granularity, setGranularity] = useState<GanttGranularity>("day");
  const { rows, timeline, loading, error, reload } = useActivity(granularity);
  const hourlyTimelineWidth =
    granularity === "hour" ? Math.max(780, (timeline.ticks.length - 1) * 112) : undefined;
  const ganttStyle = hourlyTimelineWidth
    ? ({ "--gantt-timeline-width": `${hourlyTimelineWidth}px` } as CSSProperties)
    : undefined;

  return (
    <div className="view-stack">
      <section className="panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">Последние 30 дней</p>
            <h2>Активность пользователей</h2>
          </div>
          <div className="activity-actions">
            <SegmentedControl
              label="Детализация"
              options={ganttGranularityOptions}
              value={granularity}
              onChange={(value) => setGranularity(value as GanttGranularity)}
            />
            <button type="button" className="button ghost" onClick={reload}>
              <RefreshCcw size={16} />
              Обновить
            </button>
          </div>
        </div>

        <Alert tone="error">{error}</Alert>
        {loading ? (
          <LoadingBlock />
        ) : rows.length === 0 ? (
          <EmptyState text="Нет данных активности" />
        ) : (
          <div className="gantt-scroll">
            <div className="gantt-grid" style={ganttStyle}>
              <div className="gantt-head user-col">Пользователь</div>
              <div className="gantt-head count-col">Действий</div>
              <div className="gantt-head timeline-col">
                {timeline.ticks.map((tick, index) => (
                  <span
                    key={tick}
                    className={`gantt-tick-mark${index === 0 ? " first" : ""}${
                      index === timeline.ticks.length - 1 ? " last" : ""
                    }`}
                    style={{ left: getGanttTickPosition(tick, timeline) }}
                  >
                    <span>{formatGanttTick(tick, granularity)}</span>
                  </span>
                ))}
              </div>

              {rows.map((row) => (
                <GanttRow key={row.userName} row={row} timeline={timeline} granularity={granularity} />
              ))}
            </div>
          </div>
        )}
      </section>
    </div>
  );
}
