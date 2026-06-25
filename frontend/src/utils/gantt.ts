import type { UserActivityData, UserActivityGrouppingDTO } from "../types";
import { chartColors } from "./charts";
import { toTimestamp } from "./format";
import { clamp } from "./numbers";

export type GanttGranularity = "hour" | "day" | "week" | "month";

export type GanttSegmentSummary = {
  microserviceName: string;
  actionName: string;
  count: number;
  totalDuration: number;
};

export type GanttSegment = {
  id: string;
  start: number;
  end: number;
  actionCount: number;
  totalDuration: number;
  microserviceName: string;
  actionName: string;
  activities: UserActivityData[];
  summary: GanttSegmentSummary[];
};

export const ganttGranularityOptions: Array<{ value: GanttGranularity; label: string }> = [
  { value: "hour", label: "Часы" },
  { value: "day", label: "День" },
  { value: "week", label: "Неделя" },
  { value: "month", label: "Месяц" }
];

export type Timeline = ReturnType<typeof buildTimeline>;

export function buildTimeline(rows: UserActivityGrouppingDTO[], granularity: GanttGranularity = "day") {
  const dates = rows.flatMap((row) =>
    row.data.flatMap((item) => {
      const interval = getActivityInterval(item);
      return interval ? [interval.start, interval.end] : [];
    })
  );
  const validDates = dates.filter((date) => date > 0);
  const now = Date.now();
  const minSource = validDates.length ? Math.min(...validDates) : now;
  const min = startOfBucket(minSource, granularity === "hour" ? "day" : granularity);
  const maxSource = validDates.length ? Math.max(...validDates) : now;
  const naturalMax = Math.max(
    addBucket(startOfBucket(maxSource, granularity), granularity),
    addBucket(min, granularity)
  );
  const max =
    granularity === "hour"
      ? addBucket(min, granularity, Math.max(6, Math.ceil(countBuckets(min, naturalMax, granularity) / 6) * 6))
      : naturalMax;
  const span = Math.max(max - min, 1);
  const ticks = buildTimelineTicks(min, max, granularity);

  return { min, max, span, ticks, granularity };
}

export function buildGanttSegments(data: UserActivityData[], granularity: GanttGranularity) {
  const buckets = new Map<number, UserActivityData[]>();

  data.forEach((item) => {
    const interval = getActivityInterval(item);

    if (!interval) {
      return;
    }

    const bucketStart = startOfBucket(interval.start, granularity);
    const bucketItems = buckets.get(bucketStart) ?? [];
    bucketItems.push(item);
    buckets.set(bucketStart, bucketItems);
  });

  const segments = Array.from(buckets.entries())
    .sort(([left], [right]) => left - right)
    .map(([bucketStart, activities]) => createSegment(bucketStart, activities, granularity));

  return mergeAdjacentSegments(segments);
}

export function getGanttSegmentPosition(segment: GanttSegment, timeline: Timeline) {
  const left = timeline.span === 0 ? 0 : ((segment.start - timeline.min) / timeline.span) * 100;
  const width = timeline.span === 0 ? 100 : ((segment.end - segment.start) / timeline.span) * 100;

  return {
    left: `${clamp(left, 0, 100)}%`,
    width: `${Math.max(width, 0.8)}%`
  };
}

export function getGanttTickPosition(value: number, timeline: Timeline) {
  const position = timeline.span === 0 ? 0 : ((value - timeline.min) / timeline.span) * 100;
  return `${clamp(position, 0, 100)}%`;
}

export function formatGanttTick(value: number, granularity: GanttGranularity) {
  const date = new Date(value);

  if (granularity === "hour") {
    const dateLabel = new Intl.DateTimeFormat("ru-RU", {
      day: "2-digit",
      month: "2-digit"
    }).format(date);
    const timeLabel = new Intl.DateTimeFormat("ru-RU", {
      hour: "2-digit",
      minute: "2-digit",
      hourCycle: "h23"
    }).format(date);

    return `${dateLabel} · ${timeLabel}`;
  }

  if (granularity === "month") {
    return new Intl.DateTimeFormat("ru-RU", {
      month: "short",
      year: "2-digit"
    }).format(date);
  }

  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit"
  }).format(date);
}

export function colorForText(value: string) {
  let hash = 0;

  for (let index = 0; index < value.length; index += 1) {
    hash = value.charCodeAt(index) + ((hash << 5) - hash);
  }

  return chartColors[Math.abs(hash) % chartColors.length];
}

