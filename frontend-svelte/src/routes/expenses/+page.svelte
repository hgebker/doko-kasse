<script lang="ts">
  import { createExpense, deleteExpense, listExpenses, updateExpense } from '$lib/api/expenses';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import ExpenseDialog from '$lib/components/dialogs/ExpenseDialog.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import MobileItemList from '$lib/components/ui/MobileItemList.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import type { Expense } from '$lib/types';
  import { formatNumber } from '$lib/utils/format';
  import { ArrowCounterClockwiseIcon } from 'phosphor-svelte';
  import { MediaQuery } from 'svelte/reactivity';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');

  let expenses = $derived(data.expenses);
  let loading = $state(false);
  let toast: ToastContent | null = $state(null);
  let dialogOpen = $state(false);
  let editTarget: Expense | null = $state(null);
  let selectedExpense: Expense | null = $state(null);

  let confirmOpen = $state(false);
  let deleteTarget: string | null = $state(null);

  const semesterLabel = $derived((id: string): string => {
    if (!id) return '';
    return data.semesters.find((s) => s.id === id)?.label ?? id;
  });

  const columns = $derived([
    { key: 'description', label: 'Beschreibung' },
    { key: 'value', label: 'Betrag', format: formatNumber },
    {
      key: 'semester',
      label: 'Semester',
      format: (v: string) => data.semesters.find((s) => s.id === v)?.label ?? v
    }
  ]);

  const actions = [
    {
      label: 'Bearbeiten',
      onclick: (row: Expense) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    {
      label: 'Löschen',
      onclick: (row: Expense) => {
        if (row.id) {
          deleteTarget = row.id;
          confirmOpen = true;
        }
      }
    }
  ];

  async function reload() {
    loading = true;
    try {
      expenses = await listExpenses();
      selectedExpense = null;
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
    editTarget = null;
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
  title="Ausgabe löschen"
  description="Möchtest du diese Ausgabe wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden."
  onConfirm={confirmDelete}
  onCancel={() => {
    deleteTarget = null;
  }}
/>

<SplitPane
  supportingPaneClosable
  supportingPaneTitle={selectedExpense?.description ?? ''}
  bind:supportingPaneOpen={() => !!selectedExpense, () => (selectedExpense = null)}
>
  <PageHeader title="Ausgaben" count={expenses.length}>
    {#snippet actions()}
      <button
        onclick={reload}
        class="rounded-lg border border-border-strong p-2 text-text-secondary hover:bg-surface-hover"
        aria-label="Aktualisieren"
        title="Aktualisieren"
      >
        <ArrowCounterClockwiseIcon size="20" />
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

  {#if isDesktop.current}
    <Table
      {columns}
      rows={expenses}
      {actions}
      selectable
      selected={selectedExpense}
      onselect={(row) => (selectedExpense = row)}
    />
  {:else}
    <MobileItemList
      label="Ausgaben"
      items={expenses}
      getTitle={(e) => e.description}
      getSubtitle={(e) => `${formatNumber(e.value)} · ${semesterLabel(e.semester)}`}
      onselect={(e) => (selectedExpense = e)}
      emptyText="Keine Ausgaben"
    />
  {/if}

  {#snippet supportingPane()}
    {#if selectedExpense}
      <div class="mb-4 flex items-center justify-between">
        <p class="text-sm text-text-muted">{semesterLabel(selectedExpense.semester)}</p>
        <div class="flex gap-2">
          <button
            onclick={() => {
              editTarget = selectedExpense;
              dialogOpen = true;
            }}
            class="rounded-lg border border-border-strong px-3 py-1.5 text-sm font-medium text-text-secondary hover:bg-surface-hover"
          >
            Bearbeiten
          </button>
          <button
            onclick={() => {
              if (selectedExpense?.id) {
                deleteTarget = selectedExpense.id;
                confirmOpen = true;
              }
            }}
            class="rounded-lg border border-destruct-border px-3 py-1.5 text-sm font-medium text-destruct-text hover:bg-surface-hover"
          >
            Löschen
          </button>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-3">
        <div
          class="col-span-2 rounded-lg border border-border-subtle bg-surface-raised p-3 text-center"
        >
          <p class="text-xs font-medium uppercase tracking-wide text-text-muted">Beschreibung</p>
          <p class="mt-1 text-base font-semibold text-text-primary">
            {selectedExpense.description}
          </p>
        </div>
        <div
          class="col-span-2 rounded-lg border border-border-subtle bg-surface-raised p-3 text-center"
        >
          <p class="text-xs font-medium uppercase tracking-wide text-text-muted">Betrag</p>
          <p class="mt-1 text-base font-semibold text-text-primary">
            {formatNumber(selectedExpense.value)}
          </p>
        </div>
      </div>
    {/if}
  {/snippet}
</SplitPane>
