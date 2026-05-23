<script lang="ts">
  import { DropdownMenu } from 'bits-ui';
  import { DotsThreeVerticalIcon } from 'phosphor-svelte';

  type Column = {
    key: string;
    label: string;
    format?: (v: any) => string;
  };

  type Action = {
    label: string;
    onclick: (row: any) => void;
  };

  type Props = {
    columns: Column[];
    rows: Record<string, any>[];
    actions?: Action[];
    docked?: boolean;
    selectable?: boolean;
    selected?: any;
    onselect?: (row: any) => void;
    maxHeight?: string;
    readonly?: boolean;
  };
  let {
    columns,
    rows,
    actions = [],
    docked = false,
    selectable = false,
    selected = null,
    onselect,
    maxHeight,
    readonly = false
  }: Props = $props();
</script>

<div
  class={[
    'overflow-auto bg-surface-base shadow-sm',
    !docked && 'border border-border-default rounded-lg'
  ]}
  style={maxHeight ? `max-height: ${maxHeight}` : ''}
>
  <table class="w-full text-sm">
    <thead class="sticky top-0 bg-surface-raised">
      <tr>
        {#each columns as col}
          <th class="px-3 py-2 text-left font-semibold text-text-secondary">{col.label}</th>
        {/each}
        {#if actions.length > 0 && !readonly}
          <th class="w-10 px-3 py-2"></th>
        {/if}
      </tr>
    </thead>
    <tbody>
      {#each rows as row, i}
        <tr
          class="border-t border-border-subtle transition-colors"
          class:bg-surface-raised={selectable && selected?.id === row.id}
          class:hover:bg-surface-hover={!selectable || selected?.id !== row.id}
          class:cursor-pointer={selectable}
          onclick={() => {
            if (selectable && onselect) onselect(row);
          }}
        >
          {#each columns as col}
            <td class="px-3 py-2 text-text-secondary">
              {#if col.format}
                {col.format(row[col.key])}
              {:else}
                {row[col.key] ?? ''}
              {/if}
            </td>
          {/each}
          {#if actions.length > 0 && !readonly}
            <td class="px-2 py-1 text-right">
              <DropdownMenu.Root>
                <DropdownMenu.Trigger
                  class="rounded p-1 text-text-disabled hover:bg-surface-hover hover:text-text-primary"
                  aria-label="Aktionen"
                >
                  <DotsThreeVerticalIcon size="16" />
                </DropdownMenu.Trigger>
                <DropdownMenu.Content
                  class="z-30 min-w-[8rem] rounded-lg border border-border-default bg-surface-raised py-1 shadow-lg"
                >
                  {#each actions as action}
                    <DropdownMenu.Item
                      class="flex w-full cursor-pointer px-3 py-1.5 text-sm text-text-secondary hover:bg-surface-hover data-[highlighted]:bg-surface-hover"
                      onclick={() => action.onclick(row)}
                    >
                      {action.label}
                    </DropdownMenu.Item>
                  {/each}
                </DropdownMenu.Content>
              </DropdownMenu.Root>
            </td>
          {/if}
        </tr>
      {/each}
      {#if rows.length === 0}
        <tr>
          <td
            colspan={columns.length +
              (selectable ? 1 : 0) +
              (actions.length > 0 && !readonly ? 1 : 0)}
            class="px-3 py-8 text-center text-text-disabled"
          >
            Keine Einträge vorhanden
          </td>
        </tr>
      {/if}
    </tbody>
  </table>
</div>
