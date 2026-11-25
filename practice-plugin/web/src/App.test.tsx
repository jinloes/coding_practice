import { act, render as renderDom, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { App } from './App';
import { attachSend, render as pushState, resetBridgeForTest, send, snapshot } from './bridge';
import { renderSample } from './sample';
import type { PageState, Theme } from './types';

const THEME: Theme = {
  dark: true,
  background: '#2b2d30',
  foreground: '#dfe1e5',
  secondary: '#8c8f94',
  border: '#393b40',
  accent: '#548af7',
  fontFamily: 'Inter, sans-serif',
  codeFontFamily: 'JetBrains Mono, monospace',
  fontSize: 13,
};

function problemState(
  id: string,
  passed = true,
  hasAttempt = true,
  canRunExamples = true,
): PageState {
  return {
    theme: THEME,
    page: {
      kind: 'problem',
      problem: {
        id,
        title: id === 'pair-sum' ? 'Pair Sum' : 'Binary Search',
        topics: ['Arrays', 'Hash Maps'],
        difficulty: 'Easy',
        passed,
        statement: 'Find **two** indices.',
        examples: [{ input: 'nums = [2, 7], target = 9', output: '[0, 1]' }],
        inputSyntax: 'Input: <nums> | <target>, for example: [2,7] | 9',
        sampleInput: '[2, 7] 9',
        intendedTime: 'O(n)',
        intendedSpace: 'O(n)',
        hasAttempt,
        canRunExamples,
        guides: [{ id: 'hash-maps', title: 'Hash Maps and Sets' }],
        tracks: [{ id: 'blind-75', title: 'Blind 75' }],
      },
    },
  };
}

describe('bridge', () => {
  beforeEach(() => {
    resetBridgeForTest();
  });

  it('queues outgoing events before Java attaches practiceSend and flushes them in order', () => {
    const received: string[] = [];
    send({ type: 'selectGuide', id: 'hash-maps' });
    send({ type: 'selectExercise', id: 'pair-sum' });
    expect(received).toEqual([]);

    attachSend((message) => received.push(message));
    expect(received).toEqual([
      JSON.stringify({ type: 'selectGuide', id: 'hash-maps' }),
      JSON.stringify({ type: 'selectExercise', id: 'pair-sum' }),
    ]);

    send({ type: 'selectTrack', id: 'blind-75' });
    expect(received).toHaveLength(3);
  });
});

describe('App', () => {
  beforeEach(() => {
    resetBridgeForTest();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders problem metadata, resets accordion state across exercises, and forwards badge clicks', async () => {
    const user = userEvent.setup();
    const sent: unknown[] = [];
    attachSend((message) => sent.push(JSON.parse(message)));

    renderDom(<App />);
    expect(document.body.textContent).toBe('');

    act(() => {
      pushState(problemState('pair-sum'));
    });

    expect(screen.getByRole('heading', { name: 'Pair Sum' })).toBeInTheDocument();
    expect(screen.getByText('Arrays')).toBeInTheDocument();
    expect(screen.getByText('Hash Maps')).toBeInTheDocument();
    expect(screen.getByLabelText('Passed')).toBeInTheDocument();
    expect(screen.getByText('Target: O(n) time, O(n) space')).toBeInTheDocument();
    expect(document.documentElement.style.fontSize).toBe('16px');

    act(() => {
      pushState({ ...problemState('pair-sum'), theme: { ...THEME, fontSize: 18 } });
    });
    expect(document.documentElement.style.fontSize).toBe('20px');

    const accordionButton = screen.getByRole('button', { name: 'Custom input format' });
    expect(accordionButton).toHaveAttribute('aria-expanded', 'false');
    await user.click(accordionButton);
    expect(accordionButton).toHaveAttribute('aria-expanded', 'true');

    act(() => {
      pushState(problemState('binary-search', false));
    });
    expect(screen.getByRole('heading', { name: 'Binary Search' })).toBeInTheDocument();
    expect(screen.queryByLabelText('Passed')).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Custom input format' })).toHaveAttribute('aria-expanded', 'false');

    await user.click(screen.getByRole('button', { name: 'Hash Maps and Sets' }));
    await user.click(screen.getByRole('button', { name: 'Blind 75' }));
    expect(sent).toEqual([
      { type: 'selectGuide', id: 'hash-maps' },
      { type: 'selectTrack', id: 'blind-75' },
    ]);

    act(() => {
      pushState({ theme: THEME, page: { kind: 'problem', problem: null } });
    });
    expect(screen.getByText('No exercises match these filters.')).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Reset filters' }));
    expect(sent).toContainEqual({ type: 'clearFilters' });
  });

  it('wires problem action buttons and sample input controls', async () => {
    const user = userEvent.setup();
    const writeText = vi.spyOn(navigator.clipboard, 'writeText');
    const sent: unknown[] = [];
    attachSend((message) => sent.push(JSON.parse(message)));

    renderDom(<App />);
    act(() => {
      pushState(problemState('pair-sum', false, false, false));
    });

    expect(screen.getByRole('button', { name: 'Start Attempt' })).toBeEnabled();
    expect(screen.getByRole('button', { name: 'Run Examples' })).toBeDisabled();
    await user.click(screen.getByRole('button', { name: 'Start Attempt' }));
    expect(sent).toEqual([{ type: 'startOrOpen' }]);

    act(() => {
      pushState(problemState('pair-sum', false, true, false));
    });
    expect(screen.getByRole('button', { name: 'Open Solution.java' })).toBeEnabled();
    expect(screen.getByRole('button', { name: 'Run Examples' })).toBeDisabled();
    await user.click(screen.getByRole('button', { name: 'Custom input format' }));
    expect(screen.getByRole('button', { name: 'Run sample' })).toBeDisabled();

    act(() => {
      pushState(problemState('pair-sum', false, true, true));
    });
    expect(screen.getByRole('button', { name: 'Open Solution.java' })).toBeEnabled();
    expect(screen.getByRole('button', { name: 'Run Examples' })).toBeEnabled();
    await user.click(screen.getByRole('button', { name: 'Run Examples' }));

    expect(screen.getByText('[2, 7] 9')).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Copy sample input' }));
    expect(writeText).toHaveBeenCalledWith('[2, 7] 9');
    expect(await screen.findByText('Copied')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Run sample' }));
    expect(sent).toEqual([
      { type: 'startOrOpen' },
      { type: 'runExamples' },
      { type: 'runSample' },
    ]);
  });

  it('falls back to execCommand when clipboard.writeText rejects and hides Copied when copy fails', async () => {
    const user = userEvent.setup();
    vi.spyOn(navigator.clipboard, 'writeText').mockRejectedValue(new Error('denied'));
    const execCommand = vi.fn(() => true);
    Object.defineProperty(document, 'execCommand', {
      configurable: true,
      writable: true,
      value: execCommand,
    });

    renderDom(<App />);
    act(() => {
      pushState(problemState('pair-sum'));
    });
    await user.click(screen.getByRole('button', { name: 'Custom input format' }));
    await user.click(screen.getByRole('button', { name: 'Copy sample input' }));
    expect(execCommand).toHaveBeenCalledWith('copy');
    expect(await screen.findByText('Copied')).toBeInTheDocument();

    execCommand.mockImplementation(() => {
      throw new Error('unsupported');
    });
    act(() => {
      pushState(problemState('binary-search'));
    });
    await user.click(screen.getByRole('button', { name: 'Custom input format' }));
    await user.click(screen.getByRole('button', { name: 'Copy sample input' }));
    await Promise.resolve();
    expect(screen.queryByText('Copied')).not.toBeInTheDocument();
  });

  it('renders study guides, practice tracks, and chooser states', async () => {
    const user = userEvent.setup();
    const onSend = vi.fn();
    attachSend(onSend);

    renderDom(<App />);
    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'guide',
          guide: {
            id: 'blind-75',
            title: 'Blind 75',
            kind: 'track',
            summary: 'Original practice problems.',
            lesson: 'About this list',
            levels: [
              {
                level: 'Easy',
                exercises: [
                  { id: 'pair-sum', title: 'Pair Sum', topics: ['Arrays', 'Hash Maps'], passed: true, selected: true },
                  { id: 'move-zeroes', title: 'Move Zeroes', topics: ['Arrays', 'Two Pointers'], passed: false, selected: false },
                ],
              },
              { level: 'Medium', exercises: [] },
              {
                level: 'Hard',
                exercises: [{ id: 'trapped-rainwater', title: 'Trapped Rainwater', topics: ['Arrays', 'Two Pointers'], passed: false, selected: false }],
              },
            ],
          },
        },
      });
    });

    expect(screen.getByText('Practice track')).toBeInTheDocument();
    expect(screen.getByText('1/3 passed')).toBeInTheDocument();
    expect(screen.getByText('1/2 passed')).toBeInTheDocument();
    expect(screen.getByText('0/1 passed')).toBeInTheDocument();
    expect(screen.getByText('No Medium problems in this track yet.')).toBeInTheDocument();

    await user.click(screen.getByText('Move Zeroes'));
    expect(onSend).toHaveBeenCalledWith(JSON.stringify({ type: 'selectExercise', id: 'move-zeroes' }));

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'guide',
          guide: {
            id: 'graphs',
            title: 'Graphs',
            kind: 'guide',
            summary: 'Graph traversals.',
            lesson: 'How it works',
            levels: [
              { level: 'Easy', exercises: [] },
              { level: 'Medium', exercises: [{ id: 'course-schedule', title: 'Course Schedule', topics: ['Graphs'], passed: false, selected: false }] },
              { level: 'Hard', exercises: [] },
            ],
          },
        },
      });
    });
    expect(screen.getByText('No Easy problems in this guide yet.')).toBeInTheDocument();

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'chooser',
          selected: { title: 'Roman to Integer', guides: [], tracks: [] },
        },
      });
    });
    expect(screen.getByText('Not part of any guide yet.')).toBeInTheDocument();
  });

  it('renders locked and unlocked AI Coach states and dispatches coach events', async () => {
    const user = userEvent.setup();
    const sent: unknown[] = [];
    attachSend((message) => sent.push(JSON.parse(message)));

    renderDom(<App />);
    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'coach',
          coach: {
            exerciseId: 'coin-change',
            exerciseTitle: 'Coin Change',
            attemptId: 'att-1',
            unlocked: false,
            lockReason: 'Run Check on this attempt at least once to unlock the AI Coach.',
            running: false,
            checkSummary: 'Not checked yet',
            stale: false,
            error: '',
            modelLabel: 'CLI default',
            turns: [
              { role: 'user', markdown: 'Saved prior turn', createdAt: '2026-01-01T00:00:00Z' },
            ],
          },
        },
      });
    });

    expect(screen.getByRole('heading', { name: 'AI Coach — Coin Change' })).toBeInTheDocument();
    expect(screen.getByText('AI Coach is locked')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Diagnose my attempt' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Translate my approach' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Compare with optimal' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Clear' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Ask' })).toBeDisabled();

    await user.click(screen.getByRole('button', { name: 'Settings' }));
    expect(sent).toEqual([{ type: 'coachSettings' }]);

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'coach',
          coach: {
            exerciseId: 'coin-change',
            exerciseTitle: 'Coin Change',
            attemptId: 'att-1',
            unlocked: true,
            lockReason: '',
            running: false,
            checkSummary: 'Failed: wrong answer (3/5 tests)',
            stale: true,
            error: '',
            modelLabel: 'gpt-5',
            turns: [
              { role: 'user', markdown: 'Translate my approach', createdAt: '2026-01-01T00:00:00Z' },
              {
                role: 'assistant',
                markdown: [
                  '### State',
                  'Define **optimal** `dp[a]`.',
                  '',
                  '| Approach | Time |',
                  '| --- | --- |',
                  '| Bottom-up | O(n) |',
                  '',
                  '---',
                  '',
                  '```java',
                  'int[] dp = new int[amount + 1];',
                  '```',
                ].join('\n'),
                createdAt: '2026-01-01T00:00:05Z',
              },
            ],
          },
        },
      });
    });

    expect(screen.getByText('Model: gpt-5')).toBeInTheDocument();
    expect(screen.getByText('Edited since last check')).toBeInTheDocument();
    expect(screen.getByText(/model-inferred optimal approach/i)).toBeInTheDocument();
    expect(screen.getByText('int[] dp = new int[amount + 1];')).toBeInTheDocument();
    expect(screen.getByRole('table')).toBeInTheDocument();
    expect(screen.getByText('Bottom-up')).toBeInTheDocument();
    expect(screen.getByText('optimal').tagName).toBe('STRONG');

    await user.click(screen.getByRole('button', { name: 'Diagnose my attempt' }));
    await user.click(screen.getByRole('button', { name: 'Trace an example' }));
    await user.click(screen.getByRole('button', { name: 'Translate my approach' }));
    await user.click(screen.getByRole('button', { name: 'Compare with optimal' }));
    await user.click(screen.getByRole('button', { name: 'Clear' }));

    const textarea = screen.getByLabelText('Follow-up question');
    await user.type(textarea, '  How do I pick the loop order?  ');
    await user.click(screen.getByRole('button', { name: 'Ask' }));

    expect(sent).toEqual([
      { type: 'coachSettings' },
      { type: 'coachDiagnose' },
      { type: 'coachTrace' },
      { type: 'coachTranslate' },
      { type: 'coachCompare' },
      { type: 'coachClear' },
      { type: 'coachFollowUp', text: 'How do I pick the loop order?' },
    ]);

    // Unsent draft resets when switching between two attempts of the same exercise.
    await user.type(textarea, 'Unsent attempt-1 draft');
    expect(textarea).toHaveValue('Unsent attempt-1 draft');
    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'coach',
          coach: {
            exerciseId: 'coin-change',
            exerciseTitle: 'Coin Change',
            attemptId: 'att-2',
            unlocked: true,
            lockReason: '',
            running: false,
            checkSummary: 'Passed (5/5 tests)',
            stale: false,
            error: '',
            modelLabel: 'gpt-5',
            turns: [],
          },
        },
      });
    });
    expect(screen.getByLabelText('Follow-up question')).toHaveValue('');

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'coach',
          coach: {
            exerciseId: 'coin-change',
            exerciseTitle: 'Coin Change',
            attemptId: 'att-2',
            unlocked: true,
            lockReason: '',
            running: true,
            checkSummary: 'Passed (5/5 tests)',
            stale: false,
            error: 'Previous timeout',
            modelLabel: 'gpt-5',
            turns: [],
          },
        },
      });
    });

    expect(screen.getByText('AI Coach is analyzing your attempt...')).toBeInTheDocument();
    expect(screen.getByText('Previous timeout')).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Cancel' }));
    expect(sent).toContainEqual({ type: 'coachCancel' });
  });

  it('renders progressive hints with Markdown and dispatches revealHint', async () => {
    const user = userEvent.setup();
    const sent: unknown[] = [];
    attachSend((message) => sent.push(JSON.parse(message)));

    renderDom(<App />);
    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'hints',
          hints: {
            exerciseTitle: 'Pair Sum',
            hasAttempt: false,
            revealedCount: 0,
            totalCount: 3,
            revealedHints: [],
          },
        },
      });
    });

    expect(screen.getByText('Start or resume an attempt to reveal hints.')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Reveal Hint' })).toBeDisabled();

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'hints',
          hints: {
            exerciseTitle: 'Pair Sum',
            hasAttempt: true,
            revealedCount: 1,
            totalCount: 3,
            revealedHints: ['Store **seen** indices in a `Map`.'],
          },
        },
      });
    });

    expect(screen.getByText('Hint 1 of 3')).toBeInTheDocument();
    expect(screen.getByText('seen').tagName).toBe('STRONG');
    await user.click(screen.getByRole('button', { name: 'Reveal Hint 2 of 3' }));
    expect(sent).toEqual([{ type: 'revealHint' }]);

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'hints',
          hints: {
            exerciseTitle: 'Pair Sum',
            hasAttempt: true,
            revealedCount: 3,
            totalCount: 3,
            revealedHints: ['One', 'Two', 'Three'],
          },
        },
      });
    });
    expect(screen.getByRole('button', { name: 'All 3 Hints Revealed' })).toBeDisabled();
  });

  it('renders structured Results for passed scaling, failure diagnosis, and Ask AI Coach', async () => {
    const user = userEvent.setup();
    const sent: unknown[] = [];
    attachSend((message) => sent.push(JSON.parse(message)));

    renderDom(<App />);
    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'results',
          results: {
            tone: 'passed',
            banner: 'Passed',
            checkedAt: '2026-11-24T21:34:55.720275Z',
            lastPassedAt: '2026-11-24T21:34:55.720275Z',
            stale: false,
            staleReason: '',
            askCoachEnabled: true,
            output: [
              'Managed attempt: tree-max-depth-44dce720dbbe44369a8ee7544eb0744a',
              'Full check: PASSED',
              'All 12 supplied cases passed. This does not prove optimal complexity.',
              '',
              'Measured scaling (per call)',
              '         n            time       allocated',
              '     32768           74 us           792 B',
              '     65536          186 us           792 B',
              '    524288         1193 us           792 B',
              '',
              'Time:  O(n) or O(n log n) (consistent with the intended O(n))',
              'Space: O(1) (consistent with the intended O(1))',
              'Measured on this machine under load; it indicates growth, not proof of complexity.',
            ].join('\n'),
          },
        },
      });
    });

    expect(screen.getByRole('heading', { name: 'Passed' })).toBeInTheDocument();
    expect(screen.getByText('All 12 supplied cases passed. This does not prove optimal complexity.')).toBeInTheDocument();
    expect(screen.getByText('O(n) or O(n log n)')).toBeInTheDocument();
    expect(screen.getByText('Matches intended O(n)')).toBeInTheDocument();
    expect(screen.getByText('O(1)')).toBeInTheDocument();
    expect(screen.getByText('Matches intended O(1)')).toBeInTheDocument();
    expect(screen.getByText('524,288')).toBeInTheDocument();
    expect(screen.getByText('1193 us')).toBeInTheDocument();
    expect(screen.getByText('Raw output')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Ask AI Coach' }));
    expect(sent).toEqual([{ type: 'openCoach' }]);

    act(() => {
      pushState({
        theme: THEME,
        page: {
          kind: 'results',
          results: {
            tone: 'failed',
            banner: 'Failed: runtime error',
            checkedAt: '',
            lastPassedAt: '',
            stale: true,
            staleReason: 'solution was edited; run or check again.',
            askCoachEnabled: false,
            output: [
              'Failed on Example 1:',
              '  Input:    "A man, a plan, a canal: Panama"',
              '  Expected: true',
              '  Error:    java.lang.StringIndexOutOfBoundsException: Index 28',
              '  at Solution.isPalindrome(Solution.java:13)',
            ].join('\n'),
          },
        },
      });
    });

    expect(screen.getByText('Result may be out of date')).toBeInTheDocument();
    expect(screen.getByText('Failed on Example 1')).toBeInTheDocument();
    expect(screen.getByText('"A man, a plan, a canal: Panama"')).toBeInTheDocument();
    expect(screen.getByText('true')).toBeInTheDocument();
    expect(screen.getByText('at Solution.isPalindrome(Solution.java:13)')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Ask AI Coach' })).toBeDisabled();
  });

  it('switches dev sample pages on hashchange using valid catalog IDs', () => {
    window.location.hash = '';
    renderSample();
    expect(snapshot()?.page.kind).toBe('problem');

    window.location.hash = '#guide';
    window.dispatchEvent(new HashChangeEvent('hashchange'));
    const guidePage = snapshot()?.page;
    expect(guidePage?.kind).toBe('guide');
    if (guidePage?.kind === 'guide') {
      expect(guidePage.guide.id).toBe('hash-maps');
    }

    window.location.hash = '#chooser';
    window.dispatchEvent(new HashChangeEvent('hashchange'));
    expect(snapshot()?.page.kind).toBe('chooser');
    window.location.hash = '';
  });
});
