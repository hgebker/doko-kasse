import type { PageLoad } from './$types';
import { listExpenses } from '$lib/api/expenses';

export const load: PageLoad = async () => {
  try {
    return { expenses: await listExpenses() };
  } catch {
    return { expenses: [] };
  }
};
