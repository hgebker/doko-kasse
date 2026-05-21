import type { PageLoad } from './$types';
import { getSemesterReport } from '$lib/api/reports';

export const load: PageLoad = async () => {
  try {
    return { report: await getSemesterReport(null) };
  } catch {
    return { report: null };
  }
};
