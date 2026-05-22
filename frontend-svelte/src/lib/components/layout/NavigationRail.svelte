<script lang="ts">
  import dokoLogo from '$lib/images/doko-icons/playing_512.png';
  import type { Snippet } from 'svelte';
  import { MediaQuery } from 'svelte/reactivity';

  type Props = {
    children: Snippet;
    navigationRail: Snippet;
  };
  let { children, navigationRail }: Props = $props();

  const isDesktop = new MediaQuery('(min-width: 840px)');
</script>

<div class="flex h-screen bg-surface-page">
  {#if isDesktop.current}
    <nav
      class="flex w-25 shrink-0 flex-col items-center border-r border-border-subtle bg-surface-base py-4 gap-1"
      aria-label="Hauptnavigation"
    >
      <div class="mb-4 flex h-10 w-10 items-center justify-center rounded-xl shrink-0">
        <img src="/favicon.svg" alt="Logo" />
      </div>
      {@render navigationRail()}
    </nav>

    <main class="flex-1 overflow-y-auto">
      {@render children()}
    </main>
  {:else}
    <div class="flex flex-1 flex-col overflow-hidden">
      <main class="flex-1 overflow-y-auto pb-16">
        {@render children()}
      </main>
    </div>

    <nav
      class="fixed bottom-0 left-0 right-0 z-40 flex h-16 items-center justify-around border-t border-border-default bg-surface-base"
      aria-label="Hauptnavigation"
    >
      {@render navigationRail()}
    </nav>
  {/if}
</div>
