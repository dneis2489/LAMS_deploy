export function InfoRow({ label, value }: { label: string; value: string | number | undefined }) {
  return (
    <div className="info-row">
      <span>{label}</span>
      <strong>{value ?? "-"}</strong>
    </div>
  );
}
