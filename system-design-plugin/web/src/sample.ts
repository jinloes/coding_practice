import { render } from './bridge';
import type { PageState } from './types';

/** Sample state for `npm run dev`, where no plugin calls designRender. */
export function renderSample(): void {
  const dimensions = [
    'Requirements and scope',
    'Capacity estimates',
    'API and data model',
    'High-level architecture',
    'Deep dives and scalability',
    'Reliability and tradeoffs',
    'Communication',
  ];
  const latestReview = {
    parsed: true,
    verdict: 'Lean hire',
    summary: 'A reasonable design with gaps in capacity planning.',
    dimensions: dimensions.map((name, index) => ({
      name,
      score: (index % 4) + 1,
      strengths: ['Clear components'],
      gaps: index % 2 === 0 ? ['No numbers given'] : [],
    })),
    followUpQuestions: ['How would you handle hot links?'],
    missedExpectations: ['Cache popular redirects'],
    raw: '',
    model: 'sample-model',
    createdAt: '2024-01-01T00:00:00Z',
    level: 'Senior',
    round: 'initial',
    followUpAnswers: '',
    stale: true,
  };
  const state: PageState = {
    theme: {
      dark: true,
      background: '#2b2d30',
      foreground: '#dfe1e5',
      secondary: '#868a91',
      border: '#393b40',
      accent: '#548af7',
      fontFamily: '"Inter", system-ui, sans-serif',
      fontSize: 13,
    },
    levels: ['Mid-Level', 'Senior', 'Staff+'],
    targetLevel: 'Senior',
    tracks: [
      {
        id: 'core-foundations',
        title: 'Core Infrastructure Foundations',
        summary: 'Storage engines, caching, ID generation, and rate limiting.',
        problemIds: ['url-shortener', 'rate-limiter'],
      },
    ],
    guides: [
      {
        id: 'delivery-framework',
        title: 'The 45-Minute Interview Roadmap',
        summary: 'Pace a system design interview cleanly from scoping to deep dives.',
        bodyHtml: '<h4>1. Requirements</h4><p>Pin down core features and constraints.</p>',
        relatedProblemIds: ['url-shortener', 'rate-limiter'],
      },
    ],
    problems: [
      {
        id: 'url-shortener',
        title: 'URL shortener',
        difficulty: 'Easy',
        summary: 'Short links at scale.',
        tracks: ['core-foundations'],
        reviewed: true,
        verdict: 'Lean hire',
        selected: true,
      },
      {
        id: 'rate-limiter',
        title: 'Rate limiter',
        difficulty: 'Medium',
        summary: 'Throttle API clients.',
        tracks: ['core-foundations'],
        reviewed: false,
        verdict: '',
        selected: false,
      },
    ],
    selected: {
      id: 'url-shortener',
      title: 'URL shortener',
      difficulty: 'Easy',
      promptHtml: '<p>Design a service that turns long URLs into short links.</p>',
      statementHtml: '<p>Design a service that turns long URLs into short links.</p>',
      hintsHtml: ['Estimate reads versus writes first.'],
      hintCount: 3,
      attempts: [{ id: 'a1', label: 'Attempt 1 (Jan 1, 00:00)', customLabel: '' }],
      selectedAttempt: 'a1',
    },
    running: false,
    reviews: [latestReview],
    feedback: latestReview,
    stale: true,
    error: '',
  };
  render(state);
}