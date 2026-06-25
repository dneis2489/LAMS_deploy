type AlertProps = {
  tone: "error" | "success";
  children: string;
};

export function Alert({ tone, children }: AlertProps) {
  if (!children) {
    return null;
  }

  return <div className={`alert ${tone}`}>{children}</div>;
}
