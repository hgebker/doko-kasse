<script lang="ts">
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import { formatNumber } from '$lib/utils/format';
  import MoneyIcon from 'phosphor-svelte/lib/MoneyIcon';
  import PiggyBankIcon from 'phosphor-svelte/lib/PiggyBankIcon';
  import ReceiptIcon from 'phosphor-svelte/lib/ReceiptIcon';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

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
  <div class="grid grid-cols-1 gap-8 sm:grid-cols-3">
    {#each cards as card}
      <div class="rounded-xl border border-border-subtle bg-surface-base p-6 shadow-sm">
        <div class="flex items-center gap-4">
          <div class="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl {card.color}">
            <card.icon size="20" class="text-white" />
          </div>
          <div>
            <p class="text-sm text-text-muted">{card.title}</p>
            <p class="text-xl font-bold text-text-primary">{formatNumber(card.value)}</p>
          </div>
        </div>
      </div>
    {/each}
  </div>
</SplitPane>
