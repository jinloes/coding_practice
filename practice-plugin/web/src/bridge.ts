import type { PageEvent, PageState } from './types';

declare global {
  interface Window {
    /** Called by the plugin with the page to show. */
    practiceRender?: (state: PageState) => void;
    /** Injected by the plugin after load; forwards a JSON event to Java. */
    practiceSend?: (message: string) => void;
    /** Attaches the Java event sink and flushes any events queued before load finished. */
    practiceAttachSend?: (sender: (message: string) => void) => void;
  }
}

let current: PageState | null = null;
const listeners = new Set<() => void>();
const pendingEvents: string[] = [];

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

export function attachSend(sender: (message: string) => void): void {
  window.practiceSend = sender;
  while (pendingEvents.length > 0) {
    sender(pendingEvents.shift()!);
  }
}

export function send(event: PageEvent): void {
  const payload = JSON.stringify(event);
  if (window.practiceSend) {
    window.practiceSend(payload);
  } else {
    pendingEvents.push(payload);
  }
}

export function resetBridgeForTest(): void {
  current = null;
  pendingEvents.length = 0;
  delete window.practiceSend;
  listeners.forEach((listener) => listener());
}

window.practiceRender = render;
window.practiceAttachSend = attachSend;

