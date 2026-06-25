import { Clock } from "lucide-react";

export function EmptyState({ text }: { text: string }) {
  return (
    <div className="empty-state">
      <Clock size={22} />
      <span>{text}</span>
    </div>
  );
}
