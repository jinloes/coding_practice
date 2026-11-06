// Copies Excalidraw's bundled fonts next to the built pages so the editor never falls back to a CDN.
import { cpSync, existsSync, readdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = dirname(dirname(fileURLToPath(import.meta.url)));
const source = join(root, 'node_modules', '@excalidraw', 'excalidraw', 'dist', 'prod', 'fonts');
const target = join(root, '..', 'build', 'web', 'fonts');

if (!existsSync(source) || readdirSync(source).length === 0) {
  throw new Error(`Excalidraw fonts not found at ${source}; run npm install.`);
}
cpSync(source, target, { recursive: true });
console.log(`Copied Excalidraw fonts to ${target}`);