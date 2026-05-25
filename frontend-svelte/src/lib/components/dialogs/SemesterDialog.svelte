<script lang="ts">
  import FormField from '$lib/components/ui/FormField.svelte';
  import type { SemesterEntry } from '$lib/types';
  import { Button, Dialog } from 'bits-ui';

  type Props = {
    open?: boolean;
    preset?: SemesterEntry | null;
    defaultSortKey?: number;
    onSave: (item: SemesterEntry) => void;
    onClose: () => void;
  };
  let { open = $bindable(), preset = null, defaultSortKey = 0, onSave, onClose }: Props = $props();

  let form = $state({ id: '', label: '', sortKey: 0 });

  $effect(() => {
    if (open) {
      form = preset
        ? { id: preset.id, label: preset.label, sortKey: preset.sortKey }
        : { id: '', label: '', sortKey: defaultSortKey };
    }
  });

  function save() {
    onSave({ id: form.id.trim(), label: form.label.trim(), sortKey: form.sortKey });
  }
</script>

<Dialog.Root
  bind:open
  onOpenChange={(o) => {
    if (!o) onClose();
  }}
>
  <Dialog.Portal>
    <Dialog.Overlay class="fixed inset-0 z-40 bg-black/60" />
    <Dialog.Content
      class="fixed left-1/2 top-1/2 z-50 w-full max-w-sm -translate-x-1/2 -translate-y-1/2 rounded-xl bg-surface-base p-6 shadow-xl"
      interactOutsideBehavior="ignore"
    >
      <Dialog.Title class="mb-4 text-lg font-semibold text-text-primary">
        {preset ? 'Semester bearbeiten' : 'Semester anlegen'}
      </Dialog.Title>

      <div class="flex flex-col gap-4">
        {#if preset}
          <FormField label="Schlüssel">
            <p class="rounded-lg border border-border-subtle bg-surface-raised px-3 py-2 text-sm text-text-disabled">
              {form.id}
            </p>
          </FormField>
        {:else}
          <FormField label="Schlüssel">
            <input
              type="text"
              bind:value={form.id}
              placeholder="z.B. season2627"
              class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
            />
          </FormField>
        {/if}

        <FormField label="Bezeichnung">
          <input
            type="text"
            bind:value={form.label}
            placeholder="z.B. Saison 26/27"
            class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
          />
        </FormField>

        <FormField label="Reihenfolge">
          <input
            type="number"
            bind:value={form.sortKey}
            class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
          />
        </FormField>
      </div>

      <div class="mt-6 flex justify-end gap-3">
        <Dialog.Close
          class="rounded-lg border border-border-strong px-4 py-2 text-sm font-medium text-text-secondary hover:bg-surface-hover"
        >
          Abbrechen
        </Dialog.Close>

        <Button.Root
          onclick={save}
          disabled={!form.id.trim() || !form.label.trim()}
          class="rounded-lg bg-action-primary px-4 py-2 text-sm font-medium text-action-primary-fg hover:bg-action-primary-hover disabled:opacity-50"
        >
          Speichern
        </Button.Root>
      </div>
    </Dialog.Content>
  </Dialog.Portal>
</Dialog.Root>
