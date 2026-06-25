const logTypeLabels: Record<string, string> = {
  start: "Инициализация",
  init: "Инициализация",
  initialization: "Инициализация",
  "инициализация": "Инициализация",
  "начало": "Инициализация",
  finish: "Завершение",
  finished: "Завершение",
  end: "Завершение",
  complete: "Завершение",
  completed: "Завершение",
  "завершен": "Завершение",
  "завершён": "Завершение",
  "завершение": "Завершение",
  no_response: "Без ответа",
  "no response": "Без ответа",
  "without response": "Без ответа",
  "без ответа": "Без ответа"
};

export function formatLogType(value?: string | null) {
  if (!value?.trim()) {
    return "—";
  }

  const normalized = value.trim().toLocaleLowerCase("ru-RU");
  return logTypeLabels[normalized] ?? value;
}
