<script lang="ts">
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import { capitalize, formatDate, formatNumber } from '$lib/utils/format';
  import { SORT, sortBy } from '$lib/utils/sort';
  import CalendarBlankIcon from 'phosphor-svelte/lib/CalendarBlankIcon';
  import MoneyIcon from 'phosphor-svelte/lib/MoneyIcon';
  import PiggyBankIcon from 'phosphor-svelte/lib/PiggyBankIcon';
  import ReceiptIcon from 'phosphor-svelte/lib/ReceiptIcon';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const lastEvening = $derived(
    data.semesterReport.evenings.length > 0
      ? sortBy(data.semesterReport.evenings, 'date', SORT.DESC)[0]
      : null
  );

  const cards = $derived([
    {
      title: 'Einnahmen',
      value: data.report.totalIncome ?? 0,
      color: 'bg-accent-income',
      icon: MoneyIcon
    },
    {
      title: 'Ausgaben',
      value: data.report.totalExpenses ?? 0,
      color: 'bg-accent-expense',
      icon: ReceiptIcon
    },
    {
      title: 'Kassenstand',
      value: data.report.currentCash ?? 0,
      color: 'bg-accent-neutral',
      icon: PiggyBankIcon
    }
  ]);
</script>

<svelte:head>
  <title>Übersicht - Doko Kasse</title>
</svelte:head>

<SplitPane>
  <h1 class="mb-6 text-2xl font-semibold text-text-primary">Übersicht</h1>
  <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
    {#each cards as card}
      <div class="rounded-xl border border-border-subtle bg-surface-base p-6 shadow-sm">
        <div class="flex items-center gap-4">
          <div class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl {card.color}">
            <card.icon size="24" class="text-white" />
          </div>
          <div>
            <p class="text-sm text-text-muted">{card.title}</p>
            <p class="text-xl font-bold text-text-primary">{formatNumber(card.value)}</p>
          </div>
        </div>
      </div>
    {/each}

    <div class="rounded-xl border border-border-subtle bg-surface-base p-6 shadow-sm sm:col-span-3">
      <div class="mb-6 flex items-center gap-4">
        <div
          class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-accent-evening"
        >
          <CalendarBlankIcon size="24" class="text-white" />
        </div>
        <div>
          <p class="text-sm text-text-muted">Letzter Abend</p>
          <p class="text-xl font-bold text-text-primary">
            {lastEvening ? formatDate(lastEvening.date) : 'Noch keine Abende gespielt'}
          </p>
        </div>
      </div>

      {#if lastEvening}
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <p class="text-sm text-text-muted">Schlechteste:r (∅)</p>
            <p class="text-lg font-semibold text-accent-worst">
              {capitalize(lastEvening.max.player)} — {formatNumber(lastEvening.max.value)}
            </p>
          </div>

          <div>
            <p class="text-sm text-text-muted">Beste:r (∅)</p>
            <p class="text-lg font-semibold text-accent-best">
              {capitalize(lastEvening.min.player)} — {formatNumber(lastEvening.min.value)}
            </p>
          </div>
        </div>
      {/if}
    </div>
  </div>
</SplitPane>
