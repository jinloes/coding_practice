import { describe, expect, it } from 'vitest';
import { SaveTracker } from './saveTracker';

describe('SaveTracker', () => {
  it('ignores initial render when normalized content matches loaded content', () => {
    const tracker = new SaveTracker();
    tracker.reset('{"type":"excalidraw","elements":[]}', '[[],{}]', '');

    expect(tracker.recordChange('sig-1', () => '[[],{}]')).toBe(false);
    expect(tracker.prepareSave('{"type":"excalidraw","elements":[]}', 'sig-1')).toBeNull();
  });

  it('tracks sequenced saves, ignores stale errors, and clears dirty only on latest signature', () => {
    const tracker = new SaveTracker();
    tracker.reset('raw-0', 'norm-0', '');

    expect(tracker.recordChange('sig-1', () => 'norm-1')).toBe(true);
    const first = tracker.prepareSave('bytes-1', 'sig-1');
    expect(first).toEqual({ seq: 1, bytes: 'bytes-1' });

    expect(tracker.recordChange('sig-2', () => 'norm-2')).toBe(true);
    const second = tracker.prepareSave('bytes-2', 'sig-2');
    expect(second).toEqual({ seq: 2, bytes: 'bytes-2' });

    // Completing seq 1 while seq 2 is still in flight leaves the scene dirty.
    expect(tracker.handleResult(1, '')).toEqual({ dirty: true, error: '' });
    // Completing seq 2 settles the latest signature cleanly.
    expect(tracker.handleResult(2, '')).toEqual({ dirty: false, error: '' });
    // A late failure from seq 1 never overwrites settled seq 2.
    expect(tracker.handleResult(1, 'stale error')).toEqual({ dirty: false, error: '' });
  });

  it('preserves dirty state on failed or unsequenced rejected saves until a newer save succeeds', () => {
    const tracker = new SaveTracker();
    tracker.reset('raw-0', 'norm-0', '');
    tracker.recordChange('sig-1', () => 'norm-1');
    tracker.prepareSave('bytes-1', 'sig-1');

    expect(tracker.handleResult(-1, 'Too large')).toEqual({ dirty: true, error: 'Too large' });
    // Even if bytes equal savedBytes, an active error banner forces a retry save.
    const retry = tracker.prepareSave('raw-0', 'sig-1');
    expect(retry).toEqual({ seq: 2, bytes: 'raw-0' });
    expect(tracker.handleResult(2, '')).toEqual({ dirty: false, error: '' });
  });
});
