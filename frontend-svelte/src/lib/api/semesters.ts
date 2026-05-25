import type { SemesterEntry } from '$lib/types';

const BASE = '/api/v1';

interface SemesterListResponse {
  _embedded?: { semesterList: Array<{ key: string; label: string; sortKey: number }> };
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

export async function listSemesters(): Promise<SemesterEntry[]> {
  const data = (await json(
    await fetch(`${BASE}/semester`, { headers: { Accept: 'application/json' } })
  )) as SemesterListResponse | null;
  return (data?._embedded?.semesterList ?? []).map((s) => ({ id: s.key, label: s.label, sortKey: s.sortKey }));
}

export async function createSemester(semester: SemesterEntry): Promise<void> {
  await json(
    await fetch(`${BASE}/semester`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ key: semester.id, label: semester.label, sortKey: semester.sortKey })
    })
  );
}

export async function updateSemester(semester: SemesterEntry): Promise<void> {
  await json(
    await fetch(`${BASE}/semester`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ key: semester.id, label: semester.label, sortKey: semester.sortKey })
    })
  );
}

export async function deleteSemester(id: string): Promise<void> {
  await json(await fetch(`${BASE}/semester/${encodeURIComponent(id)}`, { method: 'DELETE' }));
}
