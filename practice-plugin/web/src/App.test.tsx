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
        topic: 'Arrays',
        difficulty: 'Easy',
        passed,
        statementHtml: '<p>Find two indices.</p>',
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
    expect(screen.getByLabelText('Passed')).toBeInTheDocument();
    expect(screen.getByText('Target: O(n) time, O(n) space')).toBeInTheDocument();
    expect(document.documentElement.style.fontSize).toBe('13px');

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
            summaryHtml: 'Original practice problems.',
            lessonHtml: '<p>About this list</p>',
            levels: [
              {
                level: 'Easy',
                exercises: [
                  { id: 'pair-sum', title: 'Pair Sum', topic: 'Arrays', passed: true, selected: true },
                  { id: 'move-zeroes', title: 'Move Zeroes', topic: 'Arrays', passed: false, selected: false },
                ],
              },
              { level: 'Medium', exercises: [] },
              {
                level: 'Hard',
                exercises: [{ id: 'trapped-rainwater', title: 'Trapped Rainwater', topic: 'Arrays', passed: false, selected: false }],
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
            summaryHtml: 'Graph traversals.',
            lessonHtml: '<p>How it works</p>',
            levels: [
              { level: 'Easy', exercises: [] },
              { level: 'Medium', exercises: [{ id: 'course-schedule', title: 'Course Schedule', topic: 'Graphs', passed: false, selected: false }] },
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
