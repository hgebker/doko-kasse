import { listEarnings } from '$lib/api/earnings';
import type { Earning } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = ({ depends }) => {
  depends('app:earnings');
  return { earnings: listEarnings().catch((): Earning[] => []) };
};
