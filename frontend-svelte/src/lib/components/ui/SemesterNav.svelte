<script lang="ts">
  import type { Semester } from '$lib/types';

  type Props = {
    semesters: Semester[];
    selected?: string;
  };
  let { semesters, selected = $bindable('gesamt') }: Props = $props();
</script>

<nav class="shrink-0">
  <p class="mb-1 px-2 text-xs font-semibold uppercase tracking-wide text-text-disabled">
    Allgemein
  </p>
  <ul class="mb-4">
    <li>
      <button
        onclick={() => (selected = 'gesamt')}
        class="w-full rounded px-3 py-1.5 text-left text-sm transition-colors"
        class:bg-surface-hover={selected === 'gesamt'}
        class:font-medium={selected === 'gesamt'}
        class:text-text-primary={selected === 'gesamt'}
        class:text-text-secondary={selected !== 'gesamt'}
        class:hover:bg-surface-raised={selected !== 'gesamt'}
      >
        Gesamt
      </button>
    </li>
  </ul>

  <p class="mb-1 px-2 text-xs font-semibold uppercase tracking-wide text-text-disabled">Semester</p>
  <ul>
    {#each semesters as sem}
      <li>
        <button
          onclick={() => (selected = sem.id)}
          class="w-full rounded px-3 py-1.5 text-left text-sm transition-colors"
          class:bg-surface-hover={selected === sem.id}
          class:font-medium={selected === sem.id}
          class:text-text-primary={selected === sem.id}
          class:text-text-secondary={selected !== sem.id}
          class:hover:bg-surface-raised={selected !== sem.id}
        >
          {sem.label}
        </button>
      </li>
    {/each}
  </ul>
</nav>
