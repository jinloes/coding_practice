// The page contract shared with PageJson.java. Keep both sides in step.

export type Theme = {
  dark: boolean;
  background: string;
  foreground: string;
  secondary: string;
  border: string;
  accent: string;
  fontFamily: string;
  codeFontFamily: string;
  fontSize: number;
};

export type Link = { id: string; title: string };

export type Example = { input: string; output: string };

export type Problem = {
  id: string;
  title: string;
  topic: string;
  difficulty: string;
  passed: boolean;
  statement: string;
  examples: Example[];
  inputSyntax: string;
  sampleInput: string;
  intendedTime: string;
  intendedSpace: string;
  hasAttempt: boolean;
  canRunExamples: boolean;
  guides: Link[];
  tracks: Link[];
};

export type GuideExercise = {
  id: string;
  title: string;
  topic: string;
  passed: boolean;
  selected: boolean;
};

export type GuideLevel = { level: string; exercises: GuideExercise[] };

export type Guide = {
  id: string;
  title: string;
  kind: 'guide' | 'track';
  summary: string;
  lesson: string;
  levels: GuideLevel[];
};

export type Hints = {
  exerciseTitle: string | null;
  hasAttempt: boolean;
  revealedCount: number;
  totalCount: number;
  revealedHints: string[];
};

export type Membership = { title: string; guides: Link[]; tracks: Link[] };

export type CoachTurn = {
  role: string;
  markdown: string;
  createdAt: string;
};

export type Coach = {
  exerciseId: string | null;
  exerciseTitle: string | null;
  attemptId: string | null;
  unlocked: boolean;
  lockReason: string;
  running: boolean;
  checkSummary: string;
  stale: boolean;
  error: string;
  modelLabel: string;
  turns: CoachTurn[];
};

export type Results = {
  tone: 'passed' | 'failed' | 'error' | 'running' | 'stale' | 'info' | 'idle' | string;
  banner: string;
  checkedAt: string;
  lastPassedAt: string;
  stale: boolean;
  staleReason: string;
  askCoachEnabled: boolean;
  output: string;
};

export type Page =
  | { kind: 'problem'; problem: Problem | null }
  | { kind: 'guide'; guide: Guide }
  | { kind: 'chooser'; selected: Membership | null }
  | { kind: 'hints'; hints: Hints }
  | { kind: 'results'; results: Results }
  | { kind: 'coach'; coach: Coach };

export type PageState = { theme: Theme; page: Page };

export type PageEvent = {
  type:
    | 'selectExercise'
    | 'selectGuide'
    | 'selectTrack'
    | 'clearFilters'
    | 'startOrOpen'
    | 'runExamples'
    | 'runSample'
    | 'revealHint'
    | 'openCoach'
    | 'coachDiagnose'
    | 'coachTranslate'
    | 'coachCompare'
    | 'coachFollowUp'
    | 'coachCancel'
    | 'coachClear'
    | 'coachSettings';
  id?: string;
  text?: string;
};
