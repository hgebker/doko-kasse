const NAV_ITEMS = [
  { id: 'overview', label: 'Übersicht' },
  { id: 'evenings', label: 'Spieleinnahmen' },
  { id: 'earnings', label: 'Sonstige Einnahmen' },
  { id: 'expenses', label: 'Ausgaben' },
  { id: 'reports', label: 'Auswertungen' }
];

const NAV_CATEGORIES = [
  {
    id: 'default',
    label: 'Navigation',
    items: NAV_ITEMS
  }
];

export { NAV_ITEMS, NAV_CATEGORIES };
