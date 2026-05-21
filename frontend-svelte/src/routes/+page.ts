import type { PageLoad } from './$types';
import { getCashReport } from '$lib/api/reports';

export const load: PageLoad = async () => {
  try {
    const report = await getCashReport();
    return { report };
  } catch {
    return { report: { totalExpenses: 0, incomeFromEarnings: 0, incomeFromEvenings: 0 } };
  }
};
