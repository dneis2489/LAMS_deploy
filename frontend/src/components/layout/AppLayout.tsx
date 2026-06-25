import type { ReactNode } from "react";
import type { AuthSession } from "../../types";
import type { NavigationItem, SectionId } from "../../app/navigation";
import { Sidebar } from "./Sidebar";

type AppLayoutProps = {
  activeSection: SectionId;
  navItems: NavigationItem[];
  session: AuthSession;
  children: ReactNode;
  onNavigate: (section: SectionId) => void;
  onLogout: () => void;
};

export function AppLayout({
  activeSection,
  navItems,
  session,
  children,
  onNavigate,
  onLogout
}: AppLayoutProps) {
  return (
    <div className="app-shell">
      <Sidebar
        activeSection={activeSection}
        navItems={navItems}
        session={session}
        onNavigate={onNavigate}
        onLogout={onLogout}
      />
      <main className="workspace">
        <section className="content-area">{children}</section>
      </main>
    </div>
  );
}
