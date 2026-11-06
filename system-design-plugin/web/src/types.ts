// The page contract shared with PageState.java and PageEvent.java. Keep both sides in step.

export type Theme = {
  dark: boolean;
  background: string;
  foreground: string;
  secondary: string;
  border: string;
  accent: string;
  fontFamily: string;
  fontSize: number;
};

export type ProblemItem = {
  id: string;
  title: string;
  difficulty: string;
  summary: string;
  reviewed: boolean;
  selected: boolean;
};

export type Attempt = { id: string; label: string };

export type SelectedProblem = {
  id: string;
  title: string;
  difficulty: string;
  /** Escaped by MarkdownHtml on the Java side. */
  statementHtml: string;
  /** Revealed hints only, escaped by MarkdownHtml. */
  hintsHtml: string[];
  hintCount: number;
  attempts: Attempt[];
  selectedAttempt: string;
};

export type Dimension = { name: string; score: number; strengths: string[]; gaps: string[] };

export type Feedback = {
  parsed: boolean;
  verdict: string;
  summary: string;
  dimensions: Dimension[];
  followUpQuestions: string[];
  missedExpectations: string[];
  raw: string;
  model: string;
  createdAt: string;
};

export type PageState = {
  theme: Theme;
  problems: ProblemItem[];
  selected: SelectedProblem | null;
  running: boolean;
  feedback: Feedback | null;
  stale: boolean;
  error: string;
};

export type PageEvent =
  | {
      type: 'selectProblem' | 'newAttempt' | 'selectAttempt' | 'openAttempt' | 'revealHint' | 'requestFeedback';
      id: string;
    }
  | { type: 'cancelFeedback' | 'openSettings' };

export type EditorMessage = { type: 'ready' } | { type: 'flushed' } | { type: 'save'; scene: string };