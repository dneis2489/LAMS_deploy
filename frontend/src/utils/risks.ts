export function formatPercent(value: number) {
  return `${(value * 100).toFixed(1)}%`;
}

export function riskTone(level: string) {
  if (level === "CRITICAL" || level === "HIGH") {
    return "danger";
  }
  if (level === "MEDIUM") {
    return "warning";
  }
  return "success";
}

export function translateRiskLevel(level: string) {
  const levels: Record<string, string> = {
    LOW: "Низкий",
    MEDIUM: "Средний",
    HIGH: "Высокий",
    CRITICAL: "Критический"
  };

  return levels[level] ?? level;
}

export function translateRiskReason(reason: string) {
  const reasons: Record<string, string> = {
    LATENCY_GROWTH: "Рост задержки",
    TRAFFIC_DEVIATION: "Отклонение трафика",
    TRAFFIC_CHANGE: "Изменение трафика",
    ERROR_RATE: "Доля ошибок",
    ERROR_GROWTH: "Рост ошибок",
    UNFINISHED_REQUESTS: "Рост незавершенных запросов"
  };

  return reasons[reason] ?? reason.replaceAll("_", " ").toLowerCase();
}
