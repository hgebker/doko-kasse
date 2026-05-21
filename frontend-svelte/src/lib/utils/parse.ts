import type { EveningDto, Evening, Player } from '$lib/types';

export function parseEveningDto(dto: EveningDto): Evening {
  const players: Partial<Record<Player, number>> = {};
  for (const r of dto.results ?? []) {
    players[r.player] = r.value;
  }
  return {
    id: dto.date,
    date: dto.date,
    semester: dto.semester,
    ...players,
    sum: dto.sum,
    avg: dto.avg,
    min: dto.min,
    max: dto.max
  };
}
