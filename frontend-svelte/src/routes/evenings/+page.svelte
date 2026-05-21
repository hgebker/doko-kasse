<script lang="ts">
  import { createEvening, deleteEvening, listEvenings, updateEvening } from '$lib/api/evenings';
  import { PLAYERS } from '$lib/constants/players';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
  import { formatDate, formatNumber } from '$lib/utils/format';
  import { parseEveningDto } from '$lib/utils/parse';
  import { SORT, sortBy } from '$lib/utils/sort';
  import { MediaQuery } from 'svelte/reactivity';

  import EveningDialog from '$lib/components/dialogs/EveningDialog.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import SemesterNav from '$lib/components/ui/SemesterNav.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast from '$lib/components/ui/Toast.svelte';

  let { data } = $props();

  const isMobile = new MediaQuery('(max-width: 640px)');

  let evenings = $derived(data.evenings);
  let loading = $state(false);
  let toast = $state(null);

  let selectedSemester = $state('gesamt');
  let selectedEvening = $state(null);
  let listSortDir = $state(SORT.DESC);

  let dialogOpen = $state(false);
  let editTarget = $state(null);

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
    { key: 'semester', label: 'Semester', format: (v) => SEMESTER_LABEL_MAPPING[v] ?? v },
    ...PLAYERS.map((p) => ({
      key: p,
      label: p.charAt(0).toUpperCase() + p.slice(1),
      format: formatNumber
    }))
  ];

  const tableActions = [
    {
      label: 'Bearbeiten',
      onclick: (row) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    { label: 'Löschen', onclick: (row) => handleDelete(row.date) }
  ];

  async function handleSave(item) {
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

  async function handleDelete(date) {
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

<div>
  <PageHeader title="Spieleinnahmen" count={evenings.length}>
    {#snippet controls()}
      <!-- Semester dropdown for mobile (sidebar hidden on mobile) -->
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
    {/snippet}
    {#snippet actions()}
      <button
        onclick={() => reload(selectedSemester)}
        class="rounded-lg border border-border-strong p-2 text-text-secondary hover:bg-surface-hover"
        aria-label="Aktualisieren"
        title="Aktualisieren"
      >
        <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
          />
        </svg>
      </button>
      <button
        onclick={openNew}
        class="rounded-lg bg-action-primary px-3 py-2 text-sm font-medium text-action-primary-fg hover:bg-action-primary-hover"
      >
        Neu
      </button>
    {/snippet}
  </PageHeader>

  {#if !isMobile.current}
    <!-- DESKTOP: sidebar + table -->
    <div class="flex gap-4">
      <SemesterNav bind:selected={selectedSemester} />
      <div class="min-w-0 flex-1">
        <Table columns={tableColumns} rows={sortedEvenings} actions={tableActions} />
      </div>
    </div>
  {:else}
    <!-- MOBILE: split view (list panel + detail card) -->
    <div
      class="flex min-w-0 flex-1 gap-0 overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm"
    >
      <!-- Left: list panel -->
      <div
        class="flex flex-col border-r border-border-default"
        class:w-full={!selectedEvening}
        class:hidden={!!selectedEvening}
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
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {#if listSortDir === SORT.DESC}
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 4h13M3 8h9m-9 4h6m4 0l4-4m0 0l4 4m-4-4v12"
                />
              {:else}
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 4h13M3 8h9m-9 4h9m5-4v12m0 0l-4-4m4 4l4-4"
                />
              {/if}
            </svg>
          </button>
        </div>
        <ul class="overflow-y-auto" style="max-height: 70vh">
          {#each sortedEvenings as evening}
            <li>
              <button
                onclick={() => {
                  selectedEvening = evening;
                }}
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

      <!-- Right: detail panel -->
      <div class="flex-1 overflow-y-auto p-4" class:hidden={!selectedEvening}>
        {#if selectedEvening}
          <button
            onclick={() => (selectedEvening = null)}
            class="mb-3 flex items-center gap-1 text-sm text-text-muted hover:text-text-primary"
          >
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M15 19l-7-7 7-7"
              />
            </svg>
            Zurück
          </button>

          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-text-primary">{formatDate(selectedEvening.date)}</h2>
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
                onclick={() => handleDelete(selectedEvening.date)}
                class="rounded-lg border border-destruct-border px-3 py-1.5 text-sm font-medium text-destruct-text hover:bg-surface-hover"
              >
                Löschen
              </button>
            </div>
          </div>

          <p class="mb-4 text-sm text-text-muted">
            {SEMESTER_LABEL_MAPPING[selectedEvening.semester] ?? selectedEvening.semester}
          </p>

          <!-- Player tiles -->
          <div class="mb-4 grid grid-cols-2 gap-3">
            {#each PLAYERS as player}
              <div class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center">
                <p class="text-xs font-medium uppercase tracking-wide text-text-muted">
                  {player.charAt(0).toUpperCase() + player.slice(1)}
                </p>
                <p class="mt-1 text-base font-semibold text-text-primary">
                  {formatNumber(selectedEvening[player] ?? 0)}
                </p>
              </div>
            {/each}
          </div>

          <!-- Summary -->
          <div class="rounded-lg border border-border-default bg-surface-base p-4">
            <h3 class="mb-3 text-sm font-semibold text-text-secondary">Zusammenfassung</h3>
            <div class="grid grid-cols-2 gap-2 text-sm">
              <div>
                <span class="text-text-muted">Tagesbeste/r:</span>
                <span class="ml-1 font-medium">
                  {selectedEvening.min?.player
                    ? selectedEvening.min.player.charAt(0).toUpperCase() +
                      selectedEvening.min.player.slice(1) +
                      ' — ' +
                      formatNumber(selectedEvening.min.value ?? 0)
                    : '—'}
                </span>
              </div>
              <div>
                <span class="text-text-muted">Tagesschlechteste/r:</span>
                <span class="ml-1 font-medium">
                  {selectedEvening.max?.player
                    ? selectedEvening.max.player.charAt(0).toUpperCase() +
                      selectedEvening.max.player.slice(1) +
                      ' — ' +
                      formatNumber(selectedEvening.max.value ?? 0)
                    : '—'}
                </span>
              </div>
              <div>
                <span class="text-text-muted">Gesamt:</span>
                <span class="ml-1 font-medium">{formatNumber(selectedEvening.sum ?? 0)}</span>
              </div>
              <div>
                <span class="text-text-muted">Durchschnitt:</span>
                <span class="ml-1 font-medium">{formatNumber(selectedEvening.avg ?? 0)}</span>
              </div>
            </div>
          </div>
        {:else}
          <p class="mt-8 text-center text-text-disabled">Abend aus der Liste auswählen</p>
        {/if}
      </div>
    </div>
  {/if}
</div>
