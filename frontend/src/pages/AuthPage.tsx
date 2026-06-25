import { useState, type FormEvent } from "react";
import { Activity, Loader2, Mail, Shield } from "lucide-react";
import { api } from "../api";
import { Alert } from "../components/ui/Alert";
import { PasswordField } from "../components/ui/PasswordField";
import { errorMessage } from "../utils/format";

type AuthField = "email" | "password" | "";

export function AuthPage({ onAuth }: { onAuth: () => void }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [emptyField, setEmptyField] = useState<AuthField>("");

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();

    if (!email.trim()) {
      setEmptyField("email");
      setError("");
      return;
    }

    if (!password.trim()) {
      setEmptyField("password");
      setError("");
      return;
    }

    setLoading(true);
    setError("");
    setEmptyField("");

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

          <form className="auth-form" onSubmit={handleSubmit} noValidate>
            <label className={`field ${emptyField === "email" ? "has-validation-error" : ""}`}>
              <span>Электронная почта</span>
              <div className="input-with-icon">
                <Mail size={17} />
                <input
                  type="email"
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value);
                    if (emptyField === "email") {
                      setEmptyField("");
                    }
                  }}
                  placeholder="Введите email"
                  autoComplete="email"
                  aria-invalid={emptyField === "email"}
                />
              </div>
              {emptyField === "email" && <span className="field-validation-bubble">Заполните это поле</span>}
            </label>

            <PasswordField
              label="Пароль"
              value={password}
              onChange={(value) => {
                setPassword(value);
                if (emptyField === "password") {
                  setEmptyField("");
                }
              }}
              placeholder="Введите пароль"
              autoComplete="current-password"
              className={emptyField === "password" ? "has-validation-error" : ""}
              validationMessage={emptyField === "password" ? "Заполните это поле" : ""}
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
            <span>Мониторинг</span>
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
