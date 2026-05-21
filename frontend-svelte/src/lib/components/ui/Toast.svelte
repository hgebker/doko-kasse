<script lang="ts">
  type Toast = {
    message: string;
    type: 'success' | 'error';
  };
  type Props = {
    toast: Toast | null;
  };
  let { toast = $bindable() }: Props = $props();

  $effect(() => {
    if (toast) {
      const timer = setTimeout(() => {
        toast = null;
      }, 5000);
      return () => clearTimeout(timer);
    }
  });
</script>

{#if toast}
  <div
    class="fixed bottom-4 right-4 z-50 flex max-w-sm items-start gap-3 rounded-lg border p-4 shadow-lg"
    class:bg-toast-success-bg={toast.type === 'success'}
    class:border-toast-success-border={toast.type === 'success'}
    class:text-toast-success-text={toast.type === 'success'}
    class:bg-toast-error-bg={toast.type === 'error'}
    class:border-toast-error-border={toast.type === 'error'}
    class:text-toast-error-text={toast.type === 'error'}
    role="alert"
  >
    <span class="text-sm font-medium">{toast.message}</span>
    <button
      onclick={() => (toast = null)}
      class="ml-auto shrink-0 rounded p-0.5 opacity-60 hover:opacity-100"
      aria-label="Schließen"
    >
      <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M6 18L18 6M6 6l12 12"
        />
      </svg>
    </button>
  </div>
{/if}
