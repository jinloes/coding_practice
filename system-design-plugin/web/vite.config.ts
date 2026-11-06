import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// The plugin serves build/web from https://system-design.invalid/ through a CEF request handler, so the
// pages may load sibling assets with relative URLs. Nothing is fetched from the network.
export default defineConfig({
  base: './',
  plugins: [react()],
  build: {
    outDir: '../build/web',
    emptyOutDir: true,
    chunkSizeWarningLimit: 4096,
    rollupOptions: {
      input: {
        index: 'index.html',
        editor: 'editor.html',
      },
    },
  },
});