import { useId, useState } from "react";
import { Eye, EyeOff, Lock } from "lucide-react";

type PasswordFieldProps = {
  label?: string;
  ariaLabel?: string;
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  autoComplete?: string;
  required?: boolean;
  disabled?: boolean;
  compact?: boolean;
  className?: string;
  validationMessage?: string;
};

export function PasswordField({
  label,
  ariaLabel,
  value,
  onChange,
  placeholder,
  autoComplete,
  required,
  disabled,
  compact,
  className = "",
  validationMessage,
}: PasswordFieldProps) {
  const [visible, setVisible] = useState(false);
  const id = useId();
  const toggleText = visible ? "Скрыть пароль" : "Показать пароль";

  const control = (
    <div className="password-control">
      {!compact && <Lock size={17} aria-hidden="true" />}
      <input
        id={label ? id : undefined}
        type={visible ? "text" : "password"}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        autoComplete={autoComplete}
        required={required}
        disabled={disabled}
        aria-label={label ? undefined : ariaLabel ?? placeholder}
        aria-invalid={validationMessage ? true : undefined}
      />
      <button
        type="button"
        className="password-toggle"
        onClick={() => setVisible((current) => !current)}
        aria-pressed={visible}
        aria-label={toggleText}
        title={toggleText}
        disabled={disabled}
      >
        {visible ? <EyeOff size={15} aria-hidden="true" /> : <Eye size={15} aria-hidden="true" />}
      </button>
    </div>
  );

  const classes = ["password-field", compact ? "compact" : "", className].filter(Boolean).join(" ");

  if (!label) {
    return <div className={classes}>{control}</div>;
  }

  return (
    <label className={`field ${classes}`} htmlFor={id}>
      <span>{label}</span>
      {control}
      {validationMessage && <span className="field-validation-bubble">{validationMessage}</span>}
    </label>
  );
}
