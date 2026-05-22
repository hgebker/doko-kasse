export type Player = 'tim' | 'jan' | 'ole' | 'hannes' | 'louisa';

export interface Earning {
  id?: string;
  description: string;
  value: number;
  semester: string;
}

export interface Expense {
  id?: string;
  description: string;
  value: number;
  semester: string;
}

export interface PlayerResult {
  player: Player;
  value: number;
}

export interface StatResult {
  player: Player;
  value: number;
}

export interface EveningDto {
  date: string;
  semester: string;
  results: PlayerResult[];
  sum: number;
  avg: number;
  min: StatResult;
  max: StatResult;
}

export type Evening = {
  id: string;
  date: string;
  semester: string;
  sum: number;
  avg: number;
  min: StatResult;
  max: StatResult;
} & Partial<Record<Player, number>>;

export type EveningInput = { date: string; semester: string } & Record<Player, number>;

export interface PlayerSemesterResult {
  player: Player;
  sum: number;
  avg: number;
  min: number;
  max: number;
}

export interface CashReport {
  totalExpenses: number;
  incomeFromEarnings: number;
  incomeFromEvenings: number;
  totalIncome: number;
  currentCash: number;
}

export interface SemesterReport {
  totalIncome: number;
  numberOfEvenings: number;
  evenings: EveningDto[];
  semesterResults: PlayerSemesterResult[];
  best: PlayerSemesterResult;
  worst: PlayerSemesterResult;
}

export interface SemesterEntry {
  id: string;
  label: string;
}
