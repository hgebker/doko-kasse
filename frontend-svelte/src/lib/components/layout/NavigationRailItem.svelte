<script lang="ts">
  type Props = {
    icon: string;
    href: string;
    label?: string;
    ariaLabel?: string;
    selected?: boolean;
  };
  let { icon, href, label = '', ariaLabel = '', selected = false }: Props = $props();
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
    <span
      class={['material-symbols-rounded text-[24px] transition-colors', selected ? 'text-action-primary' : 'text-text-muted group-hover:text-text-secondary'].join(' ')}
      style={selected ? "font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24" : ''}
    >
      {icon}
    </span>
  </div>
  {#if label}
    <span
      class={[
        'text-[10px] leading-tight transition-colors',
        selected ? 'font-medium text-action-primary' : 'text-text-muted group-hover:text-text-secondary'
      ].join(' ')}
    >
      {label}
    </span>
  {/if}
</a>
