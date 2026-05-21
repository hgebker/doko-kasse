import type { Earning } from '$lib/types';

const BASE = '/api/v1';

interface EarningListResponse {
  _embedded?: { earningList: Earning[] };
}

async function json(res: Response): Promise<unknown> {
  if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text()}`);
  if (res.status === 204) return null;
  return res.json();
}

export async function listEarnings(): Promise<Earning[]> {
  const data = (await json(
    await fetch(`${BASE}/earnings`, { headers: { Accept: 'application/json' } })
  )) as EarningListResponse | null;
  return data?._embedded?.earningList ?? [];
}

export async function createEarning(earning: Omit<Earning, 'id'>): Promise<unknown> {
  return json(
    await fetch(`${BASE}/earnings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(earning)
    })
  );
}

export async function updateEarning(earning: Earning): Promise<unknown> {
  return json(
    await fetch(`${BASE}/earnings`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(earning)
    })
  );
}

export async function deleteEarning(id: string): Promise<unknown> {
  return json(await fetch(`${BASE}/earnings/${encodeURIComponent(id)}`, { method: 'DELETE' }));
}
