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

declare global {
  interface Window {
    /** Called by the plugin with the diagram file's text and whether the IDE theme is dark. */
    designLoad?: (scene: string, dark: boolean) => void;
    /** Called by the plugin to save pending changes now; replies with a `flushed` message. */
    designFlush?: () => void;
    /** Called by the plugin after a save; an empty message means it succeeded. */
    designSaveResult?: (error: string) => void;
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
  // Content of the scene as loaded; the first change event that still matches it only records the baseline.
  const loadedContent = useRef('');
  const baseline = useRef<string | null>(null);
  const dirty = useRef(false);
  const savedBytes = useRef('');
  const pending = useRef<{ bytes: string; signature: string } | null>(null);
  const timer = useRef<number | undefined>(undefined);

  const save = useCallback(() => {
    window.clearTimeout(timer.current);
    timer.current = undefined;
    const excalidraw = api.current;
    if (!excalidraw || !dirty.current) {
      return;
    }
    const bytes = serialize(excalidraw);
    const current = signature(excalidraw);
    if (bytes === savedBytes.current) {
      dirty.current = false;
      return;
    }
    pending.current = { bytes, signature: current };
    post({ type: 'save', scene: bytes });
  }, []);

  useEffect(() => {
    window.designLoad = (scene, isDark) => {
      let parsed: unknown = null;
      try {
        parsed = JSON.parse(scene);
        setError('');
      } catch {
        setError('The diagram file is not valid JSON. Drawing here will replace it.');
      }
      const data = restore(parsed as Parameters<typeof restore>[0], null, null);
      loadedContent.current = content(serializeAsJSON(data.elements, data.appState, data.files, 'local'));
      baseline.current = null;
      dirty.current = false;
      savedBytes.current = scene;
      setDark(isDark);
      setLoaded((previous) => ({ key: (previous?.key ?? 0) + 1, data }));
    };
    window.designFlush = () => {
      save();
      post({ type: 'flushed' });
    };
    window.designSaveResult = (message) => {
      const saved = pending.current;
      pending.current = null;
      if (message !== '' || !saved) {
        setError(message);
        return;
      }
      savedBytes.current = saved.bytes;
      if (baseline.current === saved.signature) {
        dirty.current = false;
      }
      setError('');
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
    if (baseline.current === null && content(serialize(excalidraw)) === loadedContent.current) {
      baseline.current = current;
      return;
    }
    if (current === baseline.current) {
      return;
    }
    baseline.current = current;
    dirty.current = true;
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