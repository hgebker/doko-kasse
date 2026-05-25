<script lang="ts">
  import { page } from '$app/state';
  import NavigationRail from '$lib/components/layout/NavigationRail.svelte';
  import NavigationRailItem from '$lib/components/layout/NavigationRailItem.svelte';
  import ChartBarIcon from 'phosphor-svelte/lib/ChartBarIcon';
  import DiceFiveIcon from 'phosphor-svelte/lib/DiceFiveIcon';
  import GraduationCapIcon from 'phosphor-svelte/lib/GraduationCapIcon';
  import HouseIcon from 'phosphor-svelte/lib/HouseIcon';
  import MoneyIcon from 'phosphor-svelte/lib/MoneyIcon';
  import ReceiptIcon from 'phosphor-svelte/lib/ReceiptIcon';
  import '../app.css';

  let { children } = $props();

  const navItems = [
    { href: '/', label: 'Übersicht', icon: HouseIcon },
    { href: '/evenings', label: 'Spieleinnahmen', icon: DiceFiveIcon },
    { href: '/earnings', label: 'Einnahmen', icon: MoneyIcon },
    { href: '/expenses', label: 'Ausgaben', icon: ReceiptIcon },
    { href: '/reports', label: 'Auswertungen', icon: ChartBarIcon },
    { href: '/semesters', label: 'Semester', icon: GraduationCapIcon }
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
