<script lang="ts">
  import { createEvening, deleteEvening, listEvenings, updateEvening } from '$lib/api/evenings';
  import EveningDialog from '$lib/components/dialogs/EveningDialog.svelte';
  import ContextPane from '$lib/components/layout/ContextPane.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import SemesterNav from '$lib/components/ui/SemesterNav.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import { PLAYERS } from '$lib/constants/players';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
  import type { Evening, EveningInput } from '$lib/types.js';
  import { capitalize, formatDate, formatNumber } from '$lib/utils/format';
  import { parseEveningDto } from '$lib/utils/parse';
  import { SORT, sortBy, type SortDirection } from '$lib/utils/sort';
  import { Separator } from 'bits-ui';
  import { SortAscendingIcon, SortDescendingIcon } from 'phosphor-svelte';
  import ArrowCounterClockwiseIcon from 'phosphor-svelte/lib/ArrowCounterClockwiseIcon';
  import FunnelSimpleIcon from 'phosphor-svelte/lib/FunnelSimpleIcon';
  import { MediaQuery } from 'svelte/reactivity';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');

  let evenings = $derived(data.evenings);
  let loading = $state(false);
  let toast: ToastContent | null = $state(null);

  let selectedSemester = $state('gesamt');
  let selectedEvening: Evening | null = $state(null);
  let listSortDir: SortDirection = $state(SORT.DESC);

  let dialogOpen = $state(false);
  let editTarget: Evening | null = $state(null);

  let contextPaneCollapsed = $state(false);
  let contextPaneModalOpen = $state(false);

  // Re-fetch when semester changes
  $effect(() => {
    reload(selectedSemester);
  });

  async function reload(semester: string) {
    loading = true;
    try {
      const raw = await listEvenings(semester);
      evenings = raw.map(parseEveningDto);
      selectedEvening = null;
    } catch {
      toast = { message: 'Fehler beim Laden', type: 'error' };
    } finally {
      loading = false;
    }
  }

  const sortedEvenings = $derived(sortBy(evenings, 'date', listSortDir));

  const tableColumns = [
    { key: 'date', label: 'Datum', format: formatDate },
    { key: 'semester', label: 'Semester', format: (v: string) => SEMESTER_LABEL_MAPPING[v] ?? v },
    ...PLAYERS.map((p) => ({
      key: p,
      label: capitalize(p),
      format: formatNumber
    }))
  ];

  const tableActions = [
    {
      label: 'Bearbeiten',
      onclick: (row: Evening) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    { label: 'Löschen', onclick: (row: Evening) => handleDelete(row.date) }
  ];

  function handleSelect(row: Evening) {
    selectedEvening = row;
  }

  async function handleSave(item: EveningInput) {
    loading = true;
    dialogOpen = false;
    try {
      if (editTarget) {
        await updateEvening(item);
        toast = { message: 'Abend aktualisiert', type: 'success' };
      } else {
        await createEvening(item);
        toast = { message: 'Abend angelegt', type: 'success' };
      }
      await reload(selectedSemester);
    } catch {
      toast = { message: 'Fehler beim Speichern', type: 'error' };
      loading = false;
    }
    editTarget = null;
  }

  async function handleDelete(date: string) {
    loading = true;
    try {
      await deleteEvening(date);
      toast = { message: 'Abend gelöscht', type: 'success' };
      await reload(selectedSemester);
    } catch {
      toast = { message: 'Fehler beim Löschen', type: 'error' };
      loading = false;
    }
  }

  function openNew() {
    editTarget = null;
    dialogOpen = true;
  }
</script>

<svelte:head>
  <title>Spieleinnahmen - Doko Kasse</title>
</svelte:head>

{#if loading}<Spinner />{/if}
<Toast bind:toast />

<EveningDialog
  bind:open={dialogOpen}
  preset={editTarget}
  onSave={handleSave}
  onClose={() => {
    editTarget = null;
  }}
/>

<ContextPane contextPaneTitle="Semester" bind:contextPaneCollapsed bind:contextPaneModalOpen>
  {#snippet contextPane()}
    <SemesterNav bind:selected={selectedSemester} />
  {/snippet}

  <SplitPane
    supportingPaneClosable
    supportingPaneTitle={selectedEvening ? formatDate(selectedEvening.date) : ''}
    bind:supportingPaneOpen={() => !!selectedEvening, () => (selectedEvening = null)}
  >
    <!-- Detail pane: header + list/table -->
    <PageHeader title="Spieleinnahmen" count={evenings.length}>
      {#snippet controls()}
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
      {/snippet}

      {#snippet actions()}
        <button
          onclick={() => reload(selectedSemester)}
          class="rounded-lg border border-border-strong p-2 text-text-secondary hover:bg-surface-hover"
          aria-label="Aktualisieren"
          title="Aktualisieren"
        >
          <ArrowCounterClockwiseIcon size="16" />
        </button>

        <button
          onclick={openNew}
          class="rounded-lg bg-action-primary px-3 py-2 text-sm font-medium text-action-primary-fg hover:bg-action-primary-hover"
        >
          Neu
        </button>
      {/snippet}
    </PageHeader>

    {#if isDesktop.current}
      <Table
        columns={tableColumns}
        rows={sortedEvenings}
        actions={tableActions}
        selectable
        onselect={handleSelect}
      />
    {:else}
      <!-- Mobile evening list -->
      <div
        class="overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
      >
        <div class="flex items-center justify-between border-b border-border-subtle p-3">
          <span class="text-xs font-semibold uppercase tracking-wide text-text-disabled"
            >Abende</span
          >
          <button
            onclick={() => (listSortDir = listSortDir === SORT.DESC ? SORT.ASC : SORT.DESC)}
            class="rounded p-1 text-text-disabled hover:bg-surface-hover hover:text-text-secondary"
            title={listSortDir === SORT.DESC ? 'Älteste zuerst' : 'Neueste zuerst'}
          >
            {#if listSortDir === SORT.DESC}
              <SortDescendingIcon size="16" />
            {:else}
              <SortAscendingIcon size="16" />
            {/if}
          </button>
        </div>
        <ul>
          {#each sortedEvenings as evening}
            <li>
              <button
                onclick={() => (selectedEvening = evening)}
                class="flex w-full flex-col px-4 py-3 text-left transition-colors hover:bg-surface-hover"
              >
                <span class="font-medium text-text-primary">{formatDate(evening.date)}</span>
                <span class="text-xs text-text-muted">
                  {SEMESTER_LABEL_MAPPING[evening.semester] ?? evening.semester} — {formatNumber(
                    evening.sum ?? 0
                  )}
                </span>
              </button>
            </li>
          {/each}
          {#if sortedEvenings.length === 0}
            <li class="p-4 text-center text-sm text-text-disabled">Keine Abende</li>
          {/if}
        </ul>
      </div>
    {/if}

    {#snippet supportingPane()}
      {#if selectedEvening}
        <div class="flex items-center justify-between mb-4">
          <p class="text-sm text-text-muted">
            {SEMESTER_LABEL_MAPPING[selectedEvening.semester] ?? selectedEvening.semester}
          </p>
          <div class="flex gap-2">
            <button
              onclick={() => {
                editTarget = selectedEvening;
                dialogOpen = true;
              }}
              class="rounded-lg border border-border-strong px-3 py-1.5 text-sm font-medium text-text-secondary hover:bg-surface-hover"
            >
              Bearbeiten
            </button>
            <button
              onclick={() => selectedEvening && handleDelete(selectedEvening.date)}
              class="rounded-lg border border-destruct-border px-3 py-1.5 text-sm font-medium text-destruct-text hover:bg-surface-hover"
            >
              Löschen
            </button>
          </div>
        </div>

        <div class="mb-4 grid grid-cols-2 gap-3">
          <!-- Player tiles -->
          {#each PLAYERS as player}
            <div class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center">
              <p class="text-xs font-medium uppercase tracking-wide text-text-muted">
                {capitalize(player)}
              </p>
              <p
                class="mt-1 text-base font-semibold"
                class:text-accent-best={selectedEvening[player] === selectedEvening.max?.value}
                class:text-accent-worst={selectedEvening[player] === selectedEvening.min?.value}
              >
                {formatNumber(selectedEvening[player] ?? 0)}
              </p>
            </div>
          {/each}

          <Separator.Root class="col-span-2 border border-border-subtle" />

          <!-- Summary -->
          <div
            class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center col-span-2"
          >
            <p class="text-xs font-medium uppercase tracking-wide text-text-muted">Gesamt (Σ)</p>
            <p class="mt-1 text-base font-semibold text-text-primary">
              {formatNumber(selectedEvening.sum ?? 0)}
            </p>
          </div>

          <div
            class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center col-span-2"
          >
            <p class="text-xs font-medium uppercase tracking-wide text-text-muted">
              Durchschnitt (∅)
            </p>
            <p class="mt-1 text-base font-semibold text-text-primary">
              {formatNumber(selectedEvening.avg ?? 0)}
            </p>
          </div>
        </div>
      {/if}
    {/snippet}
  </SplitPane>
</ContextPane>
