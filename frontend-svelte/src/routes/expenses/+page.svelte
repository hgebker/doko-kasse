<script lang="ts">
  import { createExpense, deleteExpense, listExpenses, updateExpense } from '$lib/api/expenses';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import ExpenseDialog from '$lib/components/dialogs/ExpenseDialog.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
  import type { Expense } from '$lib/types';
  import { formatNumber } from '$lib/utils/format';
  import { ArrowCounterClockwiseIcon } from 'phosphor-svelte';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  let expenses = $derived(data.expenses);
  let loading = $state(false);
  let toast: ToastContent | null = $state(null);
  let dialogOpen = $state(false);
  let editTarget: Expense | null = $state(null);

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
      onclick: (row: Expense) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    { label: 'Löschen', onclick: (row: Expense) => { if (row.id) { deleteTarget = row.id; confirmOpen = true; } } }
  ];

  async function reload() {
    loading = true;
    try {
      expenses = await listExpenses();
    } catch (e) {
      toast = { message: e instanceof Error ? e.message : 'Fehler beim Laden', type: 'error' };
    } finally {
      loading = false;
    }
  }

  async function handleSave(item: Expense) {
    loading = true;
    dialogOpen = false;
    try {
      if (item.id) {
        await updateExpense(item);
        toast = { message: 'Ausgabe aktualisiert', type: 'success' };
      } else {
        await createExpense(item);
        toast = { message: 'Ausgabe hinzugefügt', type: 'success' };
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
      await deleteExpense(id);
      toast = { message: 'Ausgabe gelöscht', type: 'success' };
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
  <title>Ausgaben - Doko Kasse</title>
</svelte:head>

{#if loading}<Spinner />{/if}
<Toast bind:toast />

<ExpenseDialog
  bind:open={dialogOpen}
  preset={editTarget}
  onSave={handleSave}
  onClose={() => {
    editTarget = null;
  }}
/>

<ConfirmDialog
  bind:open={confirmOpen}
  title="Ausgabe löschen"
  description="Möchtest du diese Ausgabe wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden."
  onConfirm={confirmDelete}
  onCancel={() => { deleteTarget = null; }}
/>

<SplitPane>
  <PageHeader title="Ausgaben" count={expenses.length}>
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

  <Table {columns} rows={expenses} {actions} />
</SplitPane>
