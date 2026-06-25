import type {
  ApiErrorResponse,
  AuthResponse,
  AuthSession,
  CreateUserDTO,
  DegradationSourceDTO,
  FullLogInfoDTO,
  JwtClaims,
  LogFilters,
  LogListDTO,
  LoginDTO,
  MethodRiskDTO,
  MethodStatisticDTO,
  MicrosAndActionListForFilterDTO,
  NotificationCategoryDTO,
  NotificationSettingDTO,
  RequestStatusListForFilterDTO,
  RoleDTO,
  StatisticMetric,
  StatisticPeriod,
  UpdateUserDTO,
  UserActivityGrouppingDTO,
  UserDTO
} from "./types";

const ACCESS_TOKEN_KEY = "lams.accessToken";
const REFRESH_TOKEN_KEY = "lams.refreshToken";
export const AUTH_EVENT = "lams-auth-changed";

const rawBaseUrl = process.env.API_BASE_URL?.trim() ?? "";
const API_BASE_URL = rawBaseUrl.endsWith("/") ? rawBaseUrl.slice(0, -1) : rawBaseUrl;

const totalStatisticEndpoints: Record<StatisticMetric, Record<StatisticPeriod, string>> = {
  count: {
    hour: "/lams/getCountRequestWithHour",
    day: "/lams/getCountRequestWithDay",
    month: "/lams/getCountRequestWithMounth"
  },
  status: {
    hour: "/lams/getCountRequestStatusWithHour",
    day: "/lams/getCountRequestStatusWithDay",
    month: "/lams/getCountRequestStatusMonth"
  },
  duration: {
    hour: "/lams/getDurationWithHour",
    day: "/lams/getDurationWithDay",
    month: "/lams/getDurationWithMonth"
  },
  unique: {
    hour: "/lams/getUniqueUserWithHour",
    day: "/lams/getUniqueUserWithDay",
    month: "/lams/getUniqueUserWithMonth"
  }
};

const methodStatisticEndpoints: Record<StatisticMetric, Record<StatisticPeriod, string>> = {
  count: {
    hour: "/lams/getCountRequestForMethodsWithHour",
    day: "/lams/getCountRequestForMethodsWithDay",
    month: "/lams/getCountRequestForMethodsWithMonth"
  },
  status: {
    hour: "/lams/getCountRequestStatusForMethodsWithHour",
    day: "/lams/getCountRequestStatusForMethodsWithDay",
    month: "/lams/getCountRequestStatusForMethodsMonth"
  },
  duration: {
    hour: "/lams/getDurationForMethodsWithHour",
    day: "/lams/getDurationForMethodsWithDay",
    month: "/lams/getDurationForMethodsWithMonth"
  },
  unique: {
    hour: "/lams/getUniqueUserForMethodsWithHour",
    day: "/lams/getUniqueUserForMethodsWithDay",
    month: "/lams/getUniqueUserForMethodsWithMonth"
  }
};

let refreshPromise: Promise<AuthResponse> | null = null;

export function getApiBaseUrl() {
  return API_BASE_URL || "Webpack proxy: /lams -> http://localhost:8080";
}

export function decodeJwt(token: string): JwtClaims {
  try {
    const payload = token.split(".")[1];
    const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
    const padded = normalized.padEnd(normalized.length + ((4 - (normalized.length % 4)) % 4), "=");
    return JSON.parse(atob(padded)) as JwtClaims;
  } catch {
    return {};
  }
}

export function getStoredSession(): AuthSession | null {
  const accessToken = localStorage.getItem(ACCESS_TOKEN_KEY);
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

  if (!accessToken || !refreshToken) {
    return null;
  }

  return {
    accessToken,
    refreshToken,
    claims: decodeJwt(accessToken)
  };
}

export function saveSession(response: AuthResponse) {
  localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
  window.dispatchEvent(new Event(AUTH_EVENT));
}

export function clearSession() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  window.dispatchEvent(new Event(AUTH_EVENT));
}

function endpoint(path: string) {
  return `${API_BASE_URL}${path}`;
}

function buildQuery(params: Record<string, unknown>) {
  const query = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === "") {
      return;
    }

    if (Array.isArray(value)) {
      value.forEach((item) => query.append(key, String(item)));
      return;
    }

    query.set(key, String(value));
  });

  const value = query.toString();
  return value ? `?${value}` : "";
}

async function parseError(response: Response): Promise<Error> {
  let payload: ApiErrorResponse | null = null;

  try {
    payload = (await response.json()) as ApiErrorResponse;
  } catch {
    payload = null;
  }

  const validation = payload?.validationErrors?.map((error) => `${error.field}: ${error.message}`).join("; ");
  const message = validation || payload?.message || payload?.error || `HTTP ${response.status}`;

  return new Error(message);
}

async function parseResponse<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get("content-type") ?? "";
  if (!contentType.includes("application/json")) {
    return (await response.text()) as T;
  }

  return (await response.json()) as T;
}

