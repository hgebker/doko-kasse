<script lang="ts">
  import FormField from '$lib/components/ui/FormField.svelte';
  import { LAST_SEMESTER, SEMESTER_LIST } from '$lib/constants/semesters';
  import type { Earning } from '$lib/types';
  import { Button, Dialog, Select } from 'bits-ui';
  import { CaretDownIcon } from 'phosphor-svelte';

  type Props = {
    open?: boolean;
    preset?: Earning | null;
    onSave: (item: Earning) => void;
    onClose: () => void;
  };
  let { open = $bindable(), preset = null, onSave, onClose }: Props = $props();

  let form = $state({ description: '', value: 0, semester: LAST_SEMESTER.id });

  $effect(() => {
    if (open) {
      form = preset
        ? { description: preset.description, value: preset.value, semester: preset.semester }
        : { description: '', value: 0, semester: LAST_SEMESTER.id };
    }
  });

  const selectedSemesterLabel = $derived(
    SEMESTER_LIST.find((s) => s.id === form.semester)?.label ?? form.semester
  );

  function save() {
    const item: Earning = { ...form, value: Number(form.value) };
    if (preset?.id) item.id = preset.id;
    onSave(item);
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
        {preset ? 'Einnahme bearbeiten' : 'Einnahme hinzufügen'}
      </Dialog.Title>

      <div class="flex flex-col gap-4">
        <FormField label="Beschreibung">
          <input
            type="text"
            bind:value={form.description}
            placeholder="z.B. Beiträge SS22"
            class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
          />
        </FormField>

        <FormField label="Betrag (€)">
          <input
            type="number"
            step="0.01"
            bind:value={form.value}
            class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
          />
        </FormField>

        <FormField label="Semester">
          <Select.Root
            type="single"
            value={form.semester}
            onValueChange={(v) => {
              if (v) form.semester = v;
            }}
          >
            <Select.Trigger
              class="flex w-full items-center justify-between rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary focus:border-border-strong focus:outline-none"
            >
              {selectedSemesterLabel}
              <CaretDownIcon size="16" class="text-text-disabled" />
            </Select.Trigger>
            <Select.Content
              class="z-50 max-h-60 overflow-auto rounded-lg border border-border-default bg-surface-raised py-1 shadow-lg"
            >
              {#each SEMESTER_LIST as sem}
                <Select.Item
                  value={sem.id}
                  class="cursor-pointer px-3 py-1.5 text-sm text-text-secondary hover:bg-surface-hover data-[highlighted]:bg-surface-hover data-[state=checked]:font-medium"
                >
                  {sem.label}
                </Select.Item>
              {/each}
            </Select.Content>
          </Select.Root>
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
          class="rounded-lg bg-action-primary px-4 py-2 text-sm font-medium text-action-primary-fg hover:bg-action-primary-hover"
        >
          Speichern
        </Button.Root>
      </div>
    </Dialog.Content>
  </Dialog.Portal>
</Dialog.Root>
