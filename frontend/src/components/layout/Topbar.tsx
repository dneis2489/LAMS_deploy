type TopbarProps = {
  title: string;
};

export function Topbar({ title }: TopbarProps) {
  return (
    <header className="topbar">
      <div>
        <p className="eyebrow">Система логирования микросервисов</p>
        <h1>{title}</h1>
      </div>
    </header>
  );
}
