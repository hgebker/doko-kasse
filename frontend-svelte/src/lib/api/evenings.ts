import type { EveningDto, EveningInput } from '$lib/types';

const BASE = '/api/v1';

interface EveningListResponse {
  _embedded?: { eveningDTOList: EveningDto[] };
}

async function json(res: Response): Promise<unknown> {
  if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text()}`);
  if (res.status === 204) return null;
  return res.json();
}

export async function listEvenings(semester: string | null): Promise<EveningDto[]> {
  const url =
    semester && semester !== 'gesamt'
      ? `${BASE}/evenings?semester=${encodeURIComponent(semester)}`
      : `${BASE}/evenings`;
  const data = (await json(
    await fetch(url, { headers: { Accept: 'application/json' } })
  )) as EveningListResponse | null;
  return data?._embedded?.eveningDTOList ?? [];
}

export async function createEvening(evening: EveningInput): Promise<unknown> {
  return json(
    await fetch(`${BASE}/evenings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(evening)
    })
  );
}

export async function updateEvening(evening: EveningInput): Promise<unknown> {
  return json(
    await fetch(`${BASE}/evenings`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(evening)
    })
  );
}

export async function deleteEvening(date: string): Promise<unknown> {
  return json(await fetch(`${BASE}/evenings/${encodeURIComponent(date)}`, { method: 'DELETE' }));
}
