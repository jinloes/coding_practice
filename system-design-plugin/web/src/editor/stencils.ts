import { convertToExcalidrawElements } from '@excalidraw/excalidraw';
import type { LibraryItems } from '@excalidraw/excalidraw/types';

type StencilSpec = {
  id: string;
  name: string;
  type: 'rectangle' | 'ellipse' | 'diamond';
  strokeColor: string;
  backgroundColor: string;
  roundness: { type: 2 | 3 } | null;
  width?: number;
  height?: number;
};

const SPECS: readonly StencilSpec[] = [
  {
    id: 'stencil-client',
    name: 'Client / App',
    type: 'rectangle',
    strokeColor: '#1971c2',
    backgroundColor: '#a5d8ff33',
    roundness: { type: 3 },
  },
  {
    id: 'stencil-cdn',
    name: 'CDN / Edge',
    type: 'ellipse',
    strokeColor: '#0ca678',
    backgroundColor: '#96f2d733',
    roundness: { type: 2 },
  },
  {
    id: 'stencil-gateway',
    name: 'API Gateway / LB',
    type: 'diamond',
    strokeColor: '#e67700',
    backgroundColor: '#ffec9933',
    roundness: { type: 2 },
    width: 190,
    height: 92,
  },
  {
    id: 'stencil-service',
    name: 'Stateless Service',
    type: 'rectangle',
    strokeColor: '#1971c2',
    backgroundColor: '#d0ebff33',
    roundness: { type: 3 },
  },
  {
    id: 'stencil-cache',
    name: 'In-Memory Cache',
    type: 'rectangle',
    strokeColor: '#e03131',
    backgroundColor: '#ffc9c933',
    roundness: { type: 3 },
  },
  {
    id: 'stencil-sql-db',
    name: 'Primary / Sharded DB',
    type: 'rectangle',
    strokeColor: '#2f9e44',
    backgroundColor: '#b2f2bb33',
    roundness: null,
  },
  {
    id: 'stencil-kv-store',
    name: 'Wide-Column / KV Store',
    type: 'rectangle',
    strokeColor: '#099268',
    backgroundColor: '#c3fae833',
    roundness: null,
  },
  {
    id: 'stencil-queue',
    name: 'Message Queue / Log',
    type: 'rectangle',
    strokeColor: '#7048e8',
    backgroundColor: '#d0bfff33',
    roundness: { type: 3 },
    width: 190,
    height: 60,
  },
  {
    id: 'stencil-workers',
    name: 'Worker Pool',
    type: 'rectangle',
    strokeColor: '#5f3dc4',
    backgroundColor: '#e5dbff33',
    roundness: { type: 3 },
  },
  {
    id: 'stencil-blob-store',
    name: 'Object Storage',
    type: 'ellipse',
    strokeColor: '#f08c00',
    backgroundColor: '#ffe06633',
    roundness: { type: 2 },
  },
];

let cached: LibraryItems | null = null;

/** Offline system-design stencils preloaded into Excalidraw's local library drawer. */
export function systemDesignStencils(): LibraryItems {
  if (cached) {
    return cached;
  }
  cached = SPECS.map((spec, index) => ({
    id: spec.id,
    name: spec.name,
    status: 'published',
    created: 1_700_000_000_000 + index,
    elements: convertToExcalidrawElements([
      {
        type: spec.type,
        x: 0,
        y: 0,
        width: spec.width ?? 168,
        height: spec.height ?? 68,
        strokeColor: spec.strokeColor,
        backgroundColor: spec.backgroundColor,
        fillStyle: 'solid',
        roundness: spec.roundness,
        label: {
          text: spec.name,
          fontSize: 16,
        },
      },
    ]),
  }));
  return cached;
}
