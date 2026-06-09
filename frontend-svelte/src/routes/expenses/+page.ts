import { listExpenses } from '$lib/api/expenses';
import type { Expense } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = ({ depends }) => {
  depends('app:expenses');
  return { expenses: listExpenses().catch((): Expense[] => []) };
};
