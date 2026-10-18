import type { PageEvent, PageState } from './types';

declare global {
  interface Window {
    /** Called by the plugin with the page to show. */
    practiceRender?: (state: PageState) => void;
    /** Injected by the plugin after load; forwards a JSON event to Java. */
    practiceSend?: (message: string) => void;
  }
}

let current: PageState | null = null;
const listeners = new Set<() => void>();

export function subscribe(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export function snapshot(): PageState | null {
  return current;
}

export function render(state: PageState): void {
  current = state;
  listeners.forEach((listener) => listener());
}

export function send(event: PageEvent): void {
  window.practiceSend?.(JSON.stringify(event));
}

window.practiceRender = render;
