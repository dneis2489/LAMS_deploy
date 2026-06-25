import { useEffect, useMemo, useState } from "react";
import { api } from "../api";
import { emptyLogFilters } from "../constants/options";
import type {
  FullLogInfoDTO,
  LogFilters,
  LogListDTO,
  MicrosAndActionListForFilterDTO,
  RequestStatusListForFilterDTO
} from "../types";
import { errorMessage } from "../utils/format";

export function useLogs() {
  const [logs, setLogs] = useState<LogListDTO[]>([]);
  const [microActions, setMicroActions] = useState<MicrosAndActionListForFilterDTO[]>([]);
  const [requestStatuses, setRequestStatuses] = useState<RequestStatusListForFilterDTO[]>([]);
  const [filters, setFilters] = useState<LogFilters>(emptyLogFilters);
  const [queryFilters, setQueryFilters] = useState<LogFilters>(emptyLogFilters);
  const [smartQuery, setSmartQuery] = useState("");
  const [querySmartSearch, setQuerySmartSearch] = useState("");
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(15);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [detail, setDetail] = useState<FullLogInfoDTO[] | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let ignore = false;

    api
      .getFilterOptions()
      .then((data) => {
        if (!ignore) {
          setMicroActions(data.microActions);
          setRequestStatuses(data.requestStatuses);
        }
      })
      .catch((fetchError) => {
        if (!ignore) {
          setError(errorMessage(fetchError));
        }
      });

    return () => {
      ignore = true;
    };
  }, []);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError("");

    const request = querySmartSearch
      ? api.smartSearchLogs(page, pageSize, querySmartSearch)
      : api.getLogs(page, pageSize, queryFilters);

    request
      .then((data) => {
        if (!ignore) {
          setLogs(data);
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
  }, [page, pageSize, queryFilters, querySmartSearch, reloadKey]);

  const microservices = useMemo(() => {
    const map = new Map<number, string>();
    microActions.forEach((item) => map.set(item.microId, item.microName));
    return [...map.entries()].map(([id, name]) => ({ id, name }));
  }, [microActions]);

  const availableActions = useMemo(() => {
    if (filters.micros.length === 0) {
      return microActions;
    }

    return microActions.filter((item) => filters.micros.includes(item.microId));
  }, [filters.micros, microActions]);

  function updateFilter<K extends keyof LogFilters>(key: K, value: LogFilters[K]) {
    setFilters((current) => ({ ...current, [key]: value }));
  }

  function setMicroserviceFilter(micros: number[]) {
    const allowed = new Set(
      microActions.filter((item) => micros.includes(item.microId)).map((item) => item.id)
    );

    setFilters((current) => ({
      ...current,
      micros,
      action: current.action.filter((id) => allowed.has(id))
    }));
  }

  function applyFilters() {
    const allowedActionIds = new Set(availableActions.map((item) => item.id));

    setQuerySmartSearch("");
    setQueryFilters({
      ...filters,
      action: filters.action.filter((id) => allowedActionIds.has(id))
    });
    setPage(1);
  }

  function applyWithoutResponseFilter(value: boolean) {
    const allowedActionIds = new Set(availableActions.map((item) => item.id));
    const nextFilters = {
      ...filters,
      withoutResponse: value,
      action: filters.action.filter((id) => allowedActionIds.has(id))
    };

    setFilters(nextFilters);
    setQuerySmartSearch("");
    setQueryFilters(nextFilters);
    setPage(1);
  }

  function resetFilters() {
    setFilters(emptyLogFilters);
    setQueryFilters(emptyLogFilters);
    setSmartQuery("");
    setQuerySmartSearch("");
    setPage(1);
  }

  function applySmartSearch() {
    const value = smartQuery.trim();
    setQuerySmartSearch(value);
    setQueryFilters(emptyLogFilters);
    setPage(1);
  }

  async function openDetail(id: number) {
    setDetailLoading(true);
    setDetailError("");
    setDetail(null);

    try {
      setDetail(await api.getLogInfo(id));
    } catch (fetchError) {
      setDetailError(errorMessage(fetchError));
    } finally {
      setDetailLoading(false);
    }
  }

  return {
    logs,
    microActions,
    requestStatuses,
    filters,
    smartQuery,
    querySmartSearch,
    page,
    pageSize,
    loading,
    error,
    detail,
    detailLoading,
    detailError,
    microservices,
    availableActions,
    setPage,
    setPageSize,
    updateFilter,
    setSmartQuery,
    setMicroserviceFilter,
    applyFilters,
    applyWithoutResponseFilter,
    applySmartSearch,
    resetFilters,
    openDetail,
    closeDetail: () => setDetail(null),
    reload: () => setReloadKey((value) => value + 1)
  };
}
