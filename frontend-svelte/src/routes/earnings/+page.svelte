<script lang="ts">
  import { createEarning, deleteEarning, listEarnings, updateEarning } from '$lib/api/earnings';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import EarningDialog from '$lib/components/dialogs/EarningDialog.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
  import type { Earning } from '$lib/types';
  import { formatNumber } from '$lib/utils/format';
  import { ArrowCounterClockwiseIcon } from 'phosphor-svelte';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  let earnings = $derived(data.earnings);
  let loading = $state(false);
  let toast: ToastContent | null = $state(null);
  let dialogOpen = $state(false);
  let editTarget: Earning | null = $state(null);

  let confirmOpen = $state(false);
  let deleteTarget: string | null = $state(null);

  const columns = [
    { key: 'description', label: 'Beschreibung' },
    { key: 'value', label: 'Betrag', format: formatNumber },
    { key: 'semester', label: 'Semester', format: (v: string) => SEMESTER_LABEL_MAPPING[v] ?? v }
  ];

  const actions = [
    {
      label: 'Bearbeiten',
      onclick: (row: Earning) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    { label: 'Löschen', onclick: (row: Earning) => { if (row.id) { deleteTarget = row.id; confirmOpen = true; } } }
  ];

  async function reload() {
    loading = true;
    try {
      earnings = await listEarnings();
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Laden', type: 'error' };
    } finally {
      loading = false;
    }
  }

  async function handleSave(item: Earning) {
    loading = true;
    dialogOpen = false;
    try {
      if (item.id) {
        await updateEarning(item);
        toast = { message: 'Einnahme aktualisiert', type: 'success' };
      } else {
        await createEarning(item);
        toast = { message: 'Einnahme hinzugefügt', type: 'success' };
      }
      await reload();
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Speichern', type: 'error' };
      loading = false;
    }
  }

  async function handleDelete(id: string) {
    loading = true;
    try {
      await deleteEarning(id);
      toast = { message: 'Einnahme gelöscht', type: 'success' };
      await reload();
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Löschen', type: 'error' };
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
  <title>Einnahmen - Doko Kasse</title>
</svelte:head>

{#if loading}<Spinner />{/if}
<Toast bind:toast />

<EarningDialog
  bind:open={dialogOpen}
  preset={editTarget}
  onSave={handleSave}
  onClose={() => {
    editTarget = null;
  }}
/>

<ConfirmDialog
  bind:open={confirmOpen}
  title="Einnahme löschen"
  description="Möchtest du diese Einnahme wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden."
  onConfirm={confirmDelete}
  onCancel={() => { deleteTarget = null; }}
/>

<SplitPane>
  <PageHeader title="Einnahmen" count={earnings.length}>
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

  <Table {columns} rows={earnings} {actions} />
</SplitPane>
