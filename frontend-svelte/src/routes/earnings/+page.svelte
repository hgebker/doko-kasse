<script>
  import { createEarning, deleteEarning, listEarnings, updateEarning } from '$lib/api/earnings';
  import EarningDialog from '$lib/components/dialogs/EarningDialog.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast from '$lib/components/ui/Toast.svelte';
  import { SEMESTER_LABEL_MAPPING } from '$lib/constants/semesters';
  import { formatNumber } from '$lib/utils/format';

  let { data } = $props();

  let earnings = $state(data.earnings);
  let loading = $state(false);
  /** @type {{ message: string; type: 'success' | 'error' } | null} */
  let toast = $state(null);
  let dialogOpen = $state(false);
  let editTarget = $state(null);

  const columns = [
    { key: 'description', label: 'Beschreibung' },
    { key: 'value', label: 'Betrag', format: formatNumber },
    { key: 'semester', label: 'Semester', format: (v) => SEMESTER_LABEL_MAPPING[v] ?? v }
  ];

  const actions = [
    {
      label: 'Bearbeiten',
      onclick: (row) => {
        editTarget = row;
        dialogOpen = true;
      }
    },
    { label: 'Löschen', onclick: (row) => handleDelete(row.id) }
  ];

  async function reload() {
    loading = true;
    try {
      earnings = await listEarnings();
    } catch {
      toast = { message: 'Fehler beim Laden', type: 'error' };
    } finally {
      loading = false;
    }
  }

  async function handleSave(item) {
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
    } catch {
      toast = { message: 'Fehler beim Speichern', type: 'error' };
      loading = false;
    }
  }

  async function handleDelete(id) {
    loading = true;
    try {
      await deleteEarning(id);
      toast = { message: 'Einnahme gelöscht', type: 'success' };
      await reload();
    } catch {
      toast = { message: 'Fehler beim Löschen', type: 'error' };
      loading = false;
    }
  }
</script>

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

<div>
  <PageHeader title="Sonstige Einnahmen" count={earnings.length}>
    {#snippet actions()}
      <button
        onclick={reload}
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
</div>
