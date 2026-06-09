import type { SemesterReport } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = () => ({ report: null as SemesterReport | null });
