import { useCallback, useEffect, useMemo, useState } from "react";
import { AUTH_EVENT, api, getStoredSession } from "../api";
import type { AuthSession } from "../types";
import { isSuperAdmin as checkSuperAdmin } from "../utils/roles";

export function useSession() {
  const [session, setSession] = useState<AuthSession | null>(() => getStoredSession());

  const syncSession = useCallback(() => {
    setSession(getStoredSession());
  }, []);

  useEffect(() => {
    window.addEventListener(AUTH_EVENT, syncSession);
    window.addEventListener("storage", syncSession);

    return () => {
      window.removeEventListener(AUTH_EVENT, syncSession);
      window.removeEventListener("storage", syncSession);
    };
  }, [syncSession]);

  const isSuperAdmin = useMemo(() => checkSuperAdmin(session?.claims.role), [session?.claims.role]);

  return {
    session,
    isSuperAdmin,
    syncSession,
    logout: api.logout
  };
}
