import type { PageLoad } from './$types';
import { listEarnings } from '$lib/api/earnings';

export const load: PageLoad = async () => {
  try {
    return { earnings: await listEarnings() };
  } catch {
    return { earnings: [] };
  }
};
