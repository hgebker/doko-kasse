<script lang="ts">
  import type { Snippet } from 'svelte';

  type Props = {
    label: string;
    items: Record<string, any>[];
    getTitle: (item: any) => string;
    getSubtitle: (item: any) => string;
    onselect: (item: any) => void;
    emptyText?: string;
    headerAction?: Snippet;
  };

  let { label, items, getTitle, getSubtitle, onselect, emptyText = 'Keine Einträge', headerAction }: Props = $props();
</script>

<div class="overflow-hidden rounded-lg border border-border-default bg-surface-base shadow-sm">
  <div class="flex items-center justify-between border-b border-border-subtle p-3">
    <span class="text-xs font-semibold uppercase tracking-wide text-text-disabled">{label}</span>
    {#if headerAction}
      {@render headerAction()}
    {/if}
  </div>
  <ul>
    {#each items as item}
      <li>
        <button
          onclick={() => onselect(item)}
          class="flex w-full flex-col px-4 py-3 text-left transition-colors hover:bg-surface-hover"
        >
          <span class="font-medium text-text-primary">{getTitle(item)}</span>
          <span class="text-xs text-text-muted">{getSubtitle(item)}</span>
        </button>
      </li>
    {/each}
    {#if items.length === 0}
      <li class="p-4 text-center text-sm text-text-disabled">{emptyText}</li>
    {/if}
  </ul>
</div>
