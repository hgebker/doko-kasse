<script>
  import { Dialog, Select } from 'bits-ui';
  import FormField from '$lib/components/ui/FormField.svelte';
  import { SEMESTER_LIST, LAST_SEMESTER } from '$lib/constants/semesters';
  import { PLAYERS } from '$lib/constants/players';

  function today() {
    return new Date().toISOString().slice(0, 10);
  }

  function emptyForm() {
    return {
      date: today(),
      semester: LAST_SEMESTER.id,
      tim: 0,
      jan: 0,
      ole: 0,
      hannes: 0,
      louisa: 0
    };
  }

  let { open = $bindable(), preset = null, onSave, onClose } = $props();

  let form = $state(emptyForm());

  $effect(() => {
    if (open) {
      form = preset
        ? {
            date: preset.date,
            semester: preset.semester,
            tim: preset.tim ?? 0,
            jan: preset.jan ?? 0,
            ole: preset.ole ?? 0,
            hannes: preset.hannes ?? 0,
            louisa: preset.louisa ?? 0
          }
        : emptyForm();
    }
  });

  const selectedSemesterLabel = $derived(
    SEMESTER_LIST.find((s) => s.id === form.semester)?.label ?? form.semester
  );

  function save() {
    const item = {
      date: form.date,
      semester: form.semester,
      tim: Number(form.tim),
      jan: Number(form.jan),
      ole: Number(form.ole),
      hannes: Number(form.hannes),
      louisa: Number(form.louisa)
    };
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
      class="fixed left-1/2 top-1/2 z-50 w-full max-w-md -translate-x-1/2 -translate-y-1/2 rounded-xl bg-surface-base p-6 shadow-xl"
    >
      <Dialog.Title class="mb-4 text-lg font-semibold text-text-primary">
        {preset ? 'Abend bearbeiten' : 'Abend anlegen'}
      </Dialog.Title>

      <div class="flex flex-col gap-4">
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
              <svg
                class="h-4 w-4 text-text-disabled"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M19 9l-7 7-7-7"
                />
              </svg>
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

        <FormField label="Datum">
          <input
            type="date"
            bind:value={form.date}
            class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
          />
        </FormField>

        <div class="grid grid-cols-2 gap-3 sm:grid-cols-3">
          {#each PLAYERS as player}
            <FormField label={player.charAt(0).toUpperCase() + player.slice(1)}>
              <input
                type="number"
                step="0.01"
                bind:value={form[player]}
                class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none"
              />
            </FormField>
          {/each}
        </div>
      </div>

      <div class="mt-6 flex justify-end gap-3">
        <Dialog.Close
          class="rounded-lg border border-border-strong px-4 py-2 text-sm font-medium text-text-secondary hover:bg-surface-hover"
        >
          Abbrechen
        </Dialog.Close>
        <button
          onclick={save}
          class="rounded-lg bg-action-primary px-4 py-2 text-sm font-medium text-action-primary-fg hover:bg-action-primary-hover"
        >
          Speichern
        </button>
      </div>
    </Dialog.Content>
  </Dialog.Portal>
</Dialog.Root>
