export type SaveCommand = { seq: number; bytes: string } | null;

export type SaveState = {
  dirty: boolean;
  error: string;
};

/**
 * Tracks in-flight Excalidraw scene saves by monotonic sequence number so overlapping debounced/flushed
 * saves and rejected payloads never clear a newer error banner or mark an unsaved scene clean.
 */
export class SaveTracker {
  private readonly inFlight = new Map<number, { bytes: string; signature: string }>();
  private baselineSignature: string | null = null;
  private loadedContent = '';
  private savedBytes = '';
  private dirty = false;
  private error = '';
  private latestSeq = 0;
  private lastSavedSeq = 0;
  private latestSettledSeq = 0;

  reset(initialBytes: string, normalizedLoadedContent: string, initialError: string): SaveState {
    this.inFlight.clear();
    this.baselineSignature = null;
    this.loadedContent = normalizedLoadedContent;
    this.savedBytes = initialBytes;
    this.dirty = false;
    this.error = initialError;
    this.latestSeq = 0;
    this.lastSavedSeq = 0;
    this.latestSettledSeq = 0;
    return this.state();
  }

  recordChange(currentSignature: string, currentNormalizedContent: () => string): boolean {
    if (this.baselineSignature === null && currentNormalizedContent() === this.loadedContent) {
      this.baselineSignature = currentSignature;
      return false;
    }
    if (currentSignature === this.baselineSignature) {
      return false;
    }
    this.baselineSignature = currentSignature;
    this.dirty = true;
    return true;
  }

  prepareSave(bytes: string, signature: string): SaveCommand {
    if (!this.dirty) {
      return null;
    }
    if (this.inFlight.size === 0 && bytes === this.savedBytes && this.error === '') {
      this.dirty = false;
      return null;
    }
    const seq = ++this.latestSeq;
    this.inFlight.set(seq, { bytes, signature });
    return { seq, bytes };
  }

  handleResult(seq: number, message: string): SaveState {
    if (message !== '') {
      if (seq < 0 || seq >= this.latestSettledSeq) {
        const targetSeq = seq < 0 ? this.latestSeq : seq;
        this.latestSettledSeq = targetSeq;
        if (seq < 0) {
          this.inFlight.clear();
        } else {
          this.inFlight.delete(seq);
        }
        this.error = message;
      }
      return this.state();
    }
    const saved = this.inFlight.get(seq);
    this.pruneUpTo(seq);
    if (seq > this.latestSettledSeq) {
      this.latestSettledSeq = seq;
    }
    if (saved && seq > this.lastSavedSeq) {
      this.lastSavedSeq = seq;
      this.savedBytes = saved.bytes;
    }
    if (seq === this.latestSeq) {
      this.error = '';
      if (saved && this.baselineSignature === saved.signature) {
        this.dirty = false;
      }
    }
    return this.state();
  }

  private pruneUpTo(maxSeq: number): void {
    for (const key of this.inFlight.keys()) {
      if (key <= maxSeq) {
        this.inFlight.delete(key);
      }
    }
  }

  private state(): SaveState {
    return { dirty: this.dirty, error: this.error };
  }
}
