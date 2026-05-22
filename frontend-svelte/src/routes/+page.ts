import { getCashReport } from '$lib/api/reports';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
  try {
    const report = await getCashReport();
    return { report };
  } catch {
    return {
      report: {
        totalExpenses: 0,
        incomeFromEarnings: 0,
        incomeFromEvenings: 0,
        totalIncome: 0,
        currentCash: 0
      }
    };
  }
};
