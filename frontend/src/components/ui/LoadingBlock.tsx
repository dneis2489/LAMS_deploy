import { Loader2 } from "lucide-react";

export function LoadingBlock() {
  return (
    <div className="loading-block">
      <Loader2 className="spin" size={22} />
      <span>Загрузка</span>
    </div>
  );
}
