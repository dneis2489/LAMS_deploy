export function errorMessage(error: unknown) {
  return error instanceof Error ? error.message : "Неизвестная ошибка";
}

export function formatDateTime(value?: string) {
  if (!value) {
    return "-";
  }

  if (/^\d{2}\.\d{2}\.\d{4}$/.test(value)) {
    return value;
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value.replace("T", " ").slice(0, 16);
  }

  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  }).format(date);
}

export function formatDateTimeWithSeconds(value?: string) {
  if (!value) {
    return "-";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value.replace("T", " ").slice(0, 19);
  }

  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
  }).format(date);
}

export function formatDateShort(value: number) {
  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit"
  }).format(new Date(value));
}

export function formatDuration(value: number) {
  if (!Number.isFinite(value)) {
    return "-";
  }

  if (value < 1000) {
    return `${Math.round(value)} мс`;
  }

  if (value < 60_000) {
    return `${(value / 1000).toFixed(1)} с`;
  }

  return `${(value / 60_000).toFixed(1)} мин`;
}

export function toTimestamp(value?: string) {
  if (!value) {
    return 0;
  }

  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 0 : date.getTime();
}
