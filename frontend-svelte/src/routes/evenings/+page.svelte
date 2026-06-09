<script lang="ts">
  import { goto, invalidate } from '$app/navigation';
  import { page } from '$app/state';
  import { createEvening, deleteEvening, updateEvening } from '$lib/api/evenings';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import EveningDialog from '$lib/components/dialogs/EveningDialog.svelte';
  import ContextPane from '$lib/components/layout/ContextPane.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import MobileItemList from '$lib/components/ui/MobileItemList.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import SemesterNav from '$lib/components/ui/SemesterNav.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import { PLAYERS } from '$lib/constants/players';
  import type { Evening, EveningInput } from '$lib/types.js';
  import { capitalize, formatDate, formatNumber } from '$lib/utils/format';
  import { SORT, sortBy, type SortDirection } from '$lib/utils/sort';
  import { Separator } from 'bits-ui';
  import { SortAscendingIcon, SortDescendingIcon } from 'phosphor-svelte';
  import ArrowCounterClockwiseIcon from 'phosphor-svelte/lib/ArrowCounterClockwiseIcon';
  import FunnelSimpleIcon from 'phosphor-svelte/lib/FunnelSimpleIcon';
  import { MediaQuery } from 'svelte/reactivity';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');

  let saving = $state(false);
  let toast: ToastContent | null = $state(null);

  let selectedEvening: Evening | null = $state(null);
  let listSortDir: SortDirection = $state(SORT.DESC);

  let dialogOpen = $state(false);
  let editTarget: Evening | null = $state(null);

  let confirmOpen = $state(false);
  let deleteTarget: string | null = $state(null);

  let contextPaneCollapsed = $state(false);
  let contextPaneModalOpen = $state(false);

  const semester = $derived(page.url.searchParams.get('semester') ?? 'gesamt');

  function setSemester(value: string) {
    selectedEvening = null;
    const next = new URL(page.url);
    if (!value || value === 'gesamt') next.searchParams.delete('semester');
    else next.searchParams.set('semester', value);
    goto(next, { replaceState: true, keepFocus: true, noScroll: true });
  }

  const semesterLabel = $derived((id: string): string => {
    if (id === 'gesamt') return 'Gesamt';
    return data.semesters.find((s) => s.id === id)?.label ?? id;
  });

  const tableColumns = $derived([
    { key: 'date', label: 'Datum', format: formatDate },
    { key: 'semester', label: 'Semester', format: (v: string) => semesterLabel(v) },
    ...PLAYERS.map((p) => ({
      key: p,
      label: capitalize(p),
      format: formatNumber
    }))
  ]);

  function handleSelect(row: Evening) {
    selectedEvening = row;
  }

  function reload() {
    invalidate('app:evenings');
  }

  async function handleSave(item: EveningInput) {
    saving = true;
    dialogOpen = false;
    try {
      if (editTarget) {
        await updateEvening(item);
        toast = { message: 'Abend aktualisiert', type: 'success' };
      } else {
        await createEvening(item);
        toast = { message: 'Abend angelegt', type: 'success' };
      }
      await invalidate('app:evenings');
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Speichern', type: 'error' };
    } finally {
      saving = false;
    }
    editTarget = null;
  }

  async function handleDelete(date: string) {
    saving = true;
    try {
      await deleteEvening(date);
      toast = { message: 'Abend gelöscht', type: 'success' };
      await invalidate('app:evenings');
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Löschen', type: 'error' };
    } finally {
      saving = false;
    }
  }

  function confirmDelete() {
    confirmOpen = false;
    if (deleteTarget) handleDelete(deleteTarget);
    deleteTarget = null;
  }

  function openNew() {
    editTarget = null;
    dialogOpen = true;
  }
</script>

<svelte:head>
  <title>Spieleinnahmen - Doko Kasse</title>
</svelte:head>

