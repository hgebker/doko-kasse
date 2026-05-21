<script lang="ts">
  import type { Snippet } from 'svelte';

  type Props = {
    label: string;
    children: Snippet;
  };
  let { label, children }: Props = $props();
</script>

<div class="flex flex-col gap-1">
  <!-- svelte-ignore a11y_label_has_associated_control -->
  <label class="text-sm font-medium text-text-secondary">{label}</label>
  {@render children()}
</div>
