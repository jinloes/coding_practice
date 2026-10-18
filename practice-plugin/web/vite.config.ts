import { defineConfig, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';

/**
 * Inlines the entry script and stylesheet into index.html. The plugin loads the page with
 * JBCefBrowser.loadHTML, which has no base URL to fetch sibling assets from, so the page must be
 * one self-contained file.
 */
function inlineBundle(): Plugin {
  const pattern = (name: string) => name.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return {
    name: 'practice-inline-bundle',
    apply: 'build',
    enforce: 'post',
    generateBundle(_options, bundle) {
      const page = bundle['index.html'];
      if (!page || page.type !== 'asset') {
        throw new Error('index.html was not emitted');
      }
      let html = String(page.source);
      for (const [name, output] of Object.entries(bundle)) {
        if (output.type === 'chunk') {
          const code = output.code.replaceAll('</script', '<\\/script');
          html = html.replace(
            new RegExp(`<script[^>]*src="[^"]*${pattern(name)}"[^>]*></script>`),
            () => `<script type="module">${code}</script>`,
          );
          delete bundle[name];
        } else if (name.endsWith('.css')) {
          const css = String(output.source).replaceAll('</style', '<\\/style');
          html = html.replace(new RegExp(`<link[^>]*href="[^"]*${pattern(name)}"[^>]*>`), () => `<style>${css}</style>`);
          delete bundle[name];
        }
      }
      const leftovers = Object.keys(bundle).filter((name) => name !== 'index.html');
      if (leftovers.length > 0 || /<script[^>]*src=|<link[^>]*stylesheet/.test(html)) {
        throw new Error(`Assets were not inlined: ${leftovers.join(', ')}`);
      }
      page.source = html;
    },
  };
}

export default defineConfig({
  base: './',
  plugins: [react(), inlineBundle()],
  build: {
    outDir: '../build/web',
    emptyOutDir: true,
    cssCodeSplit: false,
    modulePreload: false,
    assetsInlineLimit: Number.MAX_SAFE_INTEGER,
    chunkSizeWarningLimit: 2048,
  },
});