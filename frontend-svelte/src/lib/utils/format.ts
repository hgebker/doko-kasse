export function capitalize(value: string): string {
  return value.charAt(0).toUpperCase() + value.slice(1);
}

export function formatNumber(value: number): string {
  return (
    value.toLocaleString('de-DE', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' €'
  );
}

export function formatDate(value: string): string {
  if (!value) return value;
  const d = new Date(value + 'T00:00:00');
  return d.toLocaleDateString('de-DE', { day: '2-digit', month: '2-digit', year: 'numeric' });
}
