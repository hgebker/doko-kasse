export const SORT = { ASC: 'asc', DESC: 'desc' } as const;

export type SortDirection = (typeof SORT)[keyof typeof SORT];

export function sortBy<T>(array: T[], field: keyof T, direction: SortDirection = 'asc'): T[] {
  return [...array].sort((a, b) => {
    const av = a[field];
    const bv = b[field];
    if (av === bv) return 0;
    const cmp = av < bv ? -1 : 1;
    return direction === 'asc' ? cmp : -cmp;
  });
}
