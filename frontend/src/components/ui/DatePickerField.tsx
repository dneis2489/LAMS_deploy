import { useEffect, useId, useMemo, useRef, useState } from "react";
import { CalendarDays, ChevronDown, ChevronLeft, ChevronRight } from "lucide-react";

type DatePickerFieldProps = {
  label: string;
  value: string;
  onChange: (value: string) => void;
};

const weekDays = ["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];

export function DatePickerField({ label, value, onChange }: DatePickerFieldProps) {
  const labelId = useId();
  const rootRef = useRef<HTMLDivElement>(null);
  const selectedDate = parseIsoDate(value);
  const [open, setOpen] = useState(false);
  const [viewDate, setViewDate] = useState(() => selectedDate ?? new Date());

  useEffect(() => {
    if (open) {
      setViewDate(selectedDate ?? new Date());
    }
  }, [open, selectedDate?.getTime()]);

  useEffect(() => {
    function handleDocumentClick(event: MouseEvent) {
      if (!rootRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    }

    function handleEscape(event: KeyboardEvent) {
      if (event.key === "Escape") {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleDocumentClick);
    document.addEventListener("keydown", handleEscape);

    return () => {
      document.removeEventListener("mousedown", handleDocumentClick);
      document.removeEventListener("keydown", handleEscape);
    };
  }, []);

  const days = useMemo(() => buildCalendarDays(viewDate), [viewDate]);
  const monthLabel = useMemo(() => {
    const labelText = new Intl.DateTimeFormat("ru-RU", {
      month: "long",
      year: "numeric"
    }).format(viewDate);

    return labelText.charAt(0).toUpperCase() + labelText.slice(1);
  }, [viewDate]);

  function shiftMonth(delta: number) {
    setViewDate((current) => new Date(current.getFullYear(), current.getMonth() + delta, 1));
  }

  function selectDate(date: Date) {
    onChange(formatIsoDate(date));
    setOpen(false);
  }

  function selectToday() {
    const today = new Date();
    onChange(formatIsoDate(today));
    setViewDate(today);
    setOpen(false);
  }

  function clearDate() {
    onChange("");
    setOpen(false);
  }

  return (
    <div className="field compact-field date-field date-picker-field" ref={rootRef}>
      <span id={labelId}>{label}</span>
      <button
        type="button"
        className={`date-input-control date-picker-trigger${open ? " open" : ""}`}
        aria-labelledby={labelId}
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
      >
        <CalendarDays size={16} aria-hidden="true" />
        <span className={`date-input-value${value ? "" : " placeholder"}`}>
          {selectedDate ? formatDisplayDate(selectedDate) : "Выберите дату"}
        </span>
        <ChevronDown size={15} className="date-picker-chevron" aria-hidden="true" />
      </button>

      {open && (
        <div className="date-picker-popover" role="dialog" aria-labelledby={labelId}>
          <div className="date-picker-header">
            <button type="button" className="icon-button" title="Предыдущий месяц" onClick={() => shiftMonth(-1)}>
              <ChevronLeft size={17} />
            </button>
            <span className="date-picker-month">{monthLabel}</span>
            <button type="button" className="icon-button" title="Следующий месяц" onClick={() => shiftMonth(1)}>
              <ChevronRight size={17} />
            </button>
          </div>

          <div className="date-picker-weekdays" aria-hidden="true">
            {weekDays.map((day) => (
              <span key={day}>{day}</span>
            ))}
          </div>

          <div className="date-picker-grid">
            {days.map((date) => {
              const selected = selectedDate ? isSameDate(date, selectedDate) : false;
              const today = isSameDate(date, new Date());
              const outside = date.getMonth() !== viewDate.getMonth();

              return (
                <button
                  key={date.toISOString()}
                  type="button"
                  className={[
                    "date-picker-day",
                    selected ? "selected" : "",
                    today ? "today" : "",
                    outside ? "outside" : ""
                  ].filter(Boolean).join(" ")}
                  aria-pressed={selected}
                  onClick={() => selectDate(date)}
                >
                  {date.getDate()}
                </button>
              );
            })}
          </div>

          <div className="date-picker-actions">
            <button type="button" className="button ghost" onClick={clearDate}>
              Очистить
            </button>
            <button type="button" className="button primary" onClick={selectToday}>
              Сегодня
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function buildCalendarDays(viewDate: Date) {
  const year = viewDate.getFullYear();
  const month = viewDate.getMonth();
  const firstDay = new Date(year, month, 1);
  const mondayOffset = (firstDay.getDay() + 6) % 7;
  const start = new Date(year, month, 1 - mondayOffset);

  return Array.from({ length: 42 }, (_, index) =>
    new Date(start.getFullYear(), start.getMonth(), start.getDate() + index)
  );
}

function parseIsoDate(value: string) {
  if (!value) {
    return null;
  }

  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value);
  if (!match) {
    return null;
  }

  const [, year, month, day] = match;
  return new Date(Number(year), Number(month) - 1, Number(day));
}

function formatIsoDate(date: Date) {
  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0")
  ].join("-");
}

function formatDisplayDate(date: Date) {
  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  }).format(date);
}

function isSameDate(left: Date, right: Date) {
  return (
    left.getFullYear() === right.getFullYear() &&
    left.getMonth() === right.getMonth() &&
    left.getDate() === right.getDate()
  );
}
