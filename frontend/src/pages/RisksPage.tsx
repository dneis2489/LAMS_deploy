import { CircleHelp, RefreshCcw } from "lucide-react";
import { useState } from "react";
import { Alert } from "../components/ui/Alert";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { SegmentedControl } from "../components/ui/SegmentedControl";
import { periodOptions } from "../constants/options";
import { useRisks } from "../hooks/useRisks";
import type { StatisticPeriod } from "../types";
import { formatPercent, riskTone, translateRiskLevel, translateRiskReason } from "../utils/risks";

export function RisksPage() {
  const risks = useRisks();
  const [riskHelpOpen, setRiskHelpOpen] = useState(false);
  const [degradationHelpOpen, setDegradationHelpOpen] = useState(false);

  return (
    <div className="view-stack">
      <section className="panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">Аналитика</p>
            <h2>Риски и деградация</h2>
          </div>
          <button type="button" className="button ghost" onClick={risks.reload}>
            <RefreshCcw size={16} />
            Обновить
          </button>
        </div>

        <div className="control-row">
          <SegmentedControl
            label="Период"
            options={periodOptions}
            value={risks.period}
            onChange={(value) => risks.setPeriod(value as StatisticPeriod)}
          />
        </div>

        <Alert tone="error">{risks.error}</Alert>
      </section>

      <section className="panel risk-panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">Риск</p>
            <h2>Индекс риска метода</h2>
          </div>
          <button
            type="button"
            className="icon-button help-button inline-help-button"
            aria-label="Пояснение к индексу риска"
            title="Пояснение"
            onClick={() => setRiskHelpOpen((isOpen) => !isOpen)}
          >
            <CircleHelp size={18} />
          </button>
        </div>

        {riskHelpOpen && (
          <div className="info-note">
            <strong>Что показывает эта статистика:</strong> таблица оценивает текущее состояние методов за выбранный
            период и показывает какой метод представляет угрозу для стабильной работоспособности системы. «Микросервис» и «Метод» определяют анализируемую операцию. «Индекс риска» — итоговая оценка
            неблагоприятного состояния метода, а «Уровень» — категория риска. «Запросы» — число уникальных
            обращений, «Ошибки» — количество ответов 4xx и 5xx, «Доля ошибок» — часть запросов, завершившихся ошибкой.
            «Задержка» показывает среднее время выполнения, «Причина» — показатель с наибольшим вкладом в индекс.
          </div>
        )}

        {risks.loading ? (
          <LoadingBlock />
        ) : (
          <div className="table-scroll mobile-card-scroll">
            <table className="data-table risk-table mobile-card-table">
              <thead>
                <tr>
                  <th>Микросервис</th>
                  <th>Метод</th>
                  <th>Индекс риска</th>
                  <th>Уровень</th>
                  <th>Запросы</th>
                  <th>Ошибки</th>
                  <th>Доля ошибок</th>
                  <th>Задержка</th>
                  <th>Причина</th>
                </tr>
              </thead>
              <tbody>
                {risks.methodRisks.map((item) => (
                  <tr key={`${item.microserviceName}-${item.actionName}`}>
                    <td data-label="Микросервис">{item.microserviceName}</td>
                    <td data-label="Метод">{item.actionName}</td>
                    <td data-label="Индекс риска">
                      <div className="risk-meter">
                        <span style={{ width: `${Math.min(item.riskScore, 100)}%` }} />
                      </div>
                      <strong>{item.riskScore}</strong>
                    </td>
                    <td data-label="Уровень">
                      <span className={`status ${riskTone(item.riskLevel)}`}>
                        {translateRiskLevel(item.riskLevel)}
                      </span>
                    </td>
                    <td data-label="Запросы">{item.requestCount}</td>
                    <td data-label="Ошибки">{item.errorCount + item.serverErrorCount}</td>
                    <td data-label="Доля ошибок">{formatPercent(item.errorRate)}</td>
                    <td data-label="Задержка">{Math.round(item.avgDurationMs)} ms</td>
                    <td data-label="Причина">{translateRiskReason(item.mainReason)}</td>
                  </tr>
                ))}
                {risks.methodRisks.length === 0 && (
                  <tr>
                    <td colSpan={9}>Нет данных для расчета риска</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <section className="panel risk-panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">Root cause</p>
            <h2>Локализация источника деградации</h2>
          </div>
          <button
            type="button"
            className="icon-button help-button inline-help-button"
            aria-label="Пояснение к локализации деградации"
            title="Пояснение"
            onClick={() => setDegradationHelpOpen((isOpen) => !isOpen)}
          >
            <CircleHelp size={18} />
          </button>
        </div>

        {degradationHelpOpen && (
          <div className="info-note">
            <strong>Что показывает эта статистика:</strong> таблица помогает найти методы, которые сильнее всего
            влияют на ухудшение системы по сравнению с предыдущим равным периодом. «Микросервис» и «Метод» определяют
            анализируемую операцию. «Вклад» показывает относительное участие метода в общей деградации, «Запросы» —
            число уникальных обращений в текущем периоде. «Изменение ошибок» отражает рост доли ответов 4xx и 5xx,
            «Изменение задержки» — относительный рост среднего времени выполнения. «Изменение трафика» показывает
            проблемное изменение нагрузки с учетом ошибок, «Незавершенные» — рост доли запросов без завершения.
            «Причина» указывает показатель с наибольшим вкладом в деградацию метода.
          </div>
        )}

        {risks.loading ? (
          <LoadingBlock />
        ) : (
          <div className="table-scroll mobile-card-scroll">
            <table className="data-table risk-table mobile-card-table">
              <thead>
                <tr>
                  <th>Микросервис</th>
                  <th>Метод</th>
                  <th>Вклад</th>
                  <th>Запросы</th>
                  <th>Изменение ошибок</th>
                  <th>Изменение задержки</th>
                  <th>Изменение трафика</th>
                  <th>Незавершенные</th>
                  <th>Причина</th>
                </tr>
              </thead>
              <tbody>
                {risks.degradationSources.map((item) => (
                  <tr key={`${item.microserviceName}-${item.actionName}`}>
                    <td data-label="Микросервис">{item.microserviceName}</td>
                    <td data-label="Метод">{item.actionName}</td>
                    <td data-label="Вклад">
                      <div className="risk-meter">
                        <span style={{ width: `${Math.min(item.contributionPercent, 100)}%` }} />
                      </div>
                      <strong>{item.contributionPercent}%</strong>
                    </td>
                    <td data-label="Запросы">{item.requestCount}</td>
                    <td data-label="Изменение ошибок">{(item.errorRateDelta * 100).toFixed(2)}%</td>
                    <td data-label="Изменение задержки">{formatPercent(item.durationGrowthRate)}</td>
                    <td data-label="Изменение трафика">{formatPercent(item.trafficImpactRate)}</td>
                    <td data-label="Незавершенные">{(item.unfinishedRateDelta * 100).toFixed(2)}%</td>
                    <td data-label="Причина">{translateRiskReason(item.reason)}</td>
                  </tr>
                ))}
                {risks.degradationSources.length === 0 && (
                  <tr>
                    <td colSpan={9}>Нет выраженной деградации за выбранный период</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}
