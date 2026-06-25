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

function sectionFromUrl(): SectionId {
  const hash = window.location.hash.replace(/^#/, "");
  return navigationItems.some((item) => item.id === hash) ? (hash as SectionId) : "logs";
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

    if (!navigationItems.some((item) => `#${item.id}` === window.location.hash)) {
      window.history.replaceState({ section: "logs" }, "", "#logs");
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
      window.history.replaceState({ section: "logs" }, "", "#logs");
      setActiveSection("logs");
    }
  }, [activeSection, isSuperAdmin, session]);

  function navigate(section: SectionId) {
    if (window.location.hash !== `#${section}`) {
      window.history.pushState({ section }, "", `#${section}`);
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
