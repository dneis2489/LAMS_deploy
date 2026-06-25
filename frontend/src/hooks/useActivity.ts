import { useEffect, useMemo, useState } from "react";
import { api } from "../api";
import type { UserActivityGrouppingDTO } from "../types";
import { buildTimeline, type GanttGranularity } from "../utils/gantt";
import { errorMessage } from "../utils/format";

export function useActivity(granularity: GanttGranularity) {
  const [rows, setRows] = useState<UserActivityGrouppingDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError("");

    api
      .getGant()
      .then((data) => {
        if (!ignore) {
          setRows(data.sort((left, right) => right.count - left.count));
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
  }, [reloadKey]);

  const timeline = useMemo(() => buildTimeline(rows, granularity), [granularity, rows]);

  return {
    rows,
    timeline,
    loading,
    error,
    reload: () => setReloadKey((value) => value + 1)
  };
}
