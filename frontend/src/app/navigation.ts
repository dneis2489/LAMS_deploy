import { Activity, Bell, LineChart as LineChartIcon, ListFilter, ShieldAlert, Users, type LucideIcon } from "lucide-react";

export type SectionId = "logs" | "statistics" | "risks" | "activity" | "notifications" | "users";

export type NavigationItem = {
  id: SectionId;
  label: string;
  icon: LucideIcon;
  superOnly?: boolean;
};

export const navigationItems: NavigationItem[] = [
  { id: "logs", label: "Логи", icon: ListFilter },
  { id: "statistics", label: "Статистика", icon: LineChartIcon },
  { id: "risks", label: "Риски", icon: ShieldAlert },
  { id: "activity", label: "Активность", icon: Activity },
  { id: "notifications", label: "Уведомления", icon: Bell },
  { id: "users", label: "Пользователи", icon: Users, superOnly: true }
];
