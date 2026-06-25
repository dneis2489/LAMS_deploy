import { useEffect, useState } from "react";
import { api } from "../api";
import type { DegradationSourceDTO, MethodRiskDTO, StatisticPeriod } from "../types";
import { errorMessage } from "../utils/format";

export function useRisks() {
  const [period, setPeriod] = useState<StatisticPeriod>("day");
  const [methodRisks, setMethodRisks] = useState<MethodRiskDTO[]>([]);
  const [degradationSources, setDegradationSources] = useState<DegradationSourceDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError("");

    Promise.all([
      api.getMethodRisks(period),
      api.getDegradationSources(period)
    ])
      .then(([risks, sources]) => {
        if (!ignore) {
          setMethodRisks(risks);
          setDegradationSources(sources);
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
  }, [period, reloadKey]);

  return {
    period,
    methodRisks,
    degradationSources,
    loading,
    error,
    setPeriod,
    reload: () => setReloadKey((value) => value + 1)
  };
}
