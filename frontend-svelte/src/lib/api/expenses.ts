import type { Expense } from '$lib/types';

const BASE = '/api/v1';

interface ExpenseListResponse {
  _embedded?: { expenseList: Expense[] };
}

async function json(res: Response): Promise<unknown> {
  if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text()}`);
  if (res.status === 204) return null;
  return res.json();
}

export async function listExpenses(): Promise<Expense[]> {
  const data = (await json(
    await fetch(`${BASE}/expenses`, { headers: { Accept: 'application/json' } })
  )) as ExpenseListResponse | null;
  return data?._embedded?.expenseList ?? [];
}

export async function createExpense(expense: Omit<Expense, 'id'>): Promise<unknown> {
  return json(
    await fetch(`${BASE}/expenses`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(expense)
    })
  );
}

export async function updateExpense(expense: Expense): Promise<unknown> {
  return json(
    await fetch(`${BASE}/expenses`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(expense)
    })
  );
}

export async function deleteExpense(id: string): Promise<unknown> {
  return json(await fetch(`${BASE}/expenses/${encodeURIComponent(id)}`, { method: 'DELETE' }));
}
