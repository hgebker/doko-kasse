import { listEvenings } from '$lib/api/evenings';
import { parseEveningDto } from '$lib/utils/parse';
import type { Evening, EveningDto } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = ({ url, depends }) => {
  depends('app:evenings');
  const semester = url.searchParams.get('semester');
  return {
    evenings: listEvenings(semester)
      .then((raw: EveningDto[]) => raw.map(parseEveningDto))
      .catch((): Evening[] => [])
  };
};
