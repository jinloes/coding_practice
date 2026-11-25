// Development-only data for `npm run dev`; the plugin build never includes it.
import { render } from './bridge';
import type { PageState } from './types';

const dark = window.matchMedia('(prefers-color-scheme: dark)').matches;

const problem: PageState = {
  theme: {
    dark,
    background: dark ? '#2b2d30' : '#f7f8fa',
    foreground: dark ? '#dfe1e5' : '#000000',
    secondary: dark ? '#8c8f94' : '#6c707e',
    border: dark ? '#393b40' : '#ebecf0',
    accent: dark ? '#548af7' : '#2e55a3',
    fontFamily: 'Inter, Segoe UI, sans-serif',
    codeFontFamily: 'JetBrains Mono, Consolas, monospace',
    fontSize: 13,
  },
  page: {
    kind: 'problem',
    problem: {
      id: 'pair-sum',
      title: 'Pair Sum',
      topics: ['Arrays', 'Hash Maps'],
      difficulty: 'Easy',
      passed: true,
      statement: 'Return the indexes of two values in `nums` that add up to `target`.',
      examples: [{ input: 'nums = [2, 7, 11, 15], target = 9', output: '[0, 1]' }],
      inputSyntax: '<nums> | <target>',
      sampleInput: '[2, 7, 11, 15] 9',
      intendedTime: 'O(n)',
      intendedSpace: 'O(n)',
      hasAttempt: true,
      canRunExamples: true,
      guides: [{ id: 'hash-maps', title: 'Hash Maps and Sets' }],
      tracks: [{ id: 'blind-75', title: 'Blind 75' }],
    },
  },
};

const guide: PageState = {
  theme: problem.theme,
  page: {
    kind: 'guide',
    guide: {
      id: 'hash-maps',
      title: 'Hash Maps and Sets',
      kind: 'guide',
      summary: 'Trade memory for constant-time lookups.',
      lesson: 'How it works\n\n- Store what you have seen.',
      levels: [
        { level: 'Easy', exercises: [{ id: 'pair-sum', title: 'Pair Sum', topics: ['Arrays', 'Hash Maps'], passed: true, selected: true }] },
        { level: 'Medium', exercises: [] },
        { level: 'Hard', exercises: [] },
      ],
    },
  },
};

const chooser: PageState = {
  theme: problem.theme,
  page: {
    kind: 'chooser',
    selected: {
      title: 'Pair Sum',
      guides: [{ id: 'hash-maps', title: 'Hash Maps and Sets' }],
      tracks: [{ id: 'blind-75', title: 'Blind 75' }],
    },
  },
};

let listening = false;

export function renderSample(): void {
  if (!listening) {
    window.addEventListener('hashchange', renderSample);
    listening = true;
  }
  switch (window.location.hash) {
    case '#guide':
      render(guide);
      break;
    case '#chooser':
      render(chooser);
      break;
    default:
      render(problem);
      break;
  }
}
