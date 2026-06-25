import { useEffect, useMemo, useState } from "react";
import { api } from "../api";
import type { MethodStatisticDTO, StatisticMetric, StatisticPeriod } from "../types";
import { buildMethodChart, buildTotalChart } from "../utils/charts";
import { errorMessage } from "../utils/format";

const statisticsTextCollator = new Intl.Collator("ru-RU", {
  numeric: true,
  sensitivity: "base"
});

export function useStatistics() {
  const [metric, setMetric] = useState<StatisticMetric>("count");
  const [period, setPeriod] = useState<StatisticPeriod>("day");
  const [totalRows, setTotalRows] = useState<unknown[]>([]);
  const [methodRows, setMethodRows] = useState<MethodStatisticDTO[]>([]);
  const [selectedMicroservice, setSelectedMicroservice] = useState("");
  const [selectedAction, setSelectedAction] = useState("");
  const [selectedTotalStatus, setSelectedTotalStatus] = useState("");
  const [selectedMethodStatus, setSelectedMethodStatus] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError("");

    Promise.all([
      api.getTotalStatistic(metric, period),
      api.getMethodStatistic(metric, period)
    ])
      .then(([total, methods]) => {
        if (!ignore) {
          setTotalRows(total);
          setMethodRows(methods);
        }
      })
      .catch((fetchError) => {
        if (!ignore) {
          setError(errorMessage(fetchError));
        }
      })
      .finally(() => {
        if (!ignore) {
          setLoading(false);
        }
      });

    return () => {
      ignore = true;
    };
  }, [metric, period, reloadKey]);

  const sortedMethodRows = useMemo(
    () =>
      methodRows
        .map((item) => ({
          ...item,
          actionList: [...item.actionList].sort((left, right) =>
            statisticsTextCollator.compare(left.action, right.action)
          )
        }))
        .sort((left, right) => statisticsTextCollator.compare(left.microserviceName, right.microserviceName)),
    [methodRows]
  );

  useEffect(() => {
    if (sortedMethodRows.length === 0) {
      setSelectedMicroservice("");
      return;
    }

    if (!sortedMethodRows.some((item) => item.microserviceName === selectedMicroservice)) {
      setSelectedMicroservice(sortedMethodRows[0].microserviceName);
    }
  }, [sortedMethodRows, selectedMicroservice]);

  const selectedMicroserviceData = useMemo(
    () => sortedMethodRows.find((item) => item.microserviceName === selectedMicroservice),
    [sortedMethodRows, selectedMicroservice]
  );

  useEffect(() => {
    const actions = selectedMicroserviceData?.actionList ?? [];

    if (actions.length === 0) {
      setSelectedAction("");
      return;
    }

    if (!actions.some((item) => item.action === selectedAction)) {
      setSelectedAction(actions[0].action);
    }
  }, [selectedAction, selectedMicroserviceData]);

  const selectedActionData = useMemo(
    () => selectedMicroserviceData?.actionList.find((item) => item.action === selectedAction),
    [selectedAction, selectedMicroserviceData]
  );

  const totalStatusOptions = useMemo(
    () =>
      metric === "status"
        ? (totalRows as Array<{ statusCode?: number }>)
            .map((item) => item.statusCode)
            .filter((item): item is number => item !== undefined)
            .sort((left, right) => left - right)
        : [],
    [metric, totalRows]
  );

  const methodStatusOptions = useMemo(
    () =>
      metric === "status"
        ? (selectedActionData?.codeList ?? [])
            .map((item) => item.statusCode)
            .filter((item): item is number => item !== undefined)
            .sort((left, right) => left - right)
        : [],
    [metric, selectedActionData]
  );

  useEffect(() => {
    if (selectedTotalStatus && !totalStatusOptions.includes(Number(selectedTotalStatus))) {
      setSelectedTotalStatus("");
    }
  }, [selectedTotalStatus, totalStatusOptions]);

  useEffect(() => {
    if (selectedMethodStatus && !methodStatusOptions.includes(Number(selectedMethodStatus))) {
      setSelectedMethodStatus("");
    }
  }, [methodStatusOptions, selectedMethodStatus]);

  const totalChart = useMemo(
    () => buildTotalChart(metric, totalRows, selectedTotalStatus ? Number(selectedTotalStatus) : undefined),
    [metric, selectedTotalStatus, totalRows]
  );
  const methodChart = useMemo(
    () => buildMethodChart(metric, selectedActionData, selectedMethodStatus ? Number(selectedMethodStatus) : undefined),
    [metric, selectedActionData, selectedMethodStatus]
  );

  return {
    metric,
    period,
    methodRows: sortedMethodRows,
    selectedMicroservice,
    selectedAction,
    selectedTotalStatus,
    selectedMethodStatus,
    selectedMicroserviceData,
    selectedActionData,
    totalStatusOptions,
    methodStatusOptions,
    totalChart,
    methodChart,
    methodCount: sortedMethodRows.reduce((count, item) => count + item.actionList.length, 0),
    loading,
    error,
    setMetric,
    setPeriod,
    setSelectedMicroservice,
    setSelectedAction,
    setSelectedTotalStatus,
    setSelectedMethodStatus,
    reload: () => setReloadKey((value) => value + 1)
  };
}
