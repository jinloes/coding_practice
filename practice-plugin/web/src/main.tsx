import '@mantine/core/styles.css';
import './page.css';
import './bridge';
import { createRoot } from 'react-dom/client';
import { App } from './App';

createRoot(document.getElementById('root')!).render(<App />);

if (import.meta.env.DEV) {
  void import('./sample').then((sample) => sample.renderSample());
}
