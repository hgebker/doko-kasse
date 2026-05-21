import type { CashReport, SemesterReport } from '$lib/types';

const BASE = '/api/v1';

async function json(res: Response): Promise<unknown> {
  if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text()}`);
  return res.json();
}

export async function getCashReport(): Promise<CashReport> {
  return (await json(
    await fetch(`${BASE}/reports/cash`, { headers: { Accept: 'application/json' } })
  )) as CashReport;
}

export async function getSemesterReport(semester: string | null): Promise<SemesterReport> {
  const url =
    semester && semester !== 'gesamt'
      ? `${BASE}/reports/semester?semester=${encodeURIComponent(semester)}`
      : `${BASE}/reports/semester`;
  return (await json(
    await fetch(url, { headers: { Accept: 'application/json' } })
  )) as SemesterReport;
}
