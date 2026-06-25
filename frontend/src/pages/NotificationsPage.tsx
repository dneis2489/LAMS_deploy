import { Bell, RefreshCcw, Save } from "lucide-react";
import { Alert } from "../components/ui/Alert";
import { EmptyState } from "../components/ui/EmptyState";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { MultiSelectDropdown } from "../components/ui/MultiSelectDropdown";
import { useNotifications } from "../hooks/useNotifications";

export function NotificationsPage() {
  const notifications = useNotifications();

  return (
    <div className="view-stack">
      <section className="panel">
        <div className="panel-heading">
          <div>
            <p className="eyebrow">Email</p>
            <h2>Уведомления</h2>
          </div>
          <button type="button" className="button ghost" onClick={notifications.loadSettings}>
            <RefreshCcw size={16} />
            Обновить
          </button>
        </div>

        <Alert tone="error">{notifications.error}</Alert>
        <Alert tone="success">{notifications.message}</Alert>
      </section>

      <section className="panel">
        {notifications.loading ? (
          <LoadingBlock />
        ) : notifications.categories.length === 0 ? (
          <EmptyState text="Категории уведомлений не найдены" />
        ) : (
          <div className="notification-settings">
            <div className="notification-type-picker">
              <MultiSelectDropdown
                label="Типы уведомлений"
                placeholder="Выберите типы"
                options={notifications.categories.map((category) => ({
                  value: category.code,
                  label: category.title
                }))}
                value={notifications.enabledCategories}
                onChange={(value) => notifications.setEnabledCategories(value.map(String))}
              />
            </div>

            {notifications.categories.map((category) => {
              const enabled = notifications.enabledCategories.includes(category.code);

              return (
                <label key={category.code} className="notification-setting-row">
                  <input
                    type="checkbox"
                    checked={enabled}
                    onChange={() => notifications.toggleCategory(category.code)}
                  />
                  <span className="notification-setting-icon">
                    <Bell size={18} />
                  </span>
                  <span className="notification-setting-title">{category.title}</span>
                </label>
              );
            })}

            <div className="panel-actions">
              <button
                type="button"
                className="button primary"
                onClick={notifications.saveSettings}
                disabled={notifications.saving}
              >
                <Save size={16} />
                Сохранить
              </button>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}
