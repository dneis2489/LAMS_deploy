import { lazy, Suspense, useEffect, useMemo, useState } from "react";
import { AppLayout } from "../components/layout/AppLayout";
import { LoadingBlock } from "../components/ui/LoadingBlock";
import { useSession } from "../hooks/useSession";
import { AuthPage } from "../pages/AuthPage";
import { navigationItems, type SectionId } from "./navigation";

const LogsPage = lazy(() => import("../pages/LogsPage").then((module) => ({ default: module.LogsPage })));
const StatisticsPage = lazy(() =>
  import("../pages/StatisticsPage").then((module) => ({ default: module.StatisticsPage }))
);
const RisksPage = lazy(() => import("../pages/RisksPage").then((module) => ({ default: module.RisksPage })));
const ActivityPage = lazy(() =>
  import("../pages/ActivityPage").then((module) => ({ default: module.ActivityPage }))
);
const NotificationsPage = lazy(() =>
  import("../pages/NotificationsPage").then((module) => ({ default: module.NotificationsPage }))
);
const UsersAdminPage = lazy(() =>
  import("../pages/UsersAdminPage").then((module) => ({ default: module.UsersAdminPage }))
);

const sectionPaths: Record<SectionId, string> = {
  logs: "/logs",
  statistics: "/statistics",
  risks: "/risks",
  activity: "/activity",
  notifications: "/notifications",
  users: "/users"
};

function normalizePathname(pathname: string) {
  const path = pathname.replace(/\/+$/, "");
  return path || "/";
}

function sectionFromUrl(): SectionId {
  const pathname = normalizePathname(window.location.pathname);
  const sectionByPath = navigationItems.find((item) => sectionPaths[item.id] === pathname)?.id;

  if (sectionByPath) {
    return sectionByPath;
  }

  const legacyHash = window.location.hash.replace(/^#/, "");
  const sectionByHash = navigationItems.find((item) => item.id === legacyHash)?.id;

  return sectionByHash ?? "logs";
}

function replaceUrlWithSection(section: SectionId) {
  window.history.replaceState({ section }, "", sectionPaths[section]);
}

function App() {
  const { session, isSuperAdmin, logout, syncSession } = useSession();
  const [activeSection, setActiveSection] = useState<SectionId>(sectionFromUrl);

  const visibleNavItems = useMemo(
    () => navigationItems.filter((item) => !item.superOnly || isSuperAdmin),
    [isSuperAdmin]
  );

  useEffect(() => {
    function syncSectionWithUrl() {
      setActiveSection(sectionFromUrl());
    }

    const section = sectionFromUrl();
    if (normalizePathname(window.location.pathname) !== sectionPaths[section] || window.location.hash) {
      replaceUrlWithSection(section);
    }

    window.addEventListener("popstate", syncSectionWithUrl);
    window.addEventListener("hashchange", syncSectionWithUrl);
    return () => {
      window.removeEventListener("popstate", syncSectionWithUrl);
      window.removeEventListener("hashchange", syncSectionWithUrl);
    };
  }, []);

  useEffect(() => {
    if (session && activeSection === "users" && !isSuperAdmin) {
      replaceUrlWithSection("logs");
      setActiveSection("logs");
    }
  }, [activeSection, isSuperAdmin, session]);

  function navigate(section: SectionId) {
    if (normalizePathname(window.location.pathname) !== sectionPaths[section]) {
      window.history.pushState({ section }, "", sectionPaths[section]);
    }
    setActiveSection(section);
  }

  if (!session) {
    return <AuthPage onAuth={syncSession} />;
  }

  return (
    <AppLayout
      activeSection={activeSection}
      navItems={visibleNavItems}
      session={session}
      onNavigate={navigate}
      onLogout={logout}
    >
      <Suspense fallback={<LoadingBlock />}>
        {activeSection === "logs" && <LogsPage />}
        {activeSection === "statistics" && <StatisticsPage />}
        {activeSection === "risks" && <RisksPage />}
        {activeSection === "activity" && <ActivityPage />}
        {activeSection === "notifications" && <NotificationsPage />}
        {activeSection === "users" && <UsersAdminPage isAllowed={isSuperAdmin} />}
      </Suspense>
    </AppLayout>
  );
}

export default App;
