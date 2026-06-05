<script lang="ts">
  import CaretLeftIcon from 'phosphor-svelte/lib/CaretLeftIcon';
  import CaretRightIcon from 'phosphor-svelte/lib/CaretRightIcon';
  import XIcon from 'phosphor-svelte/lib/XIcon';
  import type { Snippet } from 'svelte';
  import { MediaQuery } from 'svelte/reactivity';

  type Props = {
    children: Snippet;
    contextPane: Snippet;
    contextPaneTitle?: string;
    contextPaneCollapsed?: boolean;
    contextPaneModalOpen?: boolean;
  };
  let {
    children,
    contextPane,
    contextPaneTitle = '',
    contextPaneCollapsed = $bindable(false),
    contextPaneModalOpen = $bindable(false)
  }: Props = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');
</script>

{#if isDesktop.current}
  <div class="relative flex h-full flex-row">
    <!-- Pane -->
    <div
      class={[
        'flex flex-col border-r border-border-subtle bg-surface-base overflow-hidden transition-[width] duration-200 ease-in-out shrink-0',
        contextPaneCollapsed ? 'w-0' : 'w-64'
      ].join(' ')}
    >
      {#if contextPaneTitle}
        <div class="flex h-12 shrink-0 items-center px-4">
          <span class="text-l font-semibold text-text-secondary">{contextPaneTitle}</span>
        </div>
      {/if}
      <div class="flex-1 overflow-y-auto">
        {@render contextPane()}
      </div>
    </div>

    <!-- Collapse toggle button on the boundary -->
    <button
      onclick={() => (contextPaneCollapsed = !contextPaneCollapsed)}
      class="absolute top-1/2 z-10 flex h-8 w-5 -translate-y-1/2 items-center justify-center rounded-r border border-l-0 border-border-default bg-surface-raised text-text-muted transition-[left] duration-200 hover:bg-surface-hover hover:text-text-primary"
      style={contextPaneCollapsed ? 'left: 0' : 'left: 256px'}
      aria-label={contextPaneCollapsed ? 'Seitenleiste öffnen' : 'Seitenleiste schließen'}
    >
      {#if contextPaneCollapsed}
        <CaretRightIcon size="24" />
      {:else}
        <CaretLeftIcon size="24" />
      {/if}
    </button>

    <!-- Main content -->
    <div class="flex-1 overflow-y-auto">
      {@render children()}
    </div>
  </div>
{:else}
  <!-- Mobile: content only, pane as modal overlay -->
  <div>
    {@render children()}
  </div>

  {#if contextPaneModalOpen}
    <button
      class="fixed inset-0 z-50 bg-black/60"
      onclick={() => (contextPaneModalOpen = false)}
      aria-label="Schließen"
    ></button>
    <div
      class="fixed bottom-0 left-0 top-0 z-50 flex w-72 flex-col border-r border-border-subtle bg-surface-base shadow-xl"
      role="dialog"
      aria-modal="true"
      aria-label={contextPaneTitle || 'Filter'}
    >
      <div
        class="flex h-12 shrink-0 items-center justify-between border-b border-border-subtle px-4"
      >
        <span class="font-semibold text-text-secondary">{contextPaneTitle}</span>
        <button
          onclick={() => (contextPaneModalOpen = false)}
          class="rounded p-1 text-text-muted hover:bg-surface-hover"
          aria-label="Schließen"
        >
          <XIcon size="24" />
        </button>
      </div>
      <div class="flex-1 overflow-y-auto p-2">
        {@render contextPane()}
      </div>
    </div>
  {/if}
{/if}
