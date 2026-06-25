import { useEffect, useRef } from "react";
import { X } from "lucide-react";
import type { FullLogInfoDTO } from "../../types";
import { formatDateTimeWithSeconds, formatDuration } from "../../utils/format";
import { formatLogType } from "../../utils/logs";
import { Alert } from "../ui/Alert";
import { InfoRow } from "../ui/InfoRow";
import { LoadingBlock } from "../ui/LoadingBlock";

type LogDetailDrawerProps = {
  detail: FullLogInfoDTO[] | null;
  loading: boolean;
  error: string;
  onClose: () => void;
};

export function LogDetailDrawer({ detail, loading, error, onClose }: LogDetailDrawerProps) {
  const isOpen = Boolean(detail || loading || error);
  const drawerRef = useRef<HTMLElement>(null);
  const closeButtonRef = useRef<HTMLButtonElement>(null);
  const orderedDetail = detail
    ? [...detail].sort((left, right) => {
        const typeDifference = logTypeOrder(left.logType) - logTypeOrder(right.logType);

        if (typeDifference !== 0) {
          return typeDifference;
        }

        return new Date(left.date).getTime() - new Date(right.date).getTime();
      })
    : null;

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    const previousActiveElement = document.activeElement as HTMLElement | null;
    const previousBodyOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    const focusFrame = window.requestAnimationFrame(() => closeButtonRef.current?.focus());

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === "Escape") {
        onClose();
        return;
      }

      if (event.key === "Tab") {
        const focusable = Array.from(
          drawerRef.current?.querySelectorAll<HTMLElement>(
            'button:not([disabled]), a[href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'
          ) ?? []
        ).filter((element) => element.offsetParent !== null);

        if (focusable.length === 0) {
          event.preventDefault();
          drawerRef.current?.focus();
          return;
        }

        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (event.shiftKey && document.activeElement === first) {
          event.preventDefault();
          last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
          event.preventDefault();
          first.focus();
        }
      }
    }

    window.addEventListener("keydown", handleKeyDown);
    return () => {
      window.cancelAnimationFrame(focusFrame);
      window.removeEventListener("keydown", handleKeyDown);
      document.body.style.overflow = previousBodyOverflow;
      previousActiveElement?.focus();
    };
  }, [isOpen, onClose]);

  if (!detail && !loading && !error) {
    return null;
  }

  return (
    <div className="drawer-backdrop" onPointerDown={(event) => event.currentTarget === event.target && onClose()}>
      <aside
        ref={drawerRef}
        className="detail-drawer"
        role="dialog"
        aria-modal="true"
        aria-labelledby="log-detail-title"
        tabIndex={-1}
      >
        <div className="drawer-header">
          <div>
            <p className="eyebrow">Информация о логе</p>
            <h2 id="log-detail-title">Детали записи</h2>
          </div>
          <button
            ref={closeButtonRef}
            type="button"
            className="icon-button"
            title="Закрыть"
            aria-label="Закрыть"
            onClick={onClose}
          >
            <X size={18} />
          </button>
        </div>

        {loading && <LoadingBlock />}
        <Alert tone="error">{error}</Alert>
        {orderedDetail?.map((item) => (
          <div className="detail-grid detail-log-entry" key={item.id}>
            <InfoRow label="ID" value={item.id} />
            <InfoRow label="Микросервис" value={item.microservice} />
            <InfoRow label="Метод" value={item.actionRus} />
            <InfoRow label="Пользователь" value={item.username} />
            <InfoRow label="Тип лога" value={formatLogType(item.logType)} />
            <InfoRow label="Статус" value={item.requestStatus ?? "-"} />
            <InfoRow label="Дата" value={formatDateTimeWithSeconds(item.date)} />
            <InfoRow label="Длительность" value={formatDuration(item.duration)} />
            <div className="json-box">
              <pre>{JSON.stringify(item.json, null, 2)}</pre>
            </div>
          </div>
        ))}
      </aside>
    </div>
  );
}

function logTypeOrder(logType?: string | null) {
  return formatLogType(logType) === "Инициализация" ? 0 : 1;
}
