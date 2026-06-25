import { useEffect, useId, useMemo, useRef, useState } from "react";
import { Check, ChevronDown } from "lucide-react";

export type MultiSelectOption = {
  value: string | number;
  label: string;
};

type MultiSelectDropdownProps = {
  label: string;
  placeholder: string;
  options: MultiSelectOption[];
  value: Array<string | number>;
  onChange: (value: Array<string | number>) => void;
};

export function MultiSelectDropdown({
  label,
  placeholder,
  options,
  value,
  onChange
}: MultiSelectDropdownProps) {
  const labelId = useId();
  const rootRef = useRef<HTMLDivElement>(null);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    function handleDocumentClick(event: MouseEvent) {
      if (!rootRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleDocumentClick);
    return () => document.removeEventListener("mousedown", handleDocumentClick);
  }, []);

  const selectedLabels = useMemo(() => {
    const labels = new Map(options.map((option) => [option.value, option.label]));
    return value.map((item) => labels.get(item)).filter(Boolean) as string[];
  }, [options, value]);

  function toggleOption(optionValue: string | number) {
    if (value.includes(optionValue)) {
      onChange(value.filter((item) => item !== optionValue));
      return;
    }

    onChange([...value, optionValue]);
  }

  const buttonText =
    selectedLabels.length === 0
      ? placeholder
      : selectedLabels.length === 1
        ? selectedLabels[0]
        : `Выбрано: ${selectedLabels.length}`;

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
        <span>{buttonText}</span>
        <ChevronDown size={16} />
      </button>

      {open && (
        <div className="dropdown-menu" role="listbox" aria-labelledby={labelId}>
          {options.length === 0 ? (
            <div className="dropdown-empty">Нет вариантов</div>
          ) : (
            options.map((option) => {
              const selected = value.includes(option.value);

              return (
                <button
                  key={option.value}
                  type="button"
                  className={`dropdown-option ${selected ? "selected" : ""}`}
                  role="option"
                  aria-selected={selected}
                  onClick={() => toggleOption(option.value)}
                >
                  <span className="dropdown-check">{selected && <Check size={14} />}</span>
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