function createSegment(bucketStart: number, activities: UserActivityData[], granularity: GanttGranularity): GanttSegment {
  const summary = summarizeActivities(activities);
  const primary = summary[0];
  const end = addBucket(bucketStart, granularity);
  const totalDuration = summary.reduce((sum, item) => sum + item.totalDuration, 0);

  return {
    id: `${bucketStart}-${primary?.microserviceName ?? "unknown"}`,
    start: bucketStart,
    end,
    actionCount: activities.length,
    totalDuration,
    microserviceName: primary?.microserviceName ?? "Микросервис не определен",
    actionName: primary?.actionName ?? "Действие не определено",
    activities,
    summary
  };
}

function mergeAdjacentSegments(segments: GanttSegment[]) {
  return segments.reduce<GanttSegment[]>((merged, current) => {
    const previous = merged[merged.length - 1];

    if (!previous || previous.end !== current.start || previous.microserviceName !== current.microserviceName) {
      merged.push(current);
      return merged;
    }

    const activities = [...previous.activities, ...current.activities];
    const summary = summarizeActivities(activities);
    const primary = summary[0];

    merged[merged.length - 1] = {
      ...previous,
      id: `${previous.id}-${current.id}`,
      end: current.end,
      actionCount: previous.actionCount + current.actionCount,
      totalDuration: previous.totalDuration + current.totalDuration,
      actionName: primary?.actionName ?? previous.actionName,
      activities,
      summary
    };

    return merged;
  }, []);
}

function summarizeActivities(activities: UserActivityData[]) {
  const summary = new Map<string, GanttSegmentSummary>();

  activities.forEach((activity) => {
    const microserviceName = activity.microserviceName || "Микросервис не определен";
    const actionName = activity.actionName || "Действие не определено";
    const key = `${microserviceName}\u0000${actionName}`;
    const current =
      summary.get(key) ??
      ({
        microserviceName,
        actionName,
        count: 0,
        totalDuration: 0
      } satisfies GanttSegmentSummary);

    current.count += 1;
    current.totalDuration += getActivityDuration(activity);
    summary.set(key, current);
  });

  return Array.from(summary.values()).sort((left, right) => {
    if (right.count !== left.count) {
      return right.count - left.count;
    }

    return right.totalDuration - left.totalDuration;
  });
}

function getActivityInterval(item: UserActivityData) {
  const start = toTimestamp(item.startDate);

  if (!start) {
    return null;
  }

  const endFromDate = toTimestamp(item.endDate);
  const endFromDuration = start + getActivityDuration(item);
  const end = Math.max(endFromDate || endFromDuration, start + 1);

  return { start, end };
}

function getActivityDuration(item: UserActivityData) {
  return Number.isFinite(item.duration) && item.duration > 0 ? item.duration : 0;
}

function startOfBucket(value: number, granularity: GanttGranularity) {
  const date = new Date(value);

  if (granularity === "hour") {
    date.setMinutes(0, 0, 0);
    return date.getTime();
  }

  if (granularity === "day") {
    date.setHours(0, 0, 0, 0);
    return date.getTime();
  }

  if (granularity === "week") {
    date.setHours(0, 0, 0, 0);
    const day = date.getDay();
    const mondayOffset = day === 0 ? -6 : 1 - day;
    date.setDate(date.getDate() + mondayOffset);
    return date.getTime();
  }

  date.setDate(1);
  date.setHours(0, 0, 0, 0);
  return date.getTime();
}

function addBucket(value: number, granularity: GanttGranularity, amount = 1) {
  const date = new Date(value);

  if (granularity === "hour") {
    date.setHours(date.getHours() + amount);
  } else if (granularity === "day") {
    date.setDate(date.getDate() + amount);
  } else if (granularity === "week") {
    date.setDate(date.getDate() + amount * 7);
  } else {
    date.setMonth(date.getMonth() + amount);
  }

  return date.getTime();
}

function buildTimelineTicks(min: number, max: number, granularity: GanttGranularity) {
  const bucketCount = countBuckets(min, max, granularity);
  const step = granularity === "hour" ? 6 : Math.max(1, Math.ceil(bucketCount / 5));
  const tickLimit = granularity === "hour" ? 200 : 8;
  const ticks: number[] = [];
  let cursor = min;

  while (cursor <= max && ticks.length < tickLimit) {
    ticks.push(cursor);
    cursor = addBucket(cursor, granularity, step);
  }

  if (ticks[ticks.length - 1] !== max) {
    ticks.push(max);
  }

  return ticks;
}

function countBuckets(min: number, max: number, granularity: GanttGranularity) {
  let count = 0;
  let cursor = min;

  while (cursor < max && count < 10_000) {
    count += 1;
    cursor = addBucket(cursor, granularity);
  }

  return Math.max(count, 1);
}
