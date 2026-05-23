<script lang="ts">
  import { getSemesterReport } from '$lib/api/reports';
  import ContextPane from '$lib/components/layout/ContextPane.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import SemesterNav from '$lib/components/ui/SemesterNav.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import { PLAYERS } from '$lib/constants/players';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
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

  let report = $derived(data.report);
  let loading = $state(false);
  let selectedSemester = $state('gesamt');

  let contextPaneCollapsed = $state(false);
  let contextPaneModalOpen = $state(false);

  $effect(() => {
    loadReport(selectedSemester);
  });

  async function loadReport(semester: string) {
    loading = true;
    try {
      report = await getSemesterReport(semester);
    } catch {
      report = null;
    } finally {
      loading = false;
    }
  }

  const evenings = $derived(
    report?.evenings ? sortBy(report.evenings.map(parseEveningDto), 'date', SORT.DESC) : []
  );

  const eveningColumns = [
    { key: 'date', label: 'Datum', format: formatDate },
    { key: 'semester', label: 'Semester', format: (v: string) => SEMESTER_LABEL_MAPPING[v] ?? v },
    ...PLAYERS.map((p) => ({
      key: p,
      label: capitalize(p),
      format: formatNumber
    }))
  ];

  const calcRows = $derived.by(() => {
    if (!report?.semesterResults?.length) return [];
    const byPlayer: Record<string, any> = {};
    for (const r of report.semesterResults) {
      byPlayer[r.player] = r;
    }
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
  });

  const calcColumns = [
    { key: 'type', label: 'Typ' },
    ...PLAYERS.map((p) => ({
      key: p,
      label: capitalize(p),
      format: formatNumber
    }))
  ];
</script>

<svelte:head>
  <title>Auswertungen - Doko Kasse</title>
</svelte:head>

{#if loading}<Spinner />{/if}

<ContextPane contextPaneTitle="Semester" bind:contextPaneCollapsed bind:contextPaneModalOpen>
  {#snippet contextPane()}
    <SemesterNav bind:selected={selectedSemester} />
  {/snippet}

  <SplitPane>
    <div class="mb-4 flex items-center justify-between">
      <h1 class="text-2xl font-semibold text-text-primary">Auswertungen</h1>
      {#if !isDesktop.current}
        <button
          onclick={() => (contextPaneModalOpen = true)}
          class="flex items-center gap-1 rounded-lg border border-border-strong px-2 py-1.5 text-sm text-text-secondary hover:bg-surface-hover"
          aria-label="Semester filtern"
        >
          <FunnelSimpleIcon size="16" />
          Filter
        </button>
      {/if}
    </div>

    {#if !report || evenings.length === 0}
      <p class="py-8 text-center text-text-disabled">Keine Daten für diesen Zeitraum</p>
    {:else}
      <div class="mb-4 grid grid-cols-2 gap-3 sm:grid-cols-4">
        <div
          class="rounded-lg border border-border-default bg-surface-base p-3 text-center shadow-sm"
        >
          <p class="text-xs text-text-disabled">Einnahmen</p>
          <p class="mt-0.5 font-semibold text-text-primary">
            {formatNumber(report.totalIncome ?? 0)}
          </p>
        </div>

        <div
          class="rounded-lg border border-border-default bg-surface-base p-3 text-center shadow-sm"
        >
          <p class="text-xs text-text-disabled">Abende</p>
          <p class="mt-0.5 font-semibold text-text-primary">{report.numberOfEvenings ?? 0}</p>
        </div>

        <div
          class="rounded-lg border border-border-default bg-surface-base p-3 text-center shadow-sm"
        >
          <p class="text-xs text-text-disabled">Saisonbeste:r (∅)</p>
          <p class="mt-0.5 font-semibold text-accent-best">
            {capitalize(report?.best.player)}
            — {formatNumber(report?.best.avg)}
          </p>
        </div>

        <div
          class="rounded-lg border border-border-default bg-surface-base p-3 text-center shadow-sm"
        >
          <p class="text-xs text-text-disabled">Saisonschlechteste:r (∅)</p>
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
              class="flex w-full items-center justify-between px-4 py-3 text-left text-sm font-semibold text-text-secondary hover:bg-surface-hover data-[state=open]:bg-surface-raised"
            >
              Berechnungen und Auswertungen
              <CaretDownIcon size="16" class="text-text-disabled" />
            </Accordion.Trigger>
          </Accordion.Header>
          <Accordion.Content class="border-t border-border-default p-0">
            <Table columns={calcColumns} rows={calcRows} readonly docked />
          </Accordion.Content>
        </Accordion.Item>

        <Accordion.Item
          value="evenings"
          class="overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
        >
          <Accordion.Header>
            <Accordion.Trigger
              class="flex w-full items-center justify-between px-4 py-3 text-left text-sm font-semibold text-text-secondary hover:bg-surface-hover data-[state=open]:bg-surface-raised"
            >
              Abende
              <CaretDownIcon size="16" class="text-text-disabled" />
            </Accordion.Trigger>
          </Accordion.Header>
          <Accordion.Content class="border-t border-border-default p-0">
            <Table
              columns={eveningColumns}
              rows={evenings}
              readonly
              maxHeight={isDesktop.current ? '40vh' : undefined}
              docked
            />
          </Accordion.Content>
        </Accordion.Item>
      </Accordion.Root>
    {/if}
  </SplitPane>
</ContextPane>
