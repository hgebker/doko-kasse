import type { Player } from '$lib/types';

export const PLAYERS = [
  'tim',
  'jan',
  'ole',
  'hannes',
  'louisa'
] as const satisfies readonly Player[];
