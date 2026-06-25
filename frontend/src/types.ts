export type RoleName = "ROLE_SUPER_ADMIN" | "ROLE_ADMIN" | string;

export type JwtClaims = {
  sub?: string;
  email?: string;
  username?: string;
  role?: RoleName;
  exp?: number;
  iat?: number;
};

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
};

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  claims: JwtClaims;
};

export type ApiErrorResponse = {
  timestamp?: string;
  status?: number;
  error?: string;
  code?: string;
  message?: string;
  path?: string;
  requestId?: string;
  validationErrors?: Array<{
    field: string;
    message: string;
  }>;
};

export type LoginDTO = {
  email: string;
  password: string;
};

export type CreateUserDTO = {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
  roleId: number;
};

export type UpdateUserDTO = {
  id: string;
  email: string;
  username: string;
  roleId: number;
  password: string;
  enabled: boolean;
};

export type UserDTO = {
  id: string;
  email: string;
  username: string;
  passwordHash: string;
  role: RoleName;
  enabled: boolean;
  createdAt?: string;
  updatedAt?: string;
};

export type RoleDTO = {
  id: number;
  name: RoleName;
};

export type LogListDTO = {
  id: number;
  microservice: string;
  actionRus: string;
  username: string;
  requestStatus: number | null;
  startStatus: number | null;
  finishStatus: number | null;
  date: string;
  logType: string;
};

export type LogJSONInfoData = Record<string, unknown> | null;

export type FullLogInfoDTO = LogListDTO & {
  json: LogJSONInfoData;
  duration: number;
};

export type MicrosAndActionListForFilterDTO = {
  id: number;
  microId: number;
  microName: string;
  actionEng: string;
  actionRu: string;
};

export type RequestStatusListForFilterDTO = {
  id: number;
  name: number;
};

export type LogFilters = {
  micros: number[];
  action: number[];
  requestStatus: number[];
  startDate?: string;
  endDate?: string;
  withoutResponse: boolean;
};

export type StatisticPeriod = "hour" | "day" | "month";
export type StatisticMetric = "count" | "status" | "duration" | "unique";

export type CountRequestStatDTO = {
  date: string;
  count: number;
  predict: number;
  anomaly: boolean;
};

export type DurationStatDTO = {
  date: string;
  minDuration: number;
  avgDuration: number;
  maxDuration: number;
  avgPredictDuration: number;
  anomaly: boolean;
};

export type UniqueUsersStatDTO = {
  id: number;
  date: string;
  count: number;
  predict: number;
  users: string[];
};

export type RequestStatusConvertDataForTotalDTO = {
  statusCode: number;
  countsStatusCodeList: Array<{
    date: string;
    count: number;
    predict: number | null;
    anomaly: boolean;
  }>;
};

export type MethodStatData = {
  date: string;
  count?: number;
  predict?: number;
  minDuration?: number;
  avgDuration?: number;
  maxDuration?: number;
  users?: string[];
};

export type MethodActionData = {
  action: string;
  statData?: MethodStatData[];
  codeList?: Array<{
    statusCode: number;
    countsStatusCodeList: Array<{
      date: string;
      count: number;
      predict: number | null;
      anomaly?: boolean;
    }>;
  }>;
};

export type MethodStatisticDTO = {
  microserviceName: string;
  actionList: MethodActionData[];
};

export type UserActivityData = {
  startDate: string;
  endDate: string;
  duration: number;
  microserviceName: string;
  actionName: string;
};

export type UserActivityGrouppingDTO = {
  userName: string;
  count: number;
  data: UserActivityData[];
};

export type NotificationCategoryDTO = {
  code: string;
  title: string;
};

export type NotificationSettingDTO = {
  categoryCode: string;
  categoryTitle: string;
  enabled: boolean;
};

export type MethodRiskDTO = {
  microserviceName: string;
  actionName: string;
  requestCount: number;
  errorCount: number;
  serverErrorCount: number;
  unfinishedCount: number;
  avgDurationMs: number;
  baselineRequestCount: number;
  baselineErrorRate: number;
  baselineAvgDurationMs: number;
  errorRate: number;
  durationGrowthRate: number;
  trafficDeviationRate: number;
  riskScore: number;
  riskLevel: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | string;
  mainReason: string;
};

export type DegradationSourceDTO = {
  microserviceName: string;
  actionName: string;
  requestCount: number;
  errorRateDelta: number;
  durationGrowthRate: number;
  trafficImpactRate: number;
  unfinishedRateDelta: number;
  degradationScore: number;
  contributionPercent: number;
  reason: string;
};
