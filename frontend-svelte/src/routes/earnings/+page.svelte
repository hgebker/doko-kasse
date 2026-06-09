<script lang="ts">
  import { invalidate } from '$app/navigation';
  import { createEarning, deleteEarning, updateEarning } from '$lib/api/earnings';
  import ConfirmDialog from '$lib/components/dialogs/ConfirmDialog.svelte';
  import EarningDialog from '$lib/components/dialogs/EarningDialog.svelte';
  import SplitPane from '$lib/components/layout/SplitPane.svelte';
  import MobileItemList from '$lib/components/ui/MobileItemList.svelte';
  import PageHeader from '$lib/components/ui/PageHeader.svelte';
  import Spinner from '$lib/components/ui/Spinner.svelte';
  import Table from '$lib/components/ui/Table.svelte';
  import Toast, { type ToastContent } from '$lib/components/ui/Toast.svelte';
  import type { Earning } from '$lib/types';
  import { formatNumber } from '$lib/utils/format';
  import { ArrowCounterClockwiseIcon } from 'phosphor-svelte';
  import { MediaQuery } from 'svelte/reactivity';
  import type { PageProps } from './$types';

  let { data }: PageProps = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');

  let saving = $state(false);
  let toast: ToastContent | null = $state(null);
  let dialogOpen = $state(false);
  let editTarget: Earning | null = $state(null);
  let selectedEarning: Earning | null = $state(null);

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

  async function reload() {
    await invalidate('app:earnings');
    selectedEarning = null;
  }

  async function handleSave(item: Earning) {
    saving = true;
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
    } finally {
      saving = false;
    }
    editTarget = null;
  }

  async function handleDelete(id: string) {
    saving = true;
    try {
      await deleteEarning(id);
      toast = { message: 'Einnahme gelöscht', type: 'success' };
      await reload();
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
</script>

<svelte:head>
  <title>Einnahmen - Doko Kasse</title>
</svelte:head>

{#if saving}<Spinner />{/if}
<Toast bind:toast />

<EarningDialog
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
  title="Einnahme löschen"
  description="Möchtest du diese Einnahme wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden."
  onConfirm={confirmDelete}
  onCancel={() => {
    deleteTarget = null;
  }}
/>

{#await data.earnings}
  <Spinner />
{:then earnings}
  <SplitPane
    supportingPaneClosable
    supportingPaneTitle={selectedEarning?.description ?? ''}
    bind:supportingPaneOpen={() => !!selectedEarning, () => (selectedEarning = null)}
  >
    <PageHeader title="Einnahmen" count={earnings.length}>
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
        rows={earnings}
        selectable
        selected={selectedEarning}
        onselect={(row) => (selectedEarning = row)}
      />
    {:else}
      <MobileItemList
        label="Einnahmen"
        items={earnings}
        getTitle={(e) => e.description}
        getSubtitle={(e) => `${formatNumber(e.value)} · ${semesterLabel(e.semester)}`}
        onselect={(e) => (selectedEarning = e)}
        emptyText="Keine Einnahmen"
      />
    {/if}

    {#snippet supportingPane()}
      {#if selectedEarning}
        <div class="mb-4 flex items-center justify-between">
          <p class="text-text-muted">{semesterLabel(selectedEarning.semester)}</p>
          <div class="flex gap-2">
            <button
              onclick={() => {
                editTarget = selectedEarning;
                dialogOpen = true;
              }}
              class="rounded-lg border border-border-strong px-3 py-2 font-medium text-text-secondary hover:bg-surface-hover"
            >
              Bearbeiten
            </button>
            <button
              onclick={() => {
                if (selectedEarning?.id) {
                  deleteTarget = selectedEarning.id;
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
            <p class="text-sm font-medium uppercase tracking-wide text-text-muted">Beschreibung</p>
            <p class="mt-1 text-base font-semibold text-text-primary">
              {selectedEarning.description}
            </p>
          </div>
          <div
            class="col-span-2 rounded-lg border border-border-subtle bg-surface-raised p-3 text-center"
          >
            <p class="text-sm font-medium uppercase tracking-wide text-text-muted">Betrag</p>
            <p class="mt-1 text-base font-semibold text-text-primary">
              {formatNumber(selectedEarning.value)}
            </p>
          </div>
        </div>
      {/if}
    {/snippet}
  </SplitPane>
{/await}
