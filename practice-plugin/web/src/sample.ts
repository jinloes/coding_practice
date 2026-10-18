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
      topic: 'Arrays',
      difficulty: 'Easy',
      passed: true,
      statementHtml: '<p>Return the indexes of two values in <code>nums</code> that add up to <code>target</code>.</p>',
      examples: [{ input: 'nums = [2, 7, 11, 15], target = 9', output: '[0, 1]' }],
      inputSyntax: '<nums> | <target>',
      intendedTime: 'O(n)',
      intendedSpace: 'O(n)',
      guides: [{ id: 'hashing', title: 'Hashing' }],
      tracks: [{ id: 'blind-75', title: 'Blind 75' }],
    },
  },
};

const guide: PageState = {
  theme: problem.theme,
  page: {
    kind: 'guide',
    guide: {
      id: 'hashing',
      title: 'Hashing',
      kind: 'guide',
      summaryHtml: 'Trade memory for constant-time lookups.',
      lessonHtml: '<p>How it works</p><ul><li>Store what you have seen.</li></ul>',
      levels: [
        { level: 'Easy', exercises: [{ id: 'pair-sum', title: 'Pair Sum', topic: 'Arrays', passed: true, selected: true }] },
        { level: 'Medium', exercises: [] },
        { level: 'Hard', exercises: [] },
      ],
    },
  },
};

export function renderSample(): void {
  render(window.location.hash === '#guide' ? guide : problem);
}
