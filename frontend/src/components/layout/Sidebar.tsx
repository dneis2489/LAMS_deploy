import { BarChart3, LogOut, Shield } from "lucide-react";
import type { NavigationItem, SectionId } from "../../app/navigation";
import type { AuthSession } from "../../types";
import { roleLabel } from "../../utils/roles";

type SidebarProps = {
  activeSection: SectionId;
  navItems: NavigationItem[];
  session: AuthSession;
  onNavigate: (section: SectionId) => void;
  onLogout: () => void;
};

export function Sidebar({ activeSection, navItems, session, onNavigate, onLogout }: SidebarProps) {
  const username = session.claims.username || session.claims.email || session.claims.sub || "Пользователь";

  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-mark">
          <BarChart3 size={22} />
        </div>
        <div>
          <div className="brand-title">СЛММ</div>
          <div className="brand-subtitle">ПНИПУ</div>
        </div>
      </div>

      <nav className="side-nav" aria-label="Основная навигация">
        {navItems.map((item) => {
          const Icon = item.icon;

          return (
            <button
              key={item.id}
              type="button"
              className={`nav-button ${activeSection === item.id ? "active" : ""}`}
              aria-current={activeSection === item.id ? "page" : undefined}
              onClick={() => onNavigate(item.id)}
            >
              <Icon size={18} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>

      <div className="header-profile">
        <div className="identity">
          <span className="identity-name">{username}</span>
          <span className="role-badge">
            <Shield size={14} />
            {roleLabel(session.claims.role)}
          </span>
        </div>
        <button type="button" className="icon-button logout-icon" title="Выйти" aria-label="Выйти" onClick={onLogout}>
          <LogOut size={18} />
        </button>
      </div>
    </aside>
  );
}
