import type { Expense } from '$lib/types';

const BASE = '/api/v1';

interface ExpenseListResponse {
  _embedded?: { expenseList: Expense[] };
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

export async function listExpenses(): Promise<Expense[]> {
  const data = (await json(
    await fetch(`${BASE}/expenses`, { headers: { Accept: 'application/json' } })
  )) as ExpenseListResponse | null;
  return data?._embedded?.expenseList ?? [];
}

export async function createExpense(expense: Omit<Expense, 'id'>): Promise<void> {
  await json(
    await fetch(`${BASE}/expenses`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(expense)
    })
  );
}

export async function updateExpense(expense: Expense): Promise<void> {
  await json(
    await fetch(`${BASE}/expenses`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(expense)
    })
  );
}

export async function deleteExpense(id: string): Promise<void> {
  await json(await fetch(`${BASE}/expenses/${encodeURIComponent(id)}`, { method: 'DELETE' }));
}
