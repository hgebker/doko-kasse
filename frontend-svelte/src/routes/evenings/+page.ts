import type { Evening } from '$lib/types';
import type { PageLoad } from './$types';

export const load: PageLoad = () => ({ evenings: [] as Evening[] });
