import '@excalidraw/excalidraw/index.css';
import '../page.css';
import { useCallback, useEffect, useRef, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Excalidraw,
  hashElementsVersion,
  MainMenu,
  restore,
  serializeAsJSON,
  WelcomeScreen,
} from '@excalidraw/excalidraw';
import type { ExcalidrawImperativeAPI } from '@excalidraw/excalidraw/types';
import type { EditorMessage } from '../types';
import { SaveTracker } from './saveTracker';

declare global {
  interface Window {
    /** Called by the plugin with the diagram file's text and whether the IDE theme is dark. */
    designLoad?: (scene: string, dark: boolean) => void;
    /** Called by the plugin to save pending changes now; replies with a sequenced `flushed` message. */
    designFlush?: (seq: number) => void;
    /** Called by the plugin after a save with its sequence number; an empty message means it succeeded. */
    designSaveResult?: (seq: number, error: string) => void;
    /** Called by the plugin when the IDE theme changes. */
    designTheme?: (dark: boolean) => void;
    /** Injected by the plugin after load, followed by a `designbridge` window event; forwards a JSON message to Java. */
    designSend?: (message: string) => void;
  }
}

const SAVE_DELAY_MS = 300;

type Loaded = { key: number; data: ReturnType<typeof restore> };

function post(message: EditorMessage): void {
  window.designSend?.(JSON.stringify(message));
}

/** What a save depends on: element versions and attached files. Pan, zoom, and selection do not change it. */
function signature(api: ExcalidrawImperativeAPI): string {
  const files = Object.keys(api.getFiles()).sort().join(',');
  return `${hashElementsVersion(api.getSceneElementsIncludingDeleted())}|${files}`;
}

/** The saved form: elements, files, and exportable settings only, so the view position never changes it. */
function serialize(api: ExcalidrawImperativeAPI): string {
  return serializeAsJSON(api.getSceneElementsIncludingDeleted(), api.getAppState(), api.getFiles(), 'local');
}

function content(scene: string): string {
  const parsed = JSON.parse(scene) as { elements?: unknown; files?: unknown };
  return JSON.stringify([parsed.elements ?? [], parsed.files ?? {}]);
}

function Editor() {
  const [loaded, setLoaded] = useState<Loaded | null>(null);
  const [dark, setDark] = useState(false);
  const [error, setError] = useState('');
  const api = useRef<ExcalidrawImperativeAPI | null>(null);
  const tracker = useRef(new SaveTracker());
  const timer = useRef<number | undefined>(undefined);

  const save = useCallback(() => {
    window.clearTimeout(timer.current);
    timer.current = undefined;
    const excalidraw = api.current;
    if (!excalidraw) {
      return;
    }
    const bytes = serialize(excalidraw);
    const current = signature(excalidraw);
    const command = tracker.current.prepareSave(bytes, current);
    if (!command) {
      return;
    }
    post({ type: 'save', seq: command.seq, scene: command.bytes });
  }, []);

  useEffect(() => {
    window.designLoad = (scene, isDark) => {
      let parsed: unknown = null;
      let loadError = '';
      try {
        parsed = JSON.parse(scene);
      } catch {
        loadError = 'The diagram file is not valid JSON. Drawing here will replace it.';
      }
      const data = restore(parsed as Parameters<typeof restore>[0], null, null);
      const normalized = content(serializeAsJSON(data.elements, data.appState, data.files, 'local'));
      const next = tracker.current.reset(scene, normalized, loadError);
      setError(next.error);
      setDark(isDark);
      setLoaded((previous) => ({ key: (previous?.key ?? 0) + 1, data }));
    };
    window.designFlush = (seq) => {
      save();
      post({ type: 'flushed', seq });
    };
    window.designSaveResult = (seq, message) => {
      const next = tracker.current.handleResult(seq, message);
      setError(next.error);
    };
    window.designTheme = setDark;
    // The plugin injects designSend after the page loads, which is usually after this effect runs.
    const announce = () => post({ type: 'ready' });
    if (window.designSend) {
      announce();
    } else {
      window.addEventListener('designbridge', announce, { once: true });
    }
    return () => window.removeEventListener('designbridge', announce);
  }, [save]);

  const onApi = useCallback((excalidraw: ExcalidrawImperativeAPI) => {
    api.current = excalidraw;
  }, []);

  const onChange = useCallback(() => {
    const excalidraw = api.current;
    if (!excalidraw) {
      return;
    }
    const current = signature(excalidraw);
    if (!tracker.current.recordChange(current, () => content(serialize(excalidraw)))) {
      return;
    }
    window.clearTimeout(timer.current);
    timer.current = window.setTimeout(save, SAVE_DELAY_MS);
  }, [save]);

  if (!loaded) {
    return null;
  }
  return (
    <div className="editor">
      {error !== '' && (
        <div className="save-error" role="alert">
          {error}
        </div>
      )}
      <div className="canvas">
        <Excalidraw
          key={loaded.key}
          initialData={{ ...loaded.data, scrollToContent: true }}
          excalidrawAPI={onApi}
          onChange={onChange}
          theme={dark ? 'dark' : 'light'}
          UIOptions={{
            canvasActions: {
              loadScene: false,
              export: false,
              saveToActiveFile: false,
              saveAsImage: false,
              toggleTheme: null,
            },
          }}
        >
          <MainMenu>
            <MainMenu.DefaultItems.ClearCanvas />
            <MainMenu.DefaultItems.ChangeCanvasBackground />
          </MainMenu>
          <WelcomeScreen>
            <WelcomeScreen.Hints.ToolbarHint />
            <WelcomeScreen.Center>
              <WelcomeScreen.Center.Heading>
                Sketch your components and connect them with arrows. Changes save automatically.
              </WelcomeScreen.Center.Heading>
            </WelcomeScreen.Center>
          </WelcomeScreen>
        </Excalidraw>
      </div>
    </div>
  );
}

createRoot(document.getElementById('root')!).render(<Editor />);