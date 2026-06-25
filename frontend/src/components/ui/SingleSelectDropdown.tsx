import { useEffect, useId, useRef, useState } from "react";
import { Check, ChevronDown } from "lucide-react";

export type SingleSelectOption = {
  value: string;
  label: string;
};

type SingleSelectDropdownProps = {
  label: string;
  placeholder: string;
  options: SingleSelectOption[];
  value: string;
  onChange: (value: string) => void;
};

export function SingleSelectDropdown({
  label,
  placeholder,
  options,
  value,
  onChange
}: SingleSelectDropdownProps) {
  const labelId = useId();
  const rootRef = useRef<HTMLDivElement>(null);
  const [open, setOpen] = useState(false);
  const selected = options.find((option) => option.value === value);

  useEffect(() => {
    function handleDocumentClick(event: MouseEvent) {
      if (!rootRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleDocumentClick);
    return () => document.removeEventListener("mousedown", handleDocumentClick);
  }, []);

  function selectOption(optionValue: string) {
    onChange(optionValue);
    setOpen(false);
  }

  return (
    <div className="field dropdown-field" ref={rootRef}>
      <span id={labelId}>{label}</span>
      <button
        type="button"
        className={`dropdown-trigger ${open ? "open" : ""}`}
        aria-labelledby={labelId}
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
      >
        <span>{selected?.label ?? placeholder}</span>
        <ChevronDown size={16} />
      </button>

      {open && (
        <div className="dropdown-menu" role="listbox" aria-labelledby={labelId}>
          {options.length === 0 ? (
            <div className="dropdown-empty">Нет вариантов</div>
          ) : (
            options.map((option) => {
              const isSelected = option.value === value;

              return (
                <button
                  key={option.value}
                  type="button"
                  className={`dropdown-option ${isSelected ? "selected" : ""}`}
                  role="option"
                  aria-selected={isSelected}
                  onClick={() => selectOption(option.value)}
                >
                  <span className="dropdown-check">{isSelected && <Check size={14} />}</span>
                  <span>{option.label}</span>
                </button>
              );
            })
          )}
        </div>
      )}
    </div>
  );
}
