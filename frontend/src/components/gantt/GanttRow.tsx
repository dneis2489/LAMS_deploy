import { useEffect, useId, useMemo, useState } from "react";
import { createPortal } from "react-dom";
import { UserCog } from "lucide-react";
import type { UserActivityGrouppingDTO } from "../../types";
import {
  buildGanttSegments,
  colorForText,
  getGanttSegmentPosition,
  getGanttTickPosition,
  type GanttGranularity,
  type Timeline
} from "../../utils/gantt";
import { formatDateTime, formatDuration } from "../../utils/format";

type GanttRowProps = {
  row: UserActivityGrouppingDTO;
  timeline: Timeline;
  granularity: GanttGranularity;
};

type PopoverPosition = {
  left: number;
  top: number;
  placement: "above" | "below";
};

export function GanttRow({ row, timeline, granularity }: GanttRowProps) {
  const segments = useMemo(() => buildGanttSegments(row.data, granularity), [granularity, row.data]);
  const [activeSegmentId, setActiveSegmentId] = useState("");
  const [popoverPosition, setPopoverPosition] = useState<PopoverPosition | null>(null);
  const popoverId = useId();
  const activeSegment = segments.find((segment) => segment.id === activeSegmentId);
  const activeDetails = activeSegment?.summary.slice(0, 4) ?? [];
  const hiddenActiveDetailsCount = (activeSegment?.summary.length ?? 0) - activeDetails.length;

  function showSegment(segmentId: string, element: HTMLElement) {
    const rect = element.getBoundingClientRect();
    const popoverWidth = Math.min(320, window.innerWidth - 24);
    const halfWidth = popoverWidth / 2;
    const left = Math.min(
      window.innerWidth - halfWidth - 12,
      Math.max(halfWidth + 12, rect.left + rect.width / 2)
    );
    const placement = rect.bottom + 300 > window.innerHeight ? "above" : "below";

    setPopoverPosition({
      left,
      top: placement === "above" ? rect.top - 10 : rect.bottom + 10,
      placement
    });
    setActiveSegmentId(segmentId);
  }

  function closePopover() {
    setActiveSegmentId("");
    setPopoverPosition(null);
  }

  useEffect(() => {
    if (!activeSegmentId) {
      return;
    }

    function closeOnViewportChange() {
      closePopover();
    }

    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === "Escape") {
        closePopover();
      }
    }

    window.addEventListener("resize", closeOnViewportChange);
    window.addEventListener("scroll", closeOnViewportChange, true);
    window.addEventListener("keydown", closeOnEscape);
    return () => {
      window.removeEventListener("resize", closeOnViewportChange);
      window.removeEventListener("scroll", closeOnViewportChange, true);
      window.removeEventListener("keydown", closeOnEscape);
    };
  }, [activeSegmentId]);

  return (
    <>
      <div className="gantt-user user-col">
        <UserCog size={16} />
        <span>{row.userName}</span>
      </div>
      <div className="gantt-count count-col">{row.count}</div>
      <div className="gantt-lane timeline-col">
        {timeline.ticks.map((tick) => (
          <span
            key={`tick-${tick}`}
            className="gantt-tick-line"
            style={{ left: getGanttTickPosition(tick, timeline) }}
            aria-hidden="true"
          />
        ))}
        {segments.map((segment) => {
          const position = getGanttSegmentPosition(segment, timeline);

          return (
            <button
              type="button"
              key={segment.id}
              className="gantt-bar"
              aria-label={`${row.userName}: ${segment.actionCount} действий, ${segment.microserviceName}`}
              aria-expanded={activeSegmentId === segment.id}
              aria-describedby={activeSegmentId === segment.id ? popoverId : undefined}
              onMouseEnter={(event) => showSegment(segment.id, event.currentTarget)}
              onMouseLeave={closePopover}
              onFocus={(event) => showSegment(segment.id, event.currentTarget)}
              onBlur={closePopover}
              onClick={(event) => showSegment(segment.id, event.currentTarget)}
              style={{
                ...position,
                backgroundColor: colorForText(segment.microserviceName)
              }}
            />
          );
        })}
      </div>
      {activeSegment && popoverPosition && createPortal(
        <div
          id={popoverId}
          className={`gantt-popover ${popoverPosition.placement}`}
          role="tooltip"
          style={{ left: popoverPosition.left, top: popoverPosition.top }}
        >
          <strong>{activeSegment.microserviceName}</strong>
          <span className="gantt-tooltip-muted">
            {formatDateTime(new Date(activeSegment.start).toISOString())} -{" "}
            {formatDateTime(new Date(activeSegment.end).toISOString())}
          </span>
          <span>
            {activeSegment.actionCount} действий, суммарно {formatDuration(activeSegment.totalDuration)}
          </span>
          <span className="gantt-tooltip-list">
            {activeDetails.map((item) => (
              <span key={`${item.microserviceName}-${item.actionName}`}>
                <strong>{item.count}x</strong>
                <span>{item.actionName}</span>
                <em>{item.microserviceName}</em>
              </span>
            ))}
            {hiddenActiveDetailsCount > 0 && (
              <span className="gantt-tooltip-muted">Еще {hiddenActiveDetailsCount} групп</span>
            )}
          </span>
        </div>,
        document.body
      )}
    </>
  );
}
