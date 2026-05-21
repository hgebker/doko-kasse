<script>
  import { getSemesterReport } from '$lib/api/reports';
  import { PLAYERS } from '$lib/constants/players';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
  import { formatDate, formatNumber } from '$lib/utils/format';
  import { parseEveningDto } from '$lib/utils/parse';
  import { SORT, sortBy } from '$lib/utils/sort';
  import { Accordion } from 'bits-ui';
  import { MediaQuery } from 'svelte/reactivity';

  import SemesterNav from '$lib/components/ui/SemesterNav.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';

  let { data } = $props();

  const isMobile = new MediaQuery('(max-width: 640px)');

  let report = $state(data.report);
  let loading = $state(false);
  let selectedSemester = $state('gesamt');

  $effect(() => {
    loadReport(selectedSemester);
  });

  async function loadReport(semester) {
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
    { key: 'semester', label: 'Semester', format: (v) => SEMESTER_LABEL_MAPPING[v] ?? v },
    ...PLAYERS.map((p) => ({
      key: p,
      label: p.charAt(0).toUpperCase() + p.slice(1),
      format: formatNumber
    }))
  ];

  // Transform semesterResults into calculation rows: Summe, Schnitt, Minimum, Maximum
  const calcRows = $derived.by(() => {
    if (!report?.semesterResults?.length) return [];
    const byPlayer = {};
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
      label: p.charAt(0).toUpperCase() + p.slice(1),
      format: formatNumber
    }))
  ];

  // Best = lowest avg, Worst = highest avg
  const bestPlayer = $derived.by(() => {
    if (!report?.semesterResults?.length) return null;
    return report.semesterResults.reduce((best, r) => (!best || r.avg < best.avg ? r : best), null);
  });

  const worstPlayer = $derived.by(() => {
    if (!report?.semesterResults?.length) return null;
    return report.semesterResults.reduce(
      (worst, r) => (!worst || r.avg > worst.avg ? r : worst),
      null
    );
  });
</script>

{#if loading}<Spinner />{/if}

<div>
  <div class="mb-4 flex items-center justify-between">
    <h1 class="text-2xl font-semibold text-text-primary">Auswertungen</h1>
    {#if isMobile.current}
      <select
        bind:value={selectedSemester}
        class="rounded-lg border border-border-strong bg-surface-raised px-2 py-1.5 text-sm text-text-primary"
      >
        <option value="gesamt">Gesamt</option>
        {#each Object.entries(SEMESTER_LABEL_MAPPING) as [id, label]}
          <option value={id}>{label}</option>
        {/each}
      </select>
    {/if}
  </div>

  <div class="flex gap-4">
    {#if !isMobile.current}
      <SemesterNav bind:selected={selectedSemester} />
    {/if}

    <div class="min-w-0 flex-1">
      {#if !report || evenings.length === 0}
        <p class="py-8 text-center text-text-disabled">Keine Daten für diesen Zeitraum</p>
      {:else}
        <Accordion.Root type="multiple" value={['evenings', 'calculations']}>
          <!-- Abende panel -->
          <Accordion.Item
            value="evenings"
            class="mb-3 overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
          >
            <Accordion.Header>
              <Accordion.Trigger
                class="flex w-full items-center justify-between px-4 py-3 text-left text-sm font-semibold text-text-secondary hover:bg-surface-hover data-[state=open]:bg-surface-raised"
              >
                Abende
                <svg
                  class="h-4 w-4 text-text-disabled transition-transform data-[state=open]:rotate-180"
                  data-state-ref
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M19 9l-7 7-7-7"
                  />
                </svg>
              </Accordion.Trigger>
            </Accordion.Header>
            <Accordion.Content class="border-t border-border-subtle p-0">
              <Table columns={eveningColumns} rows={evenings} readonly={true} maxHeight="40vh" />
            </Accordion.Content>
          </Accordion.Item>

          <!-- Berechnungen panel -->
          <Accordion.Item
            value="calculations"
            class="mb-3 overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
          >
            <Accordion.Header>
              <Accordion.Trigger
                class="flex w-full items-center justify-between px-4 py-3 text-left text-sm font-semibold text-text-secondary hover:bg-surface-hover data-[state=open]:bg-surface-raised"
              >
                Berechnungen und Auswertungen
                <svg
                  class="h-4 w-4 text-text-disabled transition-transform data-[state=open]:rotate-180"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M19 9l-7 7-7-7"
                  />
                </svg>
              </Accordion.Trigger>
            </Accordion.Header>
            <Accordion.Content class="border-t border-border-subtle p-0">
              <Table columns={calcColumns} rows={calcRows} readonly={true} maxHeight="40vh" />
            </Accordion.Content>
          </Accordion.Item>
        </Accordion.Root>

        <!-- Footer summary -->
        <div class="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-4">
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
          {#if bestPlayer}
            <div
              class="rounded-lg border border-border-default bg-surface-base p-3 text-center shadow-sm"
            >
              <p class="text-xs text-text-disabled">Tagesbeste/r (∅)</p>
              <p class="mt-0.5 font-semibold text-accent-income">
                {bestPlayer.player.charAt(0).toUpperCase() + bestPlayer.player.slice(1)}
                — {formatNumber(bestPlayer.avg)}
              </p>
            </div>
          {/if}
          {#if worstPlayer}
            <div
              class="rounded-lg border border-border-default bg-surface-base p-3 text-center shadow-sm"
            >
              <p class="text-xs text-text-disabled">Tagesschlechteste/r (∅)</p>
              <p class="mt-0.5 font-semibold text-accent-expense">
                {worstPlayer.player.charAt(0).toUpperCase() + worstPlayer.player.slice(1)}
                — {formatNumber(worstPlayer.avg)}
              </p>
            </div>
          {/if}
        </div>
      {/if}
    </div>
  </div>
</div>
