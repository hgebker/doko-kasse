<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import ContextPane from '$lib/components/layout/ContextPane.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import SemesterNav from '$lib/components/ui/SemesterNav.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import { PLAYERS } from '$lib/constants/players';
  import type { SemesterReport } from '$lib/types';
  import { capitalize, formatDate, formatNumber } from '$lib/utils/format';
  import { parseEveningDto } from '$lib/utils/parse';
  import { SORT, sortBy } from '$lib/utils/sort';
  import { Accordion } from 'bits-ui';
  import { CaretDownIcon } from 'phosphor-svelte';
  import FunnelSimpleIcon from 'phosphor-svelte/lib/FunnelSimpleIcon';
  import { MediaQuery } from 'svelte/reactivity';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');

  let contextPaneCollapsed = $state(false);
  let contextPaneModalOpen = $state(false);

  const semester = $derived(page.url.searchParams.get('semester') ?? 'gesamt');

  function setSemester(value: string) {
    const next = new URL(page.url);
    if (!value || value === 'gesamt') next.searchParams.delete('semester');
    else next.searchParams.set('semester', value);
    goto(next, { replaceState: true, keepFocus: true, noScroll: true });
  }

  const semesterLabel = $derived((id: string): string => {
    if (id === 'gesamt') return 'Gesamt';
    return data.semesters.find((s) => s.id === id)?.label ?? id;
  });

  const eveningColumns = $derived([
    { key: 'date', label: 'Datum', format: formatDate },
    {
      key: 'semester',
      label: 'Semester',
      format: (v: string) => data.semesters.find((s) => s.id === v)?.label ?? v
    },
    ...PLAYERS.map((p) => ({
      key: p,
      label: capitalize(p),
      format: formatNumber
    }))
  ]);

  const calcColumns = [
    { key: 'type', label: 'Typ' },
    ...PLAYERS.map((p) => ({
      key: p,
      label: capitalize(p),
      format: formatNumber
    }))
  ];

  function resolvedEvenings(report: SemesterReport | null) {
    if (!report?.evenings) return [];
    return sortBy(report.evenings.map(parseEveningDto), 'date', SORT.DESC);
  }

  function resolvedCalcRows(report: SemesterReport | null) {
    if (!report?.semesterResults?.length) return [];
    const byPlayer: Record<string, { sum: number; avg: number; min: number; max: number }> = {};
    for (const r of report.semesterResults) byPlayer[r.player] = r;
    return [
      {
        id: 'sum',
        type: 'Summe',
        ...Object.fromEntries(PLAYERS.map((p) => [p, byPlayer[p]?.sum ?? 0]))
      },
      {
        id: 'avg',
        type: 'Schnitt',
        ...Object.fromEntries(PLAYERS.map((p) => [p, byPlayer[p]?.avg ?? 0]))
      },
      {
        id: 'min',
        type: 'Minimum',
        ...Object.fromEntries(PLAYERS.map((p) => [p, byPlayer[p]?.min ?? 0]))
      },
      {
        id: 'max',
        type: 'Maximum',
        ...Object.fromEntries(PLAYERS.map((p) => [p, byPlayer[p]?.max ?? 0]))
      }
    ];
  }
</script>

<svelte:head>
  <title>Auswertungen - Doko Kasse</title>
</svelte:head>

<ContextPane contextPaneTitle="Semester" bind:contextPaneCollapsed bind:contextPaneModalOpen>
  {#snippet contextPane()}
    <SemesterNav semesters={data.semesters} bind:selected={() => semester, setSemester} />
  {/snippet}

  {#await data.report}
    <Spinner />
  {:then report}
    <SplitPane>
      <PageHeader title="Auswertungen" supportingText={semesterLabel(semester)}>
        {#snippet controls()}
          {#if !isDesktop.current}
            <button
              onclick={() => (contextPaneModalOpen = true)}
              class="flex items-center gap-1 rounded-lg border border-border-strong px-3 py-2 text-text-secondary hover:bg-surface-hover"
              aria-label="Semester filtern"
            >
              <FunnelSimpleIcon size="24" />
              Filter
            </button>
          {/if}
        {/snippet}
      </PageHeader>

      {#if !report || resolvedEvenings(report).length === 0}
        <p class="py-8 text-center text-text-disabled">Keine Daten für diesen Zeitraum</p>
      {:else}
        <div class="mb-4 grid grid-cols-2 gap-3 sm:grid-cols-4">
          <div
            class="rounded-lg border border-border-subtle bg-surface-base p-3 text-center shadow-sm"
          >
            <p class="text-sm text-text-disabled">Einnahmen</p>
            <p class="mt-0.5 font-semibold text-text-primary">
              {formatNumber(report.totalIncome ?? 0)}
            </p>
          </div>

          <div
            class="rounded-lg border border-border-subtle bg-surface-base p-3 text-center shadow-sm"
          >
            <p class="text-sm text-text-disabled">Abende</p>
            <p class="mt-0.5 font-semibold text-text-primary">{report.numberOfEvenings ?? 0}</p>
          </div>

          <div
            class="rounded-lg border border-border-subtle bg-surface-base p-3 text-center shadow-sm"
          >
            <p class="text-sm text-text-disabled">Beste:r (∅)</p>
            <p class="mt-0.5 font-semibold text-accent-best">
              {capitalize(report?.best.player)}
              — {formatNumber(report?.best.avg)}
            </p>
          </div>

          <div
            class="rounded-lg border border-border-subtle bg-surface-base p-3 text-center shadow-sm"
          >
            <p class="text-sm text-text-disabled">Schlechteste:r (∅)</p>
            <p class="mt-0.5 font-semibold text-accent-worst">
              {capitalize(report?.worst.player)}
              — {formatNumber(report?.worst.avg)}
            </p>
          </div>
        </div>

        <Accordion.Root
          type="multiple"
          value={isDesktop.current ? ['calculations', 'evenings'] : ['calculations']}
          class="w-full"
        >
          <Accordion.Item
            value="calculations"
            class="mb-3 overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
          >
            <Accordion.Header>
              <Accordion.Trigger
                class="flex w-full items-center justify-between px-4 py-3 text-left font-semibold text-text-secondary hover:bg-surface-hover data-[state=open]:bg-surface-raised"
              >
                Berechnungen und Auswertungen
                <CaretDownIcon size="20" class="text-text-disabled" />
              </Accordion.Trigger>
            </Accordion.Header>
            <Accordion.Content class="border-t border-border-default p-0">
              <Table columns={calcColumns} rows={resolvedCalcRows(report)} readonly docked />
            </Accordion.Content>
          </Accordion.Item>

          <Accordion.Item
            value="evenings"
            class="overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
          >
            <Accordion.Header>
              <Accordion.Trigger
                class="flex w-full items-center justify-between px-4 py-3 text-left font-semibold text-text-secondary hover:bg-surface-hover data-[state=open]:bg-surface-raised"
              >
                Abende
                <CaretDownIcon size="20" class="text-text-disabled" />
              </Accordion.Trigger>
            </Accordion.Header>
            <Accordion.Content class="border-t border-border-default p-0">
              <Table
                columns={eveningColumns}
                rows={resolvedEvenings(report)}
                readonly
                maxHeight={isDesktop.current ? '40vh' : undefined}
                docked
              />
            </Accordion.Content>
          </Accordion.Item>
        </Accordion.Root>
      {/if}
    </SplitPane>
  {/await}
</ContextPane>
