import { getCashReport, getSemesterReport } from '$lib/api/reports';
import type { CashReport, SemesterReport } from '$lib/types';
import type { PageLoad } from './$types';

const EMPTY_CASH: CashReport = {
  totalExpenses: 0,
  incomeFromEarnings: 0,
  incomeFromEvenings: 0,
  totalIncome: 0,
  currentCash: 0
};

const EMPTY_SEMESTER: SemesterReport = {
  totalIncome: 0,
  numberOfEvenings: 0,
  evenings: [],
  semesterResults: [],
  best: { player: 'hannes', sum: 0, avg: 0, min: 0, max: 0 },
  worst: { player: 'hannes', sum: 0, avg: 0, min: 0, max: 0 }
};

export const load: PageLoad = async () => {
  const [report, semesterReport] = await Promise.all([
    getCashReport().catch(() => EMPTY_CASH),
    getSemesterReport(null).catch(() => EMPTY_SEMESTER)
  ]);
  return { report, semesterReport };
};
