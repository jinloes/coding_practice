import { act, render as renderDom, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { App } from './App';
import { render as renderBridge } from './bridge';
import type { Feedback, PageState } from './types';

function makeReview(overrides: Partial<Feedback>): Feedback {
  return {
    parsed: true,
    verdict: 'Hire',
    summary: 'Improved partitioning and capacity math.',
    dimensions: [
      { name: 'Capacity estimates', score: 2, strengths: ['QPS estimated'], gaps: ['Storage missed'] },
      { name: 'High-level architecture', score: 4, strengths: ['Clean flow'], gaps: [] },
      { name: 'Reliability and tradeoffs', score: 2, strengths: [], gaps: ['Single region'] },
    ],
    followUpQuestions: ['How do you fail over?'],
    missedExpectations: [],
    raw: '',
    model: 'gpt-test',
    createdAt: '2026-02-01T12:00:00Z',
    level: 'Senior',
    round: 'follow-up',
    followUpAnswers: 'Use active-passive failover.',
    stale: false,
    ...overrides,
  };
}

function makeCoachingState(overrides: Partial<PageState> = {}): PageState {
  const latest = makeReview({});
  const unparsedMiddle = makeReview({
    parsed: false,
    verdict: '',
    dimensions: [],
    raw: 'unparsed retry output',
    createdAt: '2026-02-01T11:30:00Z',
  });
  const initial = makeReview({
    verdict: 'Lean hire',
    round: 'initial',
    followUpAnswers: '',
    createdAt: '2026-02-01T11:00:00Z',
    dimensions: [
      { name: 'Capacity estimates', score: 3, strengths: ['Solid math'], gaps: [] },
      { name: 'High-level architecture', score: 3, strengths: ['Good boxes'], gaps: ['Cache missing'] },
      { name: 'Reliability and tradeoffs', score: 2, strengths: [], gaps: ['Single point of failure'] },
    ],
  });

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
    tracks: [],
    guides: [
      {
        id: 'delivery-framework',
        title: 'The 45-Minute Interview Roadmap',
        summary: 'Pace your system design interview.',
        bodyHtml: '<p>Roadmap body.</p>',
        relatedProblemIds: ['url-shortener'],
      },
      {
        id: 'numbers-that-matter',
        title: 'Back-of-the-Envelope Numbers',
        summary: 'Latency, QPS, storage, and bandwidth estimation.',
        bodyHtml: '<p>Numbers body.</p>',
        relatedProblemIds: ['url-shortener'],
      },
      {
        id: 'reliability-patterns',
        title: 'Reliability, Retries & Degradation',
        summary: 'Timeouts, idempotency, circuit breakers, and failover.',
        bodyHtml: '<p>Reliability body.</p>',
        relatedProblemIds: ['url-shortener'],
      },
    ],
    dimensionSummary: [
      {
        name: 'Capacity estimates',
        averageScore: 2.0,
        reviewedCount: 1,
        guideId: 'numbers-that-matter',
        guideTitle: 'Back-of-the-Envelope Numbers',
      },
      {
        name: 'High-level architecture',
        averageScore: 4.0,
        reviewedCount: 1,
        guideId: 'delivery-framework',
        guideTitle: 'The 45-Minute Interview Roadmap',
      },
    ],
    problems: [
      {
        id: 'url-shortener',
        title: 'Link Shortening Service',
        difficulty: 'Easy',
        summary: 'Turn long links into short codes.',
        tracks: [],
        reviewed: true,
        verdict: 'Hire',
        selected: true,
      },
    ],
    selected: {
      id: 'url-shortener',
      title: 'Link Shortening Service',
      difficulty: 'Easy',
      promptHtml: '<p>Design a short-link service.</p>',
      statementHtml: '<p>Design a short-link service.</p>',
      hintsHtml: [],
      hintCount: 3,
      attempts: [{ id: 'url-shortener-a1', label: 'Attempt 1', customLabel: '' }],
      selectedAttempt: 'url-shortener-a1',
    },
    running: false,
    reviews: [latest, unparsedMiddle, initial],
    feedback: latest,
    stale: false,
    error: '',
    ...overrides,
  };
}

describe('ReadinessPanel and FeedbackView coaching loop', () => {
  beforeEach(() => {
    window.designSend = vi.fn();
  });

  it('hides ReadinessPanel when no dimensions are reviewed and jumps to targeted guide for weak dimensions', async () => {
    const user = userEvent.setup();
    renderDom(<App />);

    act(() =>
      renderBridge(
        makeCoachingState({
          dimensionSummary: [
            {
              name: 'Capacity estimates',
              averageScore: 0,
              reviewedCount: 0,
              guideId: 'numbers-that-matter',
              guideTitle: 'Back-of-the-Envelope Numbers',
            },
          ],
        }),
      ),
    );
    expect(screen.queryByRole('button', { name: 'Rubric readiness' })).not.toBeInTheDocument();

    act(() => renderBridge(makeCoachingState()));
    const toggle = screen.getByRole('button', { name: 'Rubric readiness' });
    expect(screen.getByText('1 focus area below 3.0')).toBeInTheDocument();
    await user.click(toggle);

    expect(screen.getByText('2.0 / 4')).toBeInTheDocument();
    expect(screen.getByText('4.0 / 4')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Study: The 45-Minute Interview Roadmap' })).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Study: Back-of-the-Envelope Numbers' }));
    expect(screen.getByRole('heading', { name: 'Back-of-the-Envelope Numbers' })).toBeInTheDocument();

    // Selecting another guide inside GuideView and then clicking Study again while already on Guides updates active guide.
    await user.click(screen.getByRole('button', { name: /The 45-Minute Interview Roadmap/i }));
    expect(screen.getByRole('heading', { name: 'The 45-Minute Interview Roadmap' })).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Study: Back-of-the-Envelope Numbers' }));
    expect(screen.getByRole('heading', { name: 'Back-of-the-Envelope Numbers' })).toBeInTheDocument();
  });

  it('renders verdict progression, +1/-1 score deltas across reviews, and weak-dimension Study guide links', async () => {
    const user = userEvent.setup();
    renderDom(<App />);
    act(() => renderBridge(makeCoachingState()));

    expect(screen.getByLabelText('Verdict progression')).toHaveTextContent('Lean hire → Hire');
    expect(screen.getByText('+1')).toBeInTheDocument();
    expect(screen.getByText('-1')).toBeInTheDocument();

    const studyButtons = screen.getAllByRole('button', { name: 'Study guide' });
    expect(studyButtons).toHaveLength(2);
    await user.click(studyButtons[1]);
    expect(screen.getByRole('heading', { name: 'Reliability, Retries & Degradation' })).toBeInTheDocument();
  });
});
