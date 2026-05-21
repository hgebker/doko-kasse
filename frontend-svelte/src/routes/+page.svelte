<script>
  import { formatNumber } from '$lib/utils/format';

  let { data } = $props();

  const totalIncome = $derived(
    (data.report.incomeFromEarnings ?? 0) + (data.report.incomeFromEvenings ?? 0)
  );
  const currentCash = $derived(totalIncome - (data.report.totalExpenses ?? 0));

  const cards = $derived([
    {
      title: 'Einnahmen',
      value: totalIncome,
      color: 'bg-accent-income',
      icon: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />`
    },
    {
      title: 'Ausgaben',
      value: data.report.totalExpenses ?? 0,
      color: 'bg-accent-expense',
      icon: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />`
    },
    {
      title: 'Kassenstand',
      value: currentCash,
      color: 'bg-accent-neutral',
      icon: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />`
    }
  ]);
</script>

<div>
  <h1 class="mb-6 text-2xl font-semibold text-text-primary">Übersicht</h1>
  <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
    {#each cards as card}
      <div class="rounded-xl border border-border-subtle bg-surface-base p-6 shadow-sm">
        <div class="flex items-center gap-4">
          <div class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl {card.color}">
            <svg class="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {@html card.icon}
            </svg>
          </div>
          <div>
            <p class="text-sm text-text-muted">{card.title}</p>
            <p class="text-xl font-bold text-text-primary">{formatNumber(card.value)}</p>
          </div>
        </div>
      </div>
    {/each}
  </div>
</div>
