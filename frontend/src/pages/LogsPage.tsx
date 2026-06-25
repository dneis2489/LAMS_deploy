import { ChevronLeft, ChevronRight, Eye, Filter, RefreshCcw, Search, X } from "lucide-react";
import { LogDetailDrawer } from "../components/logs/LogDetailDrawer";
import { Ban } from "lucide-react";
import { Alert } from "../components/ui/Alert";
import { EmptyState } from "../components/ui/EmptyState";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { MultiSelectDropdown } from "../components/ui/MultiSelectDropdown";
import { useLogs } from "../hooks/useLogs";
import { formatDateTimeWithSeconds } from "../utils/format";
import { formatLogType } from "../utils/logs";
import { statusTone } from "../utils/status";

export function LogsPage() {
  const logs = useLogs();
  const toNumbers = (value: Array<string | number>) => value.map(Number);
  const openDatePicker = (input: HTMLInputElement) => {
    try {
      input.showPicker?.();
    } catch {
      input.focus();
    }
  };

  return (
    <div className="view-stack">
      <section className="panel logs-filter-panel">
        <form
          className="logs-filter-form"
          onSubmit={(event) => {
            event.preventDefault();
            logs.applySmartSearch();
          }}
        >
          <label className="field smart-search-field">
            <span>Умный поиск</span>
            <span className="smart-search-control">
              <span className="search-field">
                <Search size={17} />
                <input
                  type="search"
                  value={logs.smartQuery}
                  onChange={(event) => logs.setSmartQuery(event.target.value)}
                  placeholder="Например: логи с ip 192.168.1.10 из Chrome или пользователь admin статус 500"
                />
              </span>
              <button type="submit" className="button primary smart-search-submit" aria-label="Найти" title="Найти">
                <Search size={18} />
              </button>
            </span>
          </label>

          <div className="log-filter-row">
            <MultiSelectDropdown
              label="Микросервисы"
              placeholder="Все микросервисы"
              options={logs.microservices.map((item) => ({ value: item.id, label: item.name }))}
              value={logs.filters.micros}
              onChange={(value) => logs.setMicroserviceFilter(toNumbers(value))}
            />

            <MultiSelectDropdown
              label="Методы"
              placeholder="Все методы"
              options={logs.availableActions.map((item) => ({
                value: item.id,
                label: item.actionRu || item.actionEng
              }))}
              value={logs.filters.action}
              onChange={(value) => logs.updateFilter("action", toNumbers(value))}
            />

            <MultiSelectDropdown
              label="Статусы"
              placeholder="Все статусы"
              options={[...logs.requestStatuses]
                .sort((left, right) => Number(left.name) - Number(right.name))
                .map((item) => ({ value: item.id, label: `HTTP ${item.name}` }))}
              value={logs.filters.requestStatus}
              onChange={(value) => logs.updateFilter("requestStatus", toNumbers(value))}
            />

            <label className="field compact-field">
              <span>Дата от</span>
              <input
                type="date"
                value={logs.filters.startDate}
                onClick={(event) => openDatePicker(event.currentTarget)}
                onKeyDown={(event) => {
                  if (event.key === "Enter" || event.key === " ") {
                    openDatePicker(event.currentTarget);
                  }
                }}
                onChange={(event) => logs.updateFilter("startDate", event.target.value)}
              />
            </label>

            <label className="field compact-field">
              <span>Дата до</span>
              <input
                type="date"
                value={logs.filters.endDate}
                onClick={(event) => openDatePicker(event.currentTarget)}
                onKeyDown={(event) => {
                  if (event.key === "Enter" || event.key === " ") {
                    openDatePicker(event.currentTarget);
                  }
                }}
                onChange={(event) => logs.updateFilter("endDate", event.target.value)}
              />
            </label>

            <button
              type="button"
              className={`button no-response-toggle${logs.filters.withoutResponse ? " active" : ""}`}
              aria-pressed={logs.filters.withoutResponse}
              onClick={() => logs.updateFilter("withoutResponse", !logs.filters.withoutResponse)}
            >
              <Ban size={16} />
              Без ответов
            </button>

            <button type="button" className="button primary" onClick={logs.applyFilters}>
              <Filter size={16} />
              Применить
            </button>
            <button type="button" className="button ghost" onClick={logs.resetFilters}>
              <X size={16} />
              Сбросить
            </button>
            <button
              type="button"
              className="button ghost refresh-button"
              onClick={() => {
                logs.setPage(1);
                logs.reload();
              }}
            >
              <RefreshCcw size={16} />
              Обновить
            </button>
            <label className="inline-control page-size-control">
              <span>На странице</span>
              <select value={logs.pageSize} onChange={(event) => logs.setPageSize(Number(event.target.value))}>
                {[10, 15, 25, 50].map((size) => (
                  <option key={size} value={size}>
                    {size}
                  </option>
                ))}
              </select>
            </label>
          </div>
        </form>
      </section>

      <section className="panel logs-list-panel">
        <div className="table-header">
          <div>
            <p className="eyebrow">Страница {logs.page}</p>
            <h2>Найденные записи</h2>
          </div>
          <div className="pager">
            <button
              type="button"
              className="icon-button"
              title="Предыдущая страница"
              onClick={() => logs.setPage((current) => Math.max(1, current - 1))}
              disabled={logs.page === 1 || logs.loading}
            >
              <ChevronLeft size={18} />
            </button>
            <button
              type="button"
              className="icon-button"
              title="Следующая страница"
              onClick={() => logs.setPage((current) => current + 1)}
              disabled={logs.logs.length < logs.pageSize || logs.loading}
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>

        <Alert tone="error">{logs.error}</Alert>
        {logs.loading ? (
          <LoadingBlock />
        ) : logs.logs.length === 0 ? (
          <EmptyState text="Логи не найдены" />
        ) : (
            <div className="table-scroll logs-table-scroll mobile-card-scroll">
              <table className="data-table mobile-card-table logs-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Микросервис</th>
                  <th>Метод</th>
                  <th>Пользователь</th>
                  <th>Статус</th>
                  <th>Дата</th>
                  <th>Тип лога</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {logs.logs.map((log) => (
                  <tr
                    key={log.id}
                    className="log-row"
                    tabIndex={0}
                    onClick={() => logs.openDetail(log.id)}
                    onKeyDown={(event) => {
                      if (event.key === "Enter" || event.key === " ") {
                        event.preventDefault();
                        logs.openDetail(log.id);
                      }
                    }}
                  >
                    <td className="mono" data-label="ID">{log.id}</td>
                    <td data-label="Микросервис">{log.microservice}</td>
                    <td data-label="Метод">{log.actionRus}</td>
                    <td data-label="Пользователь">{log.username}</td>
                    <td data-label="Статус">
                      <span className="status-pair">
                        <span className={`status ${statusTone(log.startStatus)}`}>
                          {log.startStatus ?? "-"}
                        </span>
                        {log.finishStatus != null && (
                          <>
                            <span className="status-separator">→</span>
                            <span className={`status ${statusTone(log.finishStatus)}`}>
                              {log.finishStatus}
                            </span>
                          </>
                        )}
                      </span>
                    </td>
                    <td data-label="Дата">{formatDateTimeWithSeconds(log.date)}</td>
                    <td data-label="Тип лога">{formatLogType(log.logType)}</td>
                    <td className="row-actions mobile-card-actions" data-label="Действия">
                      <button
                        type="button"
                        className="icon-button"
                        title="Открыть лог"
                        onClick={(event) => {
                          event.stopPropagation();
                          logs.openDetail(log.id);
                        }}
                      >
                        <Eye size={17} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <LogDetailDrawer
        detail={logs.detail}
        loading={logs.detailLoading}
        error={logs.detailError}
        onClose={logs.closeDetail}
      />
    </div>
  );
}
