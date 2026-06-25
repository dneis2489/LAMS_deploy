import { type FormEvent, useEffect, useRef, useState } from "react";
import { Pencil, Plus, RefreshCcw, Save, Search, Trash2, UserPlus, X } from "lucide-react";
import { Alert } from "../components/ui/Alert";
import { EmptyState } from "../components/ui/EmptyState";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { PasswordField } from "../components/ui/PasswordField";
import { SingleSelectDropdown } from "../components/ui/SingleSelectDropdown";
import { useUsersAdmin, type SearchMode } from "../hooks/useUsersAdmin";
import { formatDateTime } from "../utils/format";
import { roleLabel } from "../utils/roles";

const searchModeOptions = [
  { value: "email", label: "Email" },
  { value: "id", label: "ID" }
];

export function UsersAdminPage({ isAllowed }: { isAllowed: boolean }) {
  const admin = useUsersAdmin(isAllowed);
  const [isCreateOpen, setCreateOpen] = useState(false);
  const [isEditOpen, setEditOpen] = useState(false);
  const modalRef = useRef<HTMLElement>(null);
  const setEditId = admin.setEditId;
  const roleOptions = admin.roles.map((role) => ({
    value: String(role.id),
    label: roleLabel(role.name)
  }));

  useEffect(() => {
    if (!isCreateOpen && !isEditOpen) {
      return;
    }

    const previousActiveElement = document.activeElement as HTMLElement | null;
    const previousBodyOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    const focusFrame = window.requestAnimationFrame(() => modalRef.current?.focus());

    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === "Escape") {
        setCreateOpen(false);
        setEditOpen(false);
        setEditId("");
        return;
      }

      if (event.key === "Tab") {
        const focusable = Array.from(
          modalRef.current?.querySelectorAll<HTMLElement>(
            'button:not([disabled]), a[href], input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'
          ) ?? []
        ).filter((element) => element.offsetParent !== null);

        if (focusable.length === 0) {
          event.preventDefault();
          modalRef.current?.focus();
          return;
        }

        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (event.shiftKey && (document.activeElement === first || document.activeElement === modalRef.current)) {
          event.preventDefault();
          last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
          event.preventDefault();
          first.focus();
        }
      }
    }

    document.addEventListener("keydown", closeOnEscape);
    return () => {
      window.cancelAnimationFrame(focusFrame);
      document.removeEventListener("keydown", closeOnEscape);
      document.body.style.overflow = previousBodyOverflow;
      previousActiveElement?.focus();
    };
  }, [isCreateOpen, isEditOpen, setEditId]);

  if (!isAllowed) {
    return (
      <section className="panel">
        <EmptyState text="Раздел доступен только ROLE_SUPER_ADMIN" />
      </section>
    );
  }

  function closeEditModal() {
    setEditOpen(false);
    admin.setEditId("");
  }

  function openEditModal(user: (typeof admin.users)[number]) {
    admin.startEdit(user);
    setEditOpen(true);
  }

  async function handleSearch(event: FormEvent) {
    event.preventDefault();
    await admin.searchUsers();
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    const created = await admin.createUser();
    if (created) {
      setCreateOpen(false);
    }
  }

  async function handleEdit(event: FormEvent) {
    event.preventDefault();
    const updated = await admin.saveEdit(admin.editId);
    if (updated) {
      setEditOpen(false);
    }
  }

  return (
    <div className="view-stack">
      <section className="panel users-admin-panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">ROLE_SUPER_ADMIN</p>
            <h2>Управление пользователями</h2>
          </div>
          <div className="users-admin-actions">
            <button type="button" className="button primary" onClick={() => setCreateOpen(true)}>
              <UserPlus size={16} />
              Добавить пользователя
            </button>
            <button type="button" className="button ghost" onClick={admin.loadUsersAndRoles}>
              <RefreshCcw size={16} />
              Обновить
            </button>
          </div>
        </div>

        <form className="users-search-toolbar" onSubmit={handleSearch}>
          <SingleSelectDropdown
            label="Поиск"
            placeholder="Выберите поле"
            options={searchModeOptions}
            value={admin.searchMode}
            onChange={(value) => admin.setSearchMode(value as SearchMode)}
          />
          <label className="field users-search-main">
            <span>Запрос</span>
            <span className="users-search-control">
              <span className="search-field">
                <Search size={16} />
                <input
                  type="search"
                  value={admin.searchValue}
                  onChange={(event) => admin.setSearchValue(event.target.value)}
                  placeholder={admin.searchMode === "email" ? "email@example.com" : "UUID пользователя"}
                />
              </span>
              <button className="button primary smart-search-submit" type="submit" aria-label="Найти" title="Найти">
                <Search size={18} />
              </button>
            </span>
          </label>
        </form>

        <Alert tone="error">{admin.error}</Alert>
        <Alert tone="success">{admin.message}</Alert>

        <div className="table-header users-table-header">
          <div>
            <p className="eyebrow">{admin.users.length} записей</p>
            <h2>Пользователи</h2>
          </div>
        </div>

        {admin.loading ? (
          <LoadingBlock />
        ) : admin.users.length === 0 ? (
          <EmptyState text="Пользователи не найдены" />
        ) : (
          <div className="table-scroll mobile-card-scroll">
            <table className="data-table users-table mobile-card-table">
              <thead>
                <tr>
                  <th>Email</th>
                  <th>Username</th>
                  <th>Роль</th>
                  <th>Активен</th>
                  <th>Создан</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {admin.users.map((user) => (
                  <tr key={user.id}>
                    <td data-label="Email">{user.email}</td>
                    <td data-label="Username">{user.username}</td>
                    <td data-label="Роль">
                      <span className="role-chip">{roleLabel(user.role)}</span>
                    </td>
                    <td data-label="Активен">{user.enabled ? "Да" : "Нет"}</td>
                    <td data-label="Создан">{formatDateTime(user.createdAt)}</td>
                    <td className="row-actions mobile-card-actions" data-label="Действия">
                      <button
                        type="button"
                        className="icon-button"
                        title="Редактировать"
                        onClick={() => openEditModal(user)}
                      >
                        <Pencil size={17} />
                      </button>
                      <button
                        type="button"
                        className="icon-button danger"
                        title="Удалить"
                        onClick={() => admin.deleteUser(user.id)}
                        disabled={admin.saving}
                      >
                        <Trash2 size={17} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {isCreateOpen && (
        <div className="modal-backdrop" role="presentation" onPointerDown={() => setCreateOpen(false)}>
          <section
            ref={modalRef}
            className="modal-panel create-user-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="create-user-title"
            tabIndex={-1}
            onPointerDown={(event) => event.stopPropagation()}
          >
            <div className="modal-header">
              <div>
                <p className="eyebrow">Создание</p>
                <h2 id="create-user-title">Новый пользователь</h2>
              </div>
              <button type="button" className="icon-button" title="Закрыть" aria-label="Закрыть" onClick={() => setCreateOpen(false)}>
                <X size={18} />
              </button>
            </div>

            <form className="create-user-grid" onSubmit={handleCreate}>
              <label className="field">
                <span>Email</span>
                <input
                  type="email"
                  value={admin.newUser.email}
                  onChange={(event) => admin.setNewUser((current) => ({ ...current, email: event.target.value }))}
                  required
                />
              </label>
              <label className="field">
                <span>Username</span>
                <input
                  value={admin.newUser.username}
                  onChange={(event) => admin.setNewUser((current) => ({ ...current, username: event.target.value }))}
                  required
                />
              </label>
              <SingleSelectDropdown
                label="Роль"
                placeholder="Выберите роль"
                options={roleOptions}
                value={admin.newUser.roleId}
                onChange={(value) => admin.setNewUser((current) => ({ ...current, roleId: value }))}
              />
              <PasswordField
                label="Пароль"
                value={admin.newUser.password}
                onChange={(value) => admin.setNewUser((current) => ({ ...current, password: value }))}
                autoComplete="new-password"
                required
              />
              <PasswordField
                label="Повтор пароля"
                value={admin.newUser.confirmPassword}
                onChange={(value) => admin.setNewUser((current) => ({ ...current, confirmPassword: value }))}
                autoComplete="new-password"
                required
              />
              <button className="button primary align-end" type="submit" disabled={admin.saving}>
                <Plus size={16} />
                Создать
              </button>
            </form>
          </section>
        </div>
      )}

      {isEditOpen && (
        <div className="modal-backdrop" role="presentation" onPointerDown={closeEditModal}>
          <section
            ref={modalRef}
            className="modal-panel edit-user-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="edit-user-title"
            tabIndex={-1}
            onPointerDown={(event) => event.stopPropagation()}
          >
            <div className="modal-header">
              <div>
                <p className="eyebrow">Редактирование</p>
                <h2 id="edit-user-title">Пользователь</h2>
              </div>
              <button type="button" className="icon-button" title="Закрыть" aria-label="Закрыть" onClick={closeEditModal}>
                <X size={18} />
              </button>
            </div>

            <form className="edit-user-grid" onSubmit={handleEdit}>
              <label className="field">
                <span>Email</span>
                <input
                  type="email"
                  value={admin.editForm.email}
                  onChange={(event) => admin.setEditForm((current) => ({ ...current, email: event.target.value }))}
                  required
                />
              </label>
              <label className="field">
                <span>Username</span>
                <input
                  value={admin.editForm.username}
                  onChange={(event) => admin.setEditForm((current) => ({ ...current, username: event.target.value }))}
                  required
                />
              </label>
              <SingleSelectDropdown
                label="Роль"
                placeholder="Выберите роль"
                options={roleOptions}
                value={admin.editForm.roleId}
                onChange={(value) => admin.setEditForm((current) => ({ ...current, roleId: value }))}
              />
              <PasswordField
                label="Новый пароль"
                value={admin.editForm.password}
                onChange={(value) => admin.setEditForm((current) => ({ ...current, password: value }))}
                autoComplete="new-password"
              />
              <div className="field user-status-field">
                <span>Статус</span>
                <div className="status-toggle-group" role="group" aria-label="Статус пользователя">
                  <button
                    type="button"
                    className={`status-toggle${admin.editForm.enabled ? " active" : ""}`}
                    aria-pressed={admin.editForm.enabled}
                    onClick={() => admin.setEditForm((current) => ({ ...current, enabled: true }))}
                  >
                    Активен
                  </button>
                  <button
                    type="button"
                    className={`status-toggle${!admin.editForm.enabled ? " active" : ""}`}
                    aria-pressed={!admin.editForm.enabled}
                    onClick={() => admin.setEditForm((current) => ({ ...current, enabled: false }))}
                  >
                    Отключен
                  </button>
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="button ghost" onClick={closeEditModal}>
                  <X size={16} />
                  Отмена
                </button>
                <button className="button primary" type="submit" disabled={admin.saving}>
                  <Save size={16} />
                  Сохранить
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </div>
  );
}
