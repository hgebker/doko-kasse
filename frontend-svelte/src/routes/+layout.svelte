<script lang="ts">
  import { page } from '$app/state';
  import NavigationRail from '$lib/components/layout/NavigationRail.svelte';
  import NavigationRailItem from '$lib/components/layout/NavigationRailItem.svelte';
  import '../app.css';

  let { children } = $props();

  const navItems = [
    { href: '/', label: 'Übersicht', icon: 'home' },
    { href: '/evenings', label: 'Spieleinnahmen', icon: 'casino' },
    { href: '/earnings', label: 'Einnahmen', icon: 'payments' },
    { href: '/expenses', label: 'Ausgaben', icon: 'receipt_long' },
    { href: '/reports', label: 'Auswertungen', icon: 'bar_chart' }
  ];

  function isActive(href: string) {
    if (href === '/') return page.url.pathname === '/';
    return page.url.pathname.startsWith(href);
  }
</script>

<NavigationRail>
  {#snippet navigationRail()}
    {#each navItems as item}
      <NavigationRailItem
        href={item.href}
        icon={item.icon}
        label={item.label}
        ariaLabel={item.label}
        selected={isActive(item.href)}
      />
    {/each}
  {/snippet}

  {@render children()}
</NavigationRail>
