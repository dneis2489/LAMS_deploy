import { useState, type FormEvent } from "react";
import { Activity, Loader2, Mail, Shield } from "lucide-react";
import { api } from "../api";
import { Alert } from "../components/ui/Alert";
import { PasswordField } from "../components/ui/PasswordField";
import { errorMessage } from "../utils/format";

export function AuthPage({ onAuth }: { onAuth: () => void }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      await api.login({ email, password });
      onAuth();
    } catch (loginError) {
      setError(errorMessage(loginError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel" aria-label="Авторизация">
        <div className="auth-card">
          <div className="auth-heading">
            <div className="auth-logo">
              <Shield size={24} />
            </div>
            <div>
              <p className="eyebrow">СЛММ ПНИПУ</p>
              <h1>Вход</h1>
            </div>
          </div>

          <form className="auth-form" onSubmit={handleSubmit}>
            <label className="field">
              <span>Email</span>
              <div className="input-with-icon">
                <Mail size={17} />
                <input
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  placeholder="Введите email"
                  autoComplete="email"
                  required
                />
              </div>
            </label>

            <PasswordField
              label="Пароль"
              value={password}
              onChange={setPassword}
              placeholder="Введите пароль"
              autoComplete="current-password"
              required
            />

            <Alert tone="error">{error}</Alert>

            <button className="button primary full" type="submit" disabled={loading}>
              {loading && <Loader2 className="spin" size={18} />}
              Войти
            </button>
          </form>
        </div>

        <div className="auth-visual" aria-label="Состояние аналитики">
          <div className="visual-header">
            <Activity size={18} />
            <span>Monitoring</span>
          </div>
          <div className="visual-lines">
            <span style={{ height: "36%" }} />
            <span style={{ height: "70%" }} />
            <span style={{ height: "52%" }} />
            <span style={{ height: "86%" }} />
            <span style={{ height: "64%" }} />
            <span style={{ height: "42%" }} />
            <span style={{ height: "76%" }} />
          </div>
          <div className="visual-footer">
            <span>Прогноз</span>
            <span>Логи</span>
            <span>Гант</span>
          </div>
        </div>
      </section>
    </main>
  );
}
