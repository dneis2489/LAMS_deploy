import type { RoleDTO, RoleName } from "../types";

export function isSuperAdmin(role?: RoleName) {
  return role === "ROLE_SUPER_ADMIN";
}

export function roleLabel(role?: string) {
  if (role === "ROLE_SUPER_ADMIN") {
    return "Супер-администратор";
  }

  if (role === "ROLE_ADMIN") {
    return "Администратор";
  }

  return role || "Роль не определена";
}

export function roleIdByName(roles: RoleDTO[], roleName: string) {
  return roles.find((role) => role.name === roleName)?.id ?? roles[0]?.id ?? "";
}
