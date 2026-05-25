<script lang="ts">
  import { AlertDialog } from 'bits-ui';

  type Props = {
    open?: boolean;
    title: string;
    description: string;
    onConfirm: () => void;
    onCancel?: () => void;
  };
  let { open = $bindable(), title, description, onConfirm, onCancel }: Props = $props();
</script>

<AlertDialog.Root
  bind:open
  onOpenChange={(o) => {
    if (!o) onCancel?.();
  }}
>
  <AlertDialog.Portal>
    <AlertDialog.Overlay class="fixed inset-0 z-40 bg-black/60" />
    <AlertDialog.Content
      class="fixed left-1/2 top-1/2 z-50 w-full max-w-sm -translate-x-1/2 -translate-y-1/2 rounded-xl bg-surface-base p-6 shadow-xl"
    >
      <AlertDialog.Title class="mb-2 text-lg font-semibold text-text-primary">
        {title}
      </AlertDialog.Title>
      <AlertDialog.Description class="mb-6 text-sm text-text-secondary">
        {description}
      </AlertDialog.Description>
      <div class="flex justify-end gap-3">
        <AlertDialog.Cancel
          class="rounded-lg border border-border-strong px-4 py-2 text-sm font-medium text-text-secondary hover:bg-surface-hover"
        >
          Abbrechen
        </AlertDialog.Cancel>
        <AlertDialog.Action
          onclick={onConfirm}
          class="rounded-lg bg-destruct-bg px-4 py-2 text-sm font-medium text-destruct-text hover:opacity-90"
        >
          Löschen
        </AlertDialog.Action>
      </div>
    </AlertDialog.Content>
  </AlertDialog.Portal>
</AlertDialog.Root>
