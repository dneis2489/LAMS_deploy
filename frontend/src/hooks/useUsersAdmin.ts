import { useCallback, useEffect, useState } from "react";
import { api } from "../api";
import type { RoleDTO, UserDTO } from "../types";
import { errorMessage } from "../utils/format";
import { roleIdByName } from "../utils/roles";

export type SearchMode = "email" | "id";

export type EditableUserForm = {
  email: string;
  username: string;
  roleId: string;
  password: string;
  enabled: boolean;
};

export type NewUserForm = {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
  roleId: string;
};

const emptyNewUser: NewUserForm = {
  email: "",
  username: "",
  password: "",
  confirmPassword: "",
  roleId: ""
};

export function useUsersAdmin(isAllowed: boolean) {
  const [users, setUsers] = useState<UserDTO[]>([]);
  const [roles, setRoles] = useState<RoleDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [searchMode, setSearchMode] = useState<SearchMode>("email");
  const [searchValue, setSearchValue] = useState("");
  const [editId, setEditId] = useState("");
  const [editForm, setEditForm] = useState<EditableUserForm>({
    email: "",
    username: "",
    roleId: "",
    password: "",
    enabled: true
  });
  const [newUser, setNewUser] = useState<NewUserForm>(emptyNewUser);

  const loadUsersAndRoles = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [loadedUsers, loadedRoles] = await Promise.all([api.getUsers(), api.getRoles()]);
      setUsers(loadedUsers);
      setRoles(loadedRoles);
      setNewUser((current) => ({
        ...current,
        roleId: current.roleId || String(loadedRoles[0]?.id ?? "")
      }));
    } catch (fetchError) {
      setError(errorMessage(fetchError));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isAllowed) {
      loadUsersAndRoles();
    }
  }, [isAllowed, loadUsersAndRoles]);

  async function searchUsers() {
    setLoading(true);
    setError("");

    try {
      if (!searchValue.trim()) {
        await loadUsersAndRoles();
        return;
      }

      const result =
        searchMode === "email"
          ? await api.findUserByEmail(searchValue.trim())
          : await api.findUserById(searchValue.trim());
      setUsers(result);
    } catch (fetchError) {
      setError(errorMessage(fetchError));
    } finally {
      setLoading(false);
    }
  }

  async function createUser() {
    setSaving(true);
    setError("");
    setMessage("");

    try {
      await api.addUser({
        ...newUser,
        roleId: Number(newUser.roleId)
      });
      setMessage("Пользователь создан");
      setNewUser({
        ...emptyNewUser,
        roleId: String(roles[0]?.id ?? "")
      });
      await loadUsersAndRoles();
      return true;
    } catch (saveError) {
      setError(errorMessage(saveError));
      return false;
    } finally {
      setSaving(false);
    }
  }

  function startEdit(user: UserDTO) {
    setEditId(user.id);
    setEditForm({
      email: user.email,
      username: user.username,
      roleId: String(roleIdByName(roles, user.role)),
      password: "",
      enabled: user.enabled
    });
  }

  async function saveEdit(userId: string) {
    setSaving(true);
    setError("");
    setMessage("");

    try {
      await api.updateUser({
        id: userId,
        email: editForm.email,
        username: editForm.username,
        roleId: Number(editForm.roleId),
        password: editForm.password,
        enabled: editForm.enabled
      });
      setMessage("Пользователь обновлен");
      setEditId("");
      await loadUsersAndRoles();
      return true;
    } catch (saveError) {
      setError(errorMessage(saveError));
      return false;
    } finally {
      setSaving(false);
    }
  }

  async function deleteUser(id: string) {
    const confirmed = window.confirm("Удалить пользователя?");
    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError("");
    setMessage("");

    try {
      await api.deleteUser(id);
      setMessage("Пользователь удален");
      await loadUsersAndRoles();
    } catch (deleteError) {
      setError(errorMessage(deleteError));
    } finally {
      setSaving(false);
    }
  }

  return {
    users,
    roles,
    loading,
    saving,
    error,
    message,
    searchMode,
    searchValue,
    editId,
    editForm,
    newUser,
    setSearchMode,
    setSearchValue,
    setEditId,
    setEditForm,
    setNewUser,
    loadUsersAndRoles,
    searchUsers,
    createUser,
    startEdit,
    saveEdit,
    deleteUser
  };
}
