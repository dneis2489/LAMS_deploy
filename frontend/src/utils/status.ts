export function statusTone(status?: number | null) {
  if (status === undefined || status === null) {
    return "neutral";
  }

  if (status >= 500) {
    return "danger";
  }

  if (status >= 400) {
    return "warning";
  }

  if (status >= 300) {
    return "neutral";
  }

  return "success";
}
