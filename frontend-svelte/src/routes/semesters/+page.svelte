<script lang="ts">
  import { invalidate } from '$app/navigation';
  import { createSemester, deleteSemester, updateSemester } from '$lib/api/semesters';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import SemesterDialog from '$lib/components/dialogs/SemesterDialog.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import MobileItemList from '$lib/components/ui/MobileItemList.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import type { Semester } from '$lib/types';
  import { ArrowCounterClockwiseIcon } from 'phosphor-svelte';
  import { MediaQuery } from 'svelte/reactivity';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');

  let loading = $state(false);
  let toast: ToastContent | null = $state(null);
  let dialogOpen = $state(false);
  let editTarget: Semester | null = $state(null);
  let selectedSemester: Semester | null = $state(null);
  let confirmOpen = $state(false);
  let deleteTarget: string | null = $state(null);

  const columns = [
    { key: 'sortKey', label: 'Reihenfolge' },
    { key: 'id', label: 'Schlüssel' },
    { key: 'label', label: 'Bezeichnung' }
  ];

  const actions = [
    {
      label: 'Bearbeiten',
      onclick: (row: Semester) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    {
      label: 'Löschen',
      onclick: (row: Semester) => {
        deleteTarget = row.id;
        confirmOpen = true;
      }
    }
  ];

  async function reload() {
    loading = true;
    try {
      await invalidate('app:semesters');
      selectedSemester = null;
    } finally {
      loading = false;
    }
  }

  async function handleSave(item: Semester) {
    loading = true;
    dialogOpen = false;
    try {
      if (editTarget) {
        await updateSemester(item);
        toast = { message: 'Semester aktualisiert', type: 'success' };
      } else {
        await createSemester(item);
        toast = { message: 'Semester angelegt', type: 'success' };
      }
      await invalidate('app:semesters');
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Speichern', type: 'error' };
    } finally {
      loading = false;
      editTarget = null;
    }
  }

  async function handleDelete(id: string) {
    loading = true;
    try {
      await deleteSemester(id);
      toast = { message: 'Semester gelöscht', type: 'success' };
      await invalidate('app:semesters');
      selectedSemester = null;
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Löschen', type: 'error' };
    } finally {
      loading = false;
    }
  }

  function confirmDelete() {
    confirmOpen = false;
    if (deleteTarget) handleDelete(deleteTarget);
    deleteTarget = null;
  }
</script>

<svelte:head>
  <title>Semester - Doko Kasse</title>
</svelte:head>

{#if loading}<Spinner />{/if}
<Toast bind:toast />

<SemesterDialog
  bind:open={dialogOpen}
  preset={editTarget}
  defaultSortKey={Math.max(...data.semesters.map((s) => s.sortKey), -1) + 1}
  onSave={handleSave}
  onClose={() => {
    editTarget = null;
  }}
/>

<ConfirmDialog
  bind:open={confirmOpen}
  title="Semester löschen"
  description="Möchtest du dieses Semester wirklich löschen? Bestehende Abende und Einnahmen behalten ihre Semesterzuordnung."
  onConfirm={confirmDelete}
  onCancel={() => {
    deleteTarget = null;
  }}
/>

<SplitPane
  supportingPaneClosable
  supportingPaneTitle={selectedSemester?.label ?? ''}
  bind:supportingPaneOpen={() => !!selectedSemester, () => (selectedSemester = null)}
>
  <PageHeader title="Semester" count={data.semesters.length}>
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
        onclick={() => {
          editTarget = null;
          dialogOpen = true;
        }}
        class="rounded-lg bg-action-primary px-3 py-2 font-medium text-action-primary-fg hover:bg-action-primary-hover"
      >
        Neu
      </button>
    {/snippet}
  </PageHeader>

  {#if isDesktop.current}
    <Table
      {columns}
      rows={data.semesters}
      {actions}
      selectable
      selected={selectedSemester}
      onselect={(row) => (selectedSemester = row)}
    />
  {:else}
    <MobileItemList
      label="Semester"
      items={data.semesters}
      getTitle={(s) => s.label}
      getSubtitle={(s) => s.id}
      onselect={(s) => (selectedSemester = s)}
      emptyText="Keine Semester"
    />
  {/if}

  {#snippet supportingPane()}
    {#if selectedSemester}
      <div class="mb-4 flex items-center justify-between">
        <p class="text-sm text-text-muted">{selectedSemester.id}</p>
        <div class="flex gap-2">
          <button
            onclick={() => {
              editTarget = selectedSemester;
              dialogOpen = true;
            }}
            class="rounded-lg border border-border-strong px-3 py-2 font-medium text-text-secondary hover:bg-surface-hover"
          >
            Bearbeiten
          </button>
          <button
            onclick={() => {
              if (selectedSemester) {
                deleteTarget = selectedSemester.id;
                confirmOpen = true;
              }
            }}
            class="rounded-lg border border-destruct-border px-3 py-2 font-medium text-destruct-text hover:bg-surface-hover"
          >
            Löschen
          </button>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-3">
        <div
          class="col-span-2 rounded-lg border border-border-subtle bg-surface-raised p-3 text-center"
        >
          <p class="text-sm font-medium uppercase tracking-wide text-text-muted">Bezeichnung</p>
          <p class="mt-1 text-base font-semibold text-text-primary">{selectedSemester.label}</p>
        </div>
        <div class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center">
          <p class="text-sm font-medium uppercase tracking-wide text-text-muted">Schlüssel</p>
          <p class="mt-1 text-base font-semibold text-text-primary">{selectedSemester.id}</p>
        </div>
        <div class="rounded-lg border border-border-subtle bg-surface-raised p-3 text-center">
          <p class="text-sm font-medium uppercase tracking-wide text-text-muted">Reihenfolge</p>
          <p class="mt-1 text-base font-semibold text-text-primary">{selectedSemester.sortKey}</p>
        </div>
      </div>
    {/if}
  {/snippet}
</SplitPane>
