import { act, render as renderDom, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { App } from './App';
import { render as renderBridge } from './bridge';
import { formatClock, phaseForElapsedSeconds } from './InterviewTimer';
import type { PageState } from './types';

function makeState(overrides: Partial<PageState> = {}): PageState {
  const latestReview = {
    parsed: true,
    verdict: 'Hire',
    summary: 'Clean architecture; dig deeper into hot-key sharding.',
    dimensions: [
      {
        name: 'Requirements and scope',
        score: 3,
        strengths: ['Scoped latency'],
        gaps: ['Clarify retention'],
      },
    ],
    followUpQuestions: ['How would you isolate a celebrity account?'],
    missedExpectations: ['Hot-key cache replication'],
    raw: '',
    model: 'gpt-test',
    createdAt: '2026-01-15T10:30:00Z',
    level: 'Senior',
    round: 'initial',
    followUpAnswers: '',
    stale: false,
  };
  return {
    theme: {
      dark: true,
      background: '#2b2d30',
      foreground: '#dfe1e5',
      secondary: '#868a91',
      border: '#393b40',
      accent: '#548af7',
      fontFamily: 'sans-serif',
      fontSize: 13,
    },
    levels: ['Mid-Level', 'Senior', 'Staff+'],
    targetLevel: 'Senior',
    tracks: [
      {
        id: 'core-foundations',
        title: 'Core Infrastructure Foundations',
        summary: 'Storage, caching, and rate limiting.',
        problemIds: ['url-shortener'],
      },
      {
        id: 'realtime-and-social',
        title: 'Real-Time & High-Fanout Systems',
        summary: 'Live fan-out and feeds.',
        problemIds: ['news-feed'],
      },
    ],
    guides: [
      {
        id: 'delivery-framework',
        title: 'The 45-Minute Interview Roadmap',
        summary: 'Pace your system design interview.',
        bodyHtml: '<h4>1. Requirements</h4><p>Focus on top capabilities.</p>',
        relatedProblemIds: ['news-feed'],
      },
    ],
    problems: [
      {
        id: 'url-shortener',
        title: 'Link Shortening Service',
        difficulty: 'Easy',
        summary: 'Turn long links into short codes.',
        tracks: ['core-foundations'],
        reviewed: true,
        verdict: 'Hire',
        selected: true,
      },
      {
        id: 'news-feed',
        title: 'Social News Feed',
        difficulty: 'Hard',
        summary: 'Ranked home feed at scale.',
        tracks: ['realtime-and-social'],
        reviewed: false,
        verdict: '',
        selected: false,
      },
    ],
    selected: {
      id: 'url-shortener',
      title: 'Link Shortening Service',
      difficulty: 'Easy',
      promptHtml: '<p>Design a short-link service.</p>',
      statementHtml: '<p>Design a short-link service.</p><p>Scale: 50M links/month.</p>',
      hintsHtml: ['Size the key space first.'],
      hintCount: 3,
      attempts: [{ id: 'url-shortener-a1', label: 'Attempt 1 (Jan 15, 10:00)', customLabel: '' }],
      selectedAttempt: 'url-shortener-a1',
    },
    running: false,
    reviews: [latestReview],
    feedback: latestReview,
    stale: false,
    error: '',
    ...overrides,
  };
}

describe('App and InterviewTimer', () => {
  const sent: unknown[] = [];

  beforeEach(() => {
    sent.length = 0;
    window.designSend = vi.fn((raw: string) => {
      sent.push(JSON.parse(raw));
    });
  });

  it('filters problems by search query and switches between Guides and Problems', async () => {
    const user = userEvent.setup();
    renderDom(<App />);
    act(() => renderBridge(makeState()));

    expect(screen.getByLabelText('Readiness summary')).toHaveTextContent('Reviewed 1 / 2 · Passing (Hire+): 1');
    await user.click(screen.getByRole('button', { name: 'Surprise me' }));
    expect(sent).toContainEqual({ type: 'selectProblem', id: 'news-feed' });

    await user.click(screen.getByText('Ambiguous prompt'));
    expect(screen.queryByText('Scale: 50M links/month.')).not.toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: /Reveal requirements/i }));
    expect(screen.getByText('Scale: 50M links/month.')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Show checklist' }));
    await user.click(screen.getByLabelText(/Scoped top 3–4/i));
    expect(screen.getByText('Pre-review self-check (1/5)')).toBeInTheDocument();

    expect(screen.getByText('Social News Feed')).toBeInTheDocument();
    await user.type(screen.getByLabelText('Search problems'), 'news');
    expect(screen.queryByRole('button', { name: /Link Shortening Service/i })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Social News Feed/i })).toBeInTheDocument();

    await user.click(screen.getByText('Guides (1)'));
    expect(screen.getByRole('heading', { name: 'The 45-Minute Interview Roadmap' })).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: /Social News Feed/i }));
    expect(sent).toContainEqual({ type: 'selectProblem', id: 'news-feed' });
  });

  it('renames and deletes attempts and submits interactive follow-up answers as plain text', async () => {
    const user = userEvent.setup();
    renderDom(<App />);
    act(() => renderBridge(makeState()));

    await user.click(screen.getByRole('button', { name: 'Rename' }));
    const nameInput = screen.getByLabelText('Attempt name');
    await user.type(nameInput, 'Redis + Counter');
    await user.click(screen.getByRole('button', { name: 'Save name' }));
    expect(sent).toContainEqual({ type: 'renameAttempt', id: 'url-shortener-a1', label: 'Redis + Counter' });

    await user.click(screen.getByRole('button', { name: 'Delete' }));
    await user.click(screen.getByRole('button', { name: 'Confirm delete' }));
    expect(sent).toContainEqual({ type: 'deleteAttempt', id: 'url-shortener-a1' });

    const followUpBox = screen.getByLabelText('Follow-up answers');
    await user.type(followUpBox, 'Pull celebrity posts at read time and merge.');
    await user.click(screen.getByRole('button', { name: 'Submit follow-up answers' }));
    expect(sent).toContainEqual({
      type: 'submitFollowUp',
      id: 'url-shortener-a1',
      answers: 'Pull celebrity posts at read time and merge.',
    });
  });

  it('computes interview clock formatting and delivery phases accurately', () => {
    expect(formatClock(45 * 60)).toBe('45:00');
    expect(formatClock(65)).toBe('01:05');
    expect(phaseForElapsedSeconds(120).label).toContain('Requirements');
    expect(phaseForElapsedSeconds(6 * 60).label).toContain('Core entities');
    expect(phaseForElapsedSeconds(10 * 60).label).toContain('API');
    expect(phaseForElapsedSeconds(15 * 60).label).toContain('High-level');
    expect(phaseForElapsedSeconds(30 * 60).label).toContain('Deep dives');
    expect(phaseForElapsedSeconds(46 * 60).label).toContain('Time up');
  });
});
