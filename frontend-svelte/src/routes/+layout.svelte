<script>
  import '../app.css';
  import { page } from '$app/stores';
  import { MediaQuery } from 'svelte/reactivity';

  let { children } = $props();

  const isMobile = new MediaQuery('(max-width: 640px)');
  let drawerOpen = $state(false);

  const navItems = [
    { href: '/', label: 'Übersicht' },
    { href: '/evenings', label: 'Spieleinnahmen' },
    { href: '/earnings', label: 'Sonstige Einnahmen' },
    { href: '/expenses', label: 'Ausgaben' },
    { href: '/reports', label: 'Auswertungen' }
  ];

  function isActive(href) {
    if (href === '/') return $page.url.pathname === '/';
    return $page.url.pathname.startsWith(href);
  }
</script>

<div class="min-h-screen bg-surface-page">
  <!-- Top navigation bar -->
  <nav class="sticky top-0 z-40 bg-gradient-to-r from-nav-from to-nav-to shadow-md">
    <div class="flex h-14 items-center px-4">
      {#if isMobile.current}
        <button
          onclick={() => (drawerOpen = true)}
          class="mr-3 rounded p-1.5 text-white hover:bg-white/10"
          aria-label="Navigation öffnen"
        >
          <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 6h16M4 12h16M4 18h16"
            />
          </svg>
        </button>
        <span class="text-lg font-semibold text-white">Doko Kartclub Münster e.V.</span>
      {:else}
        <span class="mr-6 text-lg font-semibold text-white">Doko Kartclub Münster e.V.</span>
        <div class="flex gap-1">
          {#each navItems as item}
            <a
              href={item.href}
              class={`rounded px-3 py-1.5 text-sm font-medium transition-colors ${isActive(item.href) ? 'bg-surface-raised text-text-primary' : 'text-white hover:bg-white/10'}`}
            >
              {item.label}
            </a>
          {/each}
        </div>
      {/if}
    </div>
  </nav>

  <!-- Mobile drawer overlay -->
  {#if drawerOpen}
    <div class="fixed inset-0 z-50 flex" role="dialog" aria-modal="true">
      <button
        class="absolute inset-0 bg-black/60"
        onclick={() => (drawerOpen = false)}
        aria-label="Navigation schließen"
      ></button>
      <nav
        class="relative z-10 flex w-64 flex-col border-r border-border-subtle bg-surface-base shadow-xl"
      >
        <div class="flex items-center justify-between border-b border-border-subtle px-4 py-3">
          <span class="font-semibold text-text-secondary">Navigation</span>
          <button
            onclick={() => (drawerOpen = false)}
            class="rounded p-1 text-text-muted hover:bg-surface-hover"
            aria-label="Schließen"
          >
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
        <ul class="flex flex-col p-2">
          {#each navItems as item}
            <li>
              <a
                href={item.href}
                onclick={() => (drawerOpen = false)}
                class="flex rounded-md px-3 py-2 text-sm font-medium transition-colors"
                class:bg-surface-raised={isActive(item.href)}
                class:text-text-primary={isActive(item.href)}
                class:text-text-secondary={!isActive(item.href)}
                class:hover:bg-surface-hover={!isActive(item.href)}
              >
                {item.label}
              </a>
            </li>
          {/each}
        </ul>
      </nav>
    </div>
  {/if}

  <!-- Main content -->
  <main class="p-4">
    {@render children()}
  </main>
</div>
