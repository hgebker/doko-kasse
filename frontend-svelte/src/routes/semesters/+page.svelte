<script lang="ts">
  import { createSemester, deleteSemester, updateSemester } from '$lib/api/semesters';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import SemesterDialog from '$lib/components/dialogs/SemesterDialog.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import type { SemesterEntry } from '$lib/types';
  import { invalidate } from '$app/navigation';
  import { ArrowCounterClockwiseIcon } from 'phosphor-svelte';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  let loading = $state(false);
  let toast: ToastContent | null = $state(null);
  let dialogOpen = $state(false);
  let editTarget: SemesterEntry | null = $state(null);
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
      onclick: (row: SemesterEntry) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    {
      label: 'Löschen',
      onclick: (row: SemesterEntry) => {
        deleteTarget = row.id;
        confirmOpen = true;
      }
    }
  ];

  async function reload() {
    loading = true;
    try {
      await invalidate('app:semesters');
    } finally {
      loading = false;
    }
  }

  async function handleSave(item: SemesterEntry) {
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

<SplitPane>
  <PageHeader title="Semester" count={data.semesters.length}>
    {#snippet actions()}
      <button
        onclick={reload}
        class="rounded-lg border border-border-strong p-2 text-text-secondary hover:bg-surface-hover"
        aria-label="Aktualisieren"
        title="Aktualisieren"
      >
        <ArrowCounterClockwiseIcon size="16" />
      </button>
      <button
        onclick={() => {
          editTarget = null;
          dialogOpen = true;
        }}
        class="rounded-lg bg-action-primary px-3 py-2 text-sm font-medium text-action-primary-fg hover:bg-action-primary-hover"
      >
        Neu
      </button>
    {/snippet}
  </PageHeader>

  <Table {columns} rows={data.semesters} {actions} />
</SplitPane>
