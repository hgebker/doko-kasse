import type { EveningDto, EveningInput } from '$lib/types';

const BASE = '/api/v1';

interface EveningListResponse {
  _embedded?: { eveningDTOList: EveningDto[] };
}

async function json(res: Response): Promise<unknown> {
  if (!res.ok) {
    const text = await res.text();
    let message = text;
    try {
      const body = JSON.parse(text);
      if (typeof body.message === 'string') message = body.message;
    } catch {
      // not JSON, use raw text
    }
    throw new Error(message);
  }
  if (res.status === 204 || res.status === 201) return null;
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

export async function createEvening(evening: EveningInput): Promise<void> {
  await json(
    await fetch(`${BASE}/evenings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(evening)
    })
  );
}

export async function updateEvening(evening: EveningInput): Promise<void> {
  await json(
    await fetch(`${BASE}/evenings`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(evening)
    })
  );
}

export async function deleteEvening(date: string): Promise<void> {
  await json(await fetch(`${BASE}/evenings/${encodeURIComponent(date)}`, { method: 'DELETE' }));
}
