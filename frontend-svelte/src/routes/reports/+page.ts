import { getSemesterReport } from '$lib/api/reports';
import type { SemesterReport } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = ({ url, depends }) => {
  depends('app:reports');
  const semester = url.searchParams.get('semester');
  return {
    report: getSemesterReport(semester).catch((): SemesterReport | null => null)
  };
};
