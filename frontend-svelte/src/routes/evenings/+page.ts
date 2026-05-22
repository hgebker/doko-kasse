import type { PageLoad } from './$types';
import { listEvenings } from '$lib/api/evenings';
import { parseEveningDto } from '$lib/utils/parse';

export const load: PageLoad = async () => {
  try {
    const raw = await listEvenings(null);
    return { evenings: raw.map(parseEveningDto) };
  } catch {
    return { evenings: [] };
  }
};
