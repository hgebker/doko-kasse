import { listSemesters } from '$lib/api/semesters';
import type { LayoutLoad } from './$types';

export const ssr = false;
export const prerender = false;

export const load: LayoutLoad = async ({ depends }) => {
  depends('app:semesters');
  try {
    return { semesters: await listSemesters() };
  } catch {
    return { semesters: [] };
  }
};
