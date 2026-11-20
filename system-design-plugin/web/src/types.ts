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

export type TrackItem = {
  id: string;
  title: string;
  summary: string;
  problemIds: string[];
};

export type GuideItem = {
  id: string;
  title: string;
  summary: string;
  /** Escaped by MarkdownHtml on the Java side. */
  bodyHtml: string;
  relatedProblemIds: string[];
};

export type ProblemItem = {
  id: string;
  title: string;
  difficulty: string;
  summary: string;
  tracks: string[];
  reviewed: boolean;
  verdict: string;
  selected: boolean;
};

export type Attempt = {
  id: string;
  label: string;
  customLabel: string;
};

export type SelectedProblem = {
  id: string;
  title: string;
  difficulty: string;
  /** Opening prompt paragraph only, escaped by MarkdownHtml. */
  promptHtml: string;
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
  level: string;
  round: string;
  followUpAnswers: string;
  stale: boolean;
};

export type PageState = {
  theme: Theme;
  levels: string[];
  targetLevel: string;
  tracks: TrackItem[];
  guides: GuideItem[];
  problems: ProblemItem[];
  selected: SelectedProblem | null;
  running: boolean;
  reviews: Feedback[];
  feedback: Feedback | null;
  stale: boolean;
  error: string;
};

export type PageEvent =
  | {
      type:
        | 'selectProblem'
        | 'newAttempt'
        | 'selectAttempt'
        | 'openAttempt'
        | 'deleteAttempt'
        | 'revealHint'
        | 'requestFeedback';
      id: string;
    }
  | { type: 'renameAttempt'; id: string; label: string }
  | { type: 'submitFollowUp'; id: string; answers: string }
  | { type: 'selectLevel'; level: string }
  | { type: 'cancelFeedback' | 'openSettings' };

export type EditorMessage =
  | { type: 'ready' }
  | { type: 'flushed'; seq: number }
  | { type: 'save'; seq: number; scene: string };