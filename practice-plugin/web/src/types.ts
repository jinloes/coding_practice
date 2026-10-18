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
  statementHtml: string;
  examples: Example[];
  inputSyntax: string;
  intendedTime: string;
  intendedSpace: string;
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
  summaryHtml: string;
  lessonHtml: string;
  levels: GuideLevel[];
};

export type Membership = { title: string; guides: Link[]; tracks: Link[] };

export type Page =
  | { kind: 'problem'; problem: Problem | null }
  | { kind: 'guide'; guide: Guide }
  | { kind: 'chooser'; selected: Membership | null };

export type PageState = { theme: Theme; page: Page };

export type PageEvent = { type: 'selectExercise' | 'selectGuide' | 'selectTrack'; id: string };
