<script lang="ts">
  import type { Snippet } from 'svelte';

  type Props = {
    title: string;
    count?: number | null;
    actions?: Snippet;
    controls?: Snippet;
  };
  let { title, count = null, actions, controls }: Props = $props();
</script>

<div
  class="mb-4 flex flex-wrap items-center justify-between gap-2 border-b border-border-default pb-3"
>
  <div class="flex items-baseline gap-2">
    <h1 class="text-xl font-semibold text-text-primary">{title}</h1>
    {#if count !== null}
      <span class="text-sm text-text-muted">{count} Ergebnisse</span>
    {/if}
  </div>
  <div class="flex flex-wrap items-center gap-2">
    {#if controls}
      {@render controls()}
    {/if}
    {#if actions}
      {@render actions()}
    {/if}
  </div>
</div>
