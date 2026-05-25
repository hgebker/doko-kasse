<script lang="ts">
  import FormField from '$lib/components/ui/FormField.svelte';
  import { PLAYERS } from '$lib/constants/players';
  import type { Evening, EveningInput, Semester } from '$lib/types';
  import { capitalize } from '$lib/utils/format';
  import { type DateValue, parseDate } from '@internationalized/date';
  import { Button, DatePicker, Dialog, Select } from 'bits-ui';
  import CalendarBlankIcon from 'phosphor-svelte/lib/CalendarBlankIcon';
  import CaretDownIcon from 'phosphor-svelte/lib/CaretDownIcon';
  import CaretLeftIcon from 'phosphor-svelte/lib/CaretLeftIcon';
  import CaretRightIcon from 'phosphor-svelte/lib/CaretRightIcon';

  function today() {
    return new Date().toISOString().slice(0, 10);
  }

  type Props = {
    semesters: Semester[];
    open?: boolean;
    preset?: Evening | null;
    onSave: (item: EveningInput) => void;
    onClose: () => void;
  };
  let { semesters, open = $bindable(), preset = null, onSave, onClose }: Props = $props();

  function emptyForm() {
    return {
      date: today(),
      semester: semesters.at(-1)?.id ?? '',
      tim: 0,
      jan: 0,
      ole: 0,
      hannes: 0,
      louisa: 0
    };
  }

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
    semesters.find((s) => s.id === form.semester)?.label ?? form.semester
  );

  function save() {
    const item: EveningInput = {
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
      interactOutsideBehavior="ignore"
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
              <CaretDownIcon size="16" class="text-text-muted" />
            </Select.Trigger>
            <Select.Content
              class="z-50 max-h-60 overflow-auto rounded-lg border border-border-default bg-surface-raised py-1 shadow-lg"
            >
              {#each semesters as sem}
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
          <DatePicker.Root
            weekdayFormat="short"
            fixedWeeks
            weekStartsOn={1}
            locale="de"
            bind:value={
              () => parseDate(form.date), (value: DateValue) => (form.date = value.toString())
            }
          >
            <div class="flex w-full flex-col gap-1.5">
              <DatePicker.Input
                class="w-full rounded-lg border border-border-strong bg-surface-raised px-3 py-2 text-sm text-text-primary placeholder:text-text-disabled focus:border-border-strong focus:outline-none flex items-center"
              >
                {#snippet children({ segments })}
                  {#each segments as { part, value }, i (part + i)}
                    <div class="inline-block select-none">
                      {#if part === 'literal'}
                        <DatePicker.Segment {part} class="text-muted-foreground p-1">
                          {value}
                        </DatePicker.Segment>
                      {:else}
                        <DatePicker.Segment
                          {part}
                          class="rounded-5px hover:bg-muted focus:bg-muted focus:text-foreground aria-[valuetext=Empty]:text-muted-foreground focus-visible:ring-0! focus-visible:ring-offset-0!"
                        >
                          {value}
                        </DatePicker.Segment>
                      {/if}
                    </div>
                  {/each}
                  <DatePicker.Trigger
                    class="text-foreground/60 hover:bg-muted active:bg-dark-10 ml-auto inline-flex items-center justify-center rounded-[5px] transition-all"
                  >
                    <CalendarBlankIcon size="16" />
                  </DatePicker.Trigger>
                {/snippet}
              </DatePicker.Input>

              <DatePicker.Content sideOffset={6} class="z-50">
                <DatePicker.Calendar
                  class="border border-border-strong bg-surface-raised shadow-popover rounded-[15px] p-[22px]"
                >
                  {#snippet children({ months, weekdays })}
                    <DatePicker.Header class="flex items-center justify-between">
                      <DatePicker.PrevButton
                        class="rounded-9px bg-surface-raised hover:bg-muted inline-flex size-10 items-center justify-center transition-all active:scale-[0.98]"
                      >
                        <CaretLeftIcon class="size-6" />
                      </DatePicker.PrevButton>
                      <DatePicker.Heading class="text-[15px] font-medium" />
                      <DatePicker.NextButton
                        class="rounded-9px bg-surface-raised hover:bg-muted inline-flex size-10 items-center justify-center transition-all active:scale-[0.98]"
                      >
                        <CaretRightIcon class="size-6" />
                      </DatePicker.NextButton>
                    </DatePicker.Header>

                    <div class="flex flex-col space-y-4 pt-4 sm:flex-row sm:space-x-4 sm:space-y-0">
                      {#each months as month (month.value)}
                        <DatePicker.Grid class="w-full border-collapse select-none space-y-1">
                          <DatePicker.GridHead>
                            <DatePicker.GridRow class="mb-1 flex w-full justify-between">
                              {#each weekdays as day (day)}
                                <DatePicker.HeadCell
                                  class="text-muted-foreground font-normal! w-10 rounded-md text-xs"
                                >
                                  <div>{day.slice(0, 2)}</div>
                                </DatePicker.HeadCell>
                              {/each}
                            </DatePicker.GridRow>
                          </DatePicker.GridHead>
                          <DatePicker.GridBody>
                            {#each month.weeks as weekDates (weekDates)}
                              <DatePicker.GridRow class="flex w-full">
                                {#each weekDates as date (date)}
                                  <DatePicker.Cell
                                    {date}
                                    month={month.value}
                                    class="p-0! relative size-10 text-center text-sm"
                                  >
                                    <DatePicker.Day
                                      class="rounded-9px text-foreground hover:border-foreground data-selected:bg-foreground data-disabled:text-foreground/30 data-selected:text-background data-unavailable:text-muted-foreground data-disabled:pointer-events-none data-outside-month:pointer-events-none data-selected:font-medium data-unavailable:line-through group relative inline-flex size-10 items-center justify-center whitespace-nowrap border border-transparent bg-transparent p-0 text-sm font-normal transition-all"
                                    >
                                      <div
                                        class="bg-foreground group-data-selected:bg-background group-data-today:block absolute top-[5px] hidden size-1 rounded-full transition-all"
                                      ></div>
                                      {date.day}
                                    </DatePicker.Day>
                                  </DatePicker.Cell>
                                {/each}
                              </DatePicker.GridRow>
                            {/each}
                          </DatePicker.GridBody>
                        </DatePicker.Grid>
                      {/each}
                    </div>
                  {/snippet}
                </DatePicker.Calendar>
              </DatePicker.Content>
            </div>
          </DatePicker.Root>
        </FormField>

        <div class="grid grid-cols-2 gap-3 sm:grid-cols-3">
          {#each PLAYERS as player}
            <FormField label={capitalize(player)}>
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
