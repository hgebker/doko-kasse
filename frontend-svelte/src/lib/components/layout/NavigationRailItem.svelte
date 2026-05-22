<script lang="ts">
  import type { Component } from 'svelte';

  type Props = {
    icon: Component;
    href: string;
    label?: string;
    ariaLabel?: string;
    selected?: boolean;
  };
  let { icon: Icon, href, label = '', ariaLabel = '', selected = false }: Props = $props();
</script>

<a
  {href}
  aria-label={ariaLabel || label}
  aria-current={selected ? 'page' : undefined}
  class="group flex w-full flex-col items-center gap-1 py-1"
>
  <div
    class={[
      'flex h-8 w-14 items-center justify-center rounded-full transition-colors',
      selected ? 'bg-surface-raised' : 'group-hover:bg-surface-hover'
    ].join(' ')}
  >
    <Icon
      size="24"
      class={[
        selected ? 'text-action-primary' : 'text-text-muted group-hover:text-text-secondary'
      ].join(' ')}
      color={selected ? 'currentColor' : undefined}
      weight={selected ? 'fill' : 'regular'}
    />
  </div>
  {#if label}
    <span
      class={[
        'text-[10px] leading-tight transition-colors',
        selected
          ? 'font-medium text-action-primary'
          : 'text-text-muted group-hover:text-text-secondary'
      ].join(' ')}
    >
      {label}
    </span>
  {/if}
</a>
