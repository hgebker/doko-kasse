<script lang="ts">
  import type { Snippet } from 'svelte';
  import { MediaQuery } from 'svelte/reactivity';

  type Props = {
    children: Snippet;
    supportingPane?: Snippet;
    detailPaneTitle?: string;
    supportingPaneTitle?: string;
    supportingPaneOpen?: boolean;
    supportingPaneClosable?: boolean;
  };
  let {
    children,
    supportingPane,
    detailPaneTitle = '',
    supportingPaneTitle = '',
    supportingPaneOpen = $bindable(false),
    supportingPaneClosable = false
  }: Props = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');
</script>

{#if isDesktop.current}
  <div class="flex h-full flex-row">
    <!-- Detail pane -->
    <div class="flex flex-1 flex-col overflow-hidden">
      {#if detailPaneTitle}
        <div class="flex h-12 shrink-0 items-center border-b border-border-subtle px-4">
          <span class="text-sm font-semibold text-text-secondary">{detailPaneTitle}</span>
        </div>
      {/if}
      <div class="flex-1 overflow-y-auto p-4">
        {@render children()}
      </div>
    </div>

    <!-- Supporting pane -->
    {#if supportingPane}
      <div
        class={[
          'flex flex-col border-l border-border-default bg-surface-base overflow-hidden transition-[width] duration-200 ease-in-out shrink-0',
          supportingPaneOpen ? 'w-80' : 'w-0'
        ].join(' ')}
      >
        <div class="flex h-12 shrink-0 items-center justify-between border-b border-border-subtle px-4">
          <span class="text-sm font-semibold text-text-secondary truncate">{supportingPaneTitle}</span>
          {#if supportingPaneClosable}
            <button
              onclick={() => (supportingPaneOpen = false)}
              class="ml-2 shrink-0 rounded p-1 text-text-muted hover:bg-surface-hover"
              aria-label="Detailansicht schließen"
            >
              <span class="material-symbols-rounded text-[20px]">close</span>
            </button>
          {/if}
        </div>
        <div class="flex-1 overflow-y-auto p-4">
          {@render supportingPane()}
        </div>
      </div>
    {/if}
  </div>

{:else}
  <!-- Mobile: one panel at a time -->
  {#if supportingPaneOpen && supportingPane}
    <div class="flex h-full flex-col">
      <div class="flex h-12 shrink-0 items-center gap-2 border-b border-border-subtle px-4">
        <button
          onclick={() => (supportingPaneOpen = false)}
          class="rounded p-1 text-text-muted hover:bg-surface-hover"
          aria-label="Zurück"
        >
          <span class="material-symbols-rounded text-[20px]">arrow_back</span>
        </button>
        {#if supportingPaneTitle}
          <span class="text-sm font-semibold text-text-secondary truncate">{supportingPaneTitle}</span>
        {/if}
      </div>
      <div class="flex-1 overflow-y-auto p-4">
        {@render supportingPane()}
      </div>
    </div>
  {:else}
    <div class="flex h-full flex-col">
      <div class="flex-1 overflow-y-auto p-4">
        {@render children()}
      </div>
    </div>
  {/if}
{/if}
