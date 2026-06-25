type SegmentedControlProps = {
  label: string;
  options: Array<{ value: string; label: string }>;
  value: string;
  onChange: (value: string) => void;
};

export function SegmentedControl({ label, options, value, onChange }: SegmentedControlProps) {
  return (
    <div className="segmented-field">
      <span>{label}</span>
      <div className="segmented">
        {options.map((option) => (
          <button
            key={option.value}
            type="button"
            className={value === option.value ? "active" : ""}
            onClick={() => onChange(option.value)}
          >
            {option.label}
          </button>
        ))}
      </div>
    </div>
  );
}