async function refreshTokens() {
  if (!refreshPromise) {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

    if (!refreshToken) {
      throw new Error("Нет refresh token");
    }

    refreshPromise = fetch(endpoint("/lams/refresh"), {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ refreshToken })
    })
      .then(async (response) => {
        if (!response.ok) {
          throw await parseError(response);
        }

        return parseResponse<AuthResponse>(response);
      })
      .then((response) => {
        saveSession(response);
        return response;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
}

async function apiRequest<T>(path: string, init: RequestInit = {}, auth = true): Promise<T> {
  const headers = new Headers(init.headers);

  if (init.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (auth) {
    const accessToken = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (accessToken) {
      headers.set("Authorization", `Bearer ${accessToken}`);
    }
  }

  const response = await fetch(endpoint(path), {
    ...init,
    headers
  });

  if (response.status === 401 && auth) {
    try {
      const refreshed = await refreshTokens();
      const retryHeaders = new Headers(headers);
      retryHeaders.set("Authorization", `Bearer ${refreshed.accessToken}`);

      const retryResponse = await fetch(endpoint(path), {
        ...init,
        headers: retryHeaders
      });

      if (!retryResponse.ok) {
        throw await parseError(retryResponse);
      }

      return parseResponse<T>(retryResponse);
    } catch (error) {
      clearSession();
      throw error;
    }
  }

  if (!response.ok) {
    throw await parseError(response);
  }

  return parseResponse<T>(response);
}

export const api = {
  async login(payload: LoginDTO) {
    const response = await apiRequest<AuthResponse>(
      "/lams/login",
      {
        method: "POST",
        body: JSON.stringify(payload)
      },
      false
    );
    saveSession(response);
    return response;
  },

  async logout() {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refreshToken) {
      clearSession();
      return;
    }

    try {
      await apiRequest<void>("/lams/logout", {
        method: "POST",
        body: JSON.stringify({ refreshToken })
      });
    } finally {
      clearSession();
    }
  },

  getLogs(page: number, pageSize: number, filters: LogFilters) {
    const hasFilters =
      filters.micros.length > 0 ||
      filters.action.length > 0 ||
      filters.requestStatus.length > 0 ||
      Boolean(filters.startDate) ||
      Boolean(filters.endDate) ||
      filters.withoutResponse;

    const path = hasFilters ? "/lams/loglist/filter" : "/lams/loglist";
    return apiRequest<LogListDTO[]>(
      `${path}${buildQuery({
        p: page,
        s: pageSize,
        micros: filters.micros,
        action: filters.action,
        requestStatus: filters.requestStatus,
        startDate: filters.startDate,
        endDate: filters.endDate,
        withoutResponse: filters.withoutResponse || undefined
      })}`
    );
  },

  getLogInfo(id: number) {
    return apiRequest<FullLogInfoDTO[]>(`/lams/loginfo${buildQuery({ id })}`);
  },

  smartSearchLogs(page: number, pageSize: number, query: string) {
    return apiRequest<LogListDTO[]>(
      `/lams/loglist/smart-search${buildQuery({
        p: page,
        s: pageSize,
        q: query
      })}`
    );
  },

  async getFilterOptions() {
    const [microActions, requestStatuses] = await Promise.all([
      apiRequest<MicrosAndActionListForFilterDTO[]>("/lams/getMicroserviceAndActionToFilter"),
      apiRequest<RequestStatusListForFilterDTO[]>("/lams/getRequestStatusToFilter")
    ]);

    return {
      microActions,
      requestStatuses
    };
  },

  getTotalStatistic(metric: StatisticMetric, period: StatisticPeriod) {
    return apiRequest<unknown[]>(totalStatisticEndpoints[metric][period]);
  },

  getMethodStatistic(metric: StatisticMetric, period: StatisticPeriod) {
    return apiRequest<MethodStatisticDTO[]>(methodStatisticEndpoints[metric][period]);
  },

  getMethodRisks(period: StatisticPeriod) {
    return apiRequest<MethodRiskDTO[]>(`/lams/method-risk${buildQuery({ period, limit: 10 })}`);
  },

  getDegradationSources(period: StatisticPeriod) {
    return apiRequest<DegradationSourceDTO[]>(`/lams/degradation-sources${buildQuery({ period, limit: 10 })}`);
  },

  getGant() {
    return apiRequest<UserActivityGrouppingDTO[]>("/lams/getGant");
  },

  getUsers() {
    return apiRequest<UserDTO[]>("/lams/findAllUsers");
  },

  findUserByEmail(email: string) {
    return apiRequest<UserDTO[]>(`/lams/findByEmail${buildQuery({ email })}`);
  },

  findUserById(id: string) {
    return apiRequest<UserDTO[]>(`/lams/findById${buildQuery({ id })}`);
  },

  getRoles() {
    return apiRequest<RoleDTO[]>("/lams/findAllRoles");
  },

  addUser(payload: CreateUserDTO) {
    return apiRequest<boolean>("/lams/addUser", {
      method: "POST",
      body: JSON.stringify(payload)
    });
  },

  updateUser(payload: UpdateUserDTO) {
    return apiRequest<boolean>("/lams/updateUser", {
      method: "POST",
      body: JSON.stringify(payload)
    });
  },

  deleteUser(id: string) {
    return apiRequest<boolean>(`/lams/deleteUser${buildQuery({ id })}`, {
      method: "DELETE"
    });
  },

  getNotificationCategories() {
    return apiRequest<NotificationCategoryDTO[]>("/lams/notifications/categories");
  },

  getNotificationSettings() {
    return apiRequest<NotificationSettingDTO[]>("/lams/notifications/settings");
  },

  updateNotificationSettings(enabledCategories: string[]) {
    return apiRequest<void>("/lams/notifications/settings", {
      method: "PUT",
      body: JSON.stringify({ enabledCategories })
    });
  }
};