{#if saving}<Spinner />{/if}
<Toast bind:toast />

<EveningDialog
  semesters={data.semesters}
  bind:open={dialogOpen}
  preset={editTarget}
  onSave={handleSave}
  onClose={() => {
    editTarget = null;
  }}
/>

<ConfirmDialog
  bind:open={confirmOpen}
  title="Abend löschen"
  description="Möchtest du diesen Abend wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden."
  onConfirm={confirmDelete}
  onCancel={() => {
    deleteTarget = null;
  }}
/>

<ContextPane contextPaneTitle="Semester" bind:contextPaneCollapsed bind:contextPaneModalOpen>
  {#snippet contextPane()}
    <SemesterNav semesters={data.semesters} bind:selected={() => semester, setSemester} />
  {/snippet}

  {#await data.evenings}
    <Spinner />
  {:then evenings}
    <SplitPane
      supportingPaneClosable
      supportingPaneTitle={selectedEvening ? formatDate(selectedEvening.date) : ''}
      bind:supportingPaneOpen={() => !!selectedEvening, () => (selectedEvening = null)}
    >
      <PageHeader
        title="Spieleinnahmen"
        count={evenings.length}
        supportingText={semesterLabel(semester)}
      >
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

        {#snippet actions()}
          <button
            onclick={reload}
            class="rounded-lg border border-border-strong p-2 text-text-secondary hover:bg-surface-hover"
            aria-label="Aktualisieren"
            title="Aktualisieren"
          >
            <ArrowCounterClockwiseIcon size="24" />
          </button>

          <button
            onclick={openNew}
            class="rounded-lg bg-action-primary px-3 py-2 font-medium text-action-primary-fg hover:bg-action-primary-hover"
          >
            Neu
          </button>
        {/snippet}
      </PageHeader>

      {#if isDesktop.current}
        <Table
          columns={tableColumns}
          rows={sortBy(evenings, 'date', listSortDir)}
          selectable
          onselect={handleSelect}
        />
      {:else}
        <MobileItemList
          label="Abende"
          items={sortBy(evenings, 'date', listSortDir)}
          getTitle={(e) => formatDate(e.date)}
          getSubtitle={(e) => `${formatNumber(e.sum ?? 0)} · ${semesterLabel(e.semester)}`}
          onselect={(e) => (selectedEvening = e)}
          emptyText="Keine Abende"
        >
          {#snippet headerAction()}
            <button
              onclick={() => (listSortDir = listSortDir === SORT.DESC ? SORT.ASC : SORT.DESC)}
              class="rounded p-1 text-text-disabled hover:bg-surface-hover hover:text-text-secondary"
              title={listSortDir === SORT.DESC ? 'Älteste zuerst' : 'Neueste zuerst'}
            >
              {#if listSortDir === SORT.DESC}
                <SortDescendingIcon size="20" />
              {:else}
                <SortAscendingIcon size="20" />
              {/if}
            </button>
          {/snippet}
        </MobileItemList>
      {/if}

      {#snippet supportingPane()}
        {#if selectedEvening}
          <div class="flex items-center justify-between mb-4">
            <p class="text-text-muted">
              {semesterLabel(selectedEvening.semester)}
            </p>
            <div class="flex gap-2">
              <button
                onclick={() => {
                  editTarget = selectedEvening;
                  dialogOpen = true;
                }}
                class="rounded-lg border border-border-strong px-3 py-2 font-medium text-text-secondary hover:bg-surface-hover"
              >
                Bearbeiten
              </button>
              <button
                onclick={() => {
                  if (selectedEvening) {
                    deleteTarget = selectedEvening.date;
                    confirmOpen = true;
                  }
                }}
                class="rounded-lg border border-destruct-border px-3 py-2 font-medium text-destruct-text hover:bg-surface-hover"
              >
                Löschen
              </button>
            </div>
          </div>

          <div class="mb-4 grid grid-cols-2 gap-3">
            {#each PLAYERS as player}
              <div class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center">
                <p class="text-sm font-medium uppercase tracking-wide text-text-muted">
                  {capitalize(player)}
                </p>
                <p
                  class="mt-1 text-base font-semibold"
                  class:text-accent-best={selectedEvening[player] === selectedEvening.min?.value}
                  class:text-accent-worst={selectedEvening[player] === selectedEvening.max?.value}
                >
                  {formatNumber(selectedEvening[player] ?? 0)}
                </p>
              </div>
            {/each}

            <Separator.Root class="col-span-2 border border-border-subtle" />

            <div
              class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center col-span-2"
            >
              <p class="text-sm font-medium uppercase tracking-wide text-text-muted">Gesamt (Σ)</p>
              <p class="mt-1 text-base font-semibold text-text-primary">
                {formatNumber(selectedEvening.sum ?? 0)}
              </p>
            </div>

            <div
              class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center col-span-2"
            >
              <p class="text-sm font-medium uppercase tracking-wide text-text-muted">
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
  {/await}
</ContextPane>
