import type { SemesterEntry } from '$lib/types';

export const SEMESTER_LIST: SemesterEntry[] = [
  { id: 'ws1819', label: 'Wintersemester 18/19' },
  { id: 'ss19', label: 'Sommersemester 19' },
  { id: 'ws1920', label: 'Wintersemester 19/20' },
  { id: 'ss20', label: 'Sommersemester 20' },
  { id: 'ws2021', label: 'Wintersemester 20/21' },
  { id: 'ss21', label: 'Sommersemester 21' },
  { id: 'ws2122', label: 'Wintersemester 21/22' },
  { id: 'ss22', label: 'Sommersemester 22' },
  { id: 'ws2223', label: 'Wintersemester 22/23' },
  { id: 'ss23', label: 'Sommersemester 23' },
  { id: 'season2324', label: 'Saison 23/24' },
  { id: 'season2425', label: 'Saison 24/25' },
  { id: 'season2526', label: 'Saison 25/26' }
];

export const SEMESTER_LABEL_MAPPING: Record<string, string> = Object.fromEntries(
  SEMESTER_LIST.map((s) => [s.id, s.label])
);

export const LAST_SEMESTER: SemesterEntry = SEMESTER_LIST[SEMESTER_LIST.length - 1];
