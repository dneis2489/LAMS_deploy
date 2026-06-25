import { useEffect, useMemo, useState } from "react";
import { api } from "../api";
import type { NotificationCategoryDTO, NotificationSettingDTO } from "../types";

export function useNotifications() {
  const [categories, setCategories] = useState<NotificationCategoryDTO[]>([]);
  const [settings, setSettings] = useState<NotificationSettingDTO[]>([]);
  const [enabledCategories, setEnabledCategories] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  async function loadSettings() {
    setLoading(true);
    setError("");
    try {
      const [categoriesResponse, settingsResponse] = await Promise.all([
        api.getNotificationCategories(),
        api.getNotificationSettings()
      ]);
      setCategories(categoriesResponse);
      setSettings(settingsResponse);
      setEnabledCategories(settingsResponse.filter((item) => item.enabled).map((item) => item.categoryCode));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Не удалось загрузить настройки уведомлений");
    } finally {
      setLoading(false);
    }
  }

  async function saveSettings() {
    setSaving(true);
    setError("");
    setMessage("");
    try {
      await api.updateNotificationSettings(enabledCategories);
      setMessage("Настройки уведомлений сохранены");
      await loadSettings();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Не удалось сохранить настройки уведомлений");
    } finally {
      setSaving(false);
    }
  }

  function toggleCategory(code: string) {
    setEnabledCategories((current) =>
      current.includes(code) ? current.filter((item) => item !== code) : [...current, code]
    );
  }

  const visibleCategories = useMemo(() => {
    const titleByCode = new Map(settings.map((item) => [item.categoryCode, item.categoryTitle]));
    return categories.map((category) => ({
      ...category,
      title: titleByCode.get(category.code) ?? category.title
    }));
  }, [categories, settings]);

  useEffect(() => {
    void loadSettings();
  }, []);

  return {
    categories: visibleCategories,
    enabledCategories,
    loading,
    saving,
    error,
    message,
    loadSettings,
    saveSettings,
    setEnabledCategories,
    toggleCategory
  };
}
