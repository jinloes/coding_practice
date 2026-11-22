import { useEffect, useMemo, useState, useSyncExternalStore } from 'react';
import {
  Badge,
  Box,
  Button,
  createTheme,
  Group,
  MantineProvider,
  NavLink,
  Paper,
  SegmentedControl,
  Select,
  Stack,
  Text,
  TextInput,
  type CSSVariablesResolver,
} from '@mantine/core';
import { send, snapshot, subscribe } from './bridge';
import { GuideView } from './GuideView';
import { difficultyColor, ProblemView, SectionLabel } from './ProblemView';
import { ReadinessPanel } from './ReadinessPanel';
import type { ProblemItem, Theme, TrackItem } from './types';

type Tab = 'practice' | 'guides';

const PASSING_VERDICTS = new Set(['Strong hire', 'Hire']);
const RETRY_VERDICTS = new Set(['Lean no hire', 'No hire', '']);

const VERDICT_BADGE_COLORS: Record<string, string> = {
  'Strong hire': 'teal',
  Hire: 'green',
  'Lean hire': 'lime',
  'Lean no hire': 'orange',
  'No hire': 'red',
};

function needsRetry(problem: ProblemItem): boolean {
  return problem.reviewed && RETRY_VERDICTS.has(problem.verdict);
}

export function App() {
  const state = useSyncExternalStore(subscribe, snapshot);
  const [tab, setTab] = useState<Tab>('practice');
  const [selectedGuideId, setSelectedGuideId] = useState<string>('');
  const [listCollapsed, setListCollapsed] = useState(false);
  const [query, setQuery] = useState('');
  const [trackFilter, setTrackFilter] = useState('all');
  const [difficultyFilter, setDifficultyFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');

  const theme = state?.theme;
  const mantine = useMemo(() => (theme ? mantineTheme(theme) : undefined), [theme]);
  const resolver = useMemo(() => (theme ? variables(theme) : undefined), [theme]);

  useEffect(() => {
    if (theme) {
      document.documentElement.style.fontSize = `${theme.fontSize}px`;
    }
  }, [theme]);

  const selectedId = state?.selected?.id ?? '';
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [selectedId, tab]);

  const problems = state?.problems ?? [];
  const filteredProblems = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();
    return problems.filter((problem) => {
      if (trackFilter !== 'all' && !problem.tracks.includes(trackFilter)) {
        return false;
      }
      if (difficultyFilter !== 'all' && problem.difficulty !== difficultyFilter) {
        return false;
      }
      if (statusFilter === 'reviewed' && !problem.reviewed) {
        return false;
      }
      if (statusFilter === 'unreviewed' && problem.reviewed) {
        return false;
      }
      if (statusFilter === 'retry' && !needsRetry(problem)) {
        return false;
      }
      if (normalizedQuery !== '') {
        const haystack = `${problem.title} ${problem.summary} ${problem.id}`.toLowerCase();
        if (!haystack.includes(normalizedQuery)) {
          return false;
        }
      }
      return true;
    });
  }, [problems, query, trackFilter, difficultyFilter, statusFilter]);

  if (!state || !theme) {
    return null;
  }
  const activeTrack: TrackItem | undefined = state.tracks.find((track) => track.id === trackFilter);
  const showProblemList = !state.selected || !listCollapsed;
  const reviewedCount = state.problems.filter((problem) => problem.reviewed).length;
  const passingCount = state.problems.filter((problem) => PASSING_VERDICTS.has(problem.verdict)).length;

  const pickSurpriseProblem = () => {
    const candidates = filteredProblems.filter((problem) => !problem.reviewed || needsRetry(problem));
    const pool = candidates.length > 0 ? candidates : filteredProblems.length > 0 ? filteredProblems : state.problems;
    if (pool.length === 0) {
      return;
    }
    const chosen = pool[Math.floor(Math.random() * pool.length)];
    send({ type: 'selectProblem', id: chosen.id });
  };

  const openGuide = (guideId: string) => {
    setSelectedGuideId(guideId);
    setTab('guides');
  };

  return (
    <MantineProvider theme={mantine} cssVariablesResolver={resolver} forceColorScheme={theme.dark ? 'dark' : 'light'}>
      <Box p="md">
        <Stack gap="md">
          <Group justify="space-between" align="center" gap="xs" wrap="wrap">
            <SegmentedControl
              size="xs"
              aria-label="Section"
              value={tab}
              onChange={(value) => setTab(value as Tab)}
              data={[
                { value: 'practice', label: `Problems (${state.problems.length})` },
                { value: 'guides', label: `Guides (${state.guides.length})` },
              ]}
            />
            <Group gap={6} align="center">
              <Text size="xs" c="dimmed">
                Target level
              </Text>
              <Select
                aria-label="Target level"
                size="xs"
                w={118}
                allowDeselect={false}
                data={state.levels}
                value={state.targetLevel}
                onChange={(level) => level && send({ type: 'selectLevel', level })}
              />
            </Group>
          </Group>

          <Group justify="space-between" align="center" gap="xs" wrap="wrap">
            <Text size="xs" c="dimmed" aria-label="Readiness summary">
              {`Reviewed ${reviewedCount} / ${state.problems.length} · Passing (Hire+): ${passingCount}`}
            </Text>
            {tab === 'practice' && state.problems.length > 0 && (
              <Button size="compact-xs" variant="light" onClick={pickSurpriseProblem}>
                Surprise me
              </Button>
            )}
          </Group>

          <ReadinessPanel dimensionSummary={state.dimensionSummary} onOpenGuide={openGuide} />

          {tab === 'guides' && (
            <GuideView
              guides={state.guides}
              problems={state.problems}
              selectedGuideId={selectedGuideId}
              onSelectGuide={setSelectedGuideId}
              onSelectProblem={(id) => {
                setTab('practice');
                send({ type: 'selectProblem', id });
              }}
            />
          )}

          <div hidden={tab !== 'practice'} style={{ display: tab === 'practice' ? undefined : 'none' }}>
            <Stack gap="md">
              {state.selected && (
                <Group justify="space-between" align="center">
                  <SectionLabel>{`Problems (${filteredProblems.length} of ${state.problems.length})`}</SectionLabel>
                  <Button
                    variant="subtle"
                    size="compact-xs"
                    aria-expanded={showProblemList}
                    onClick={() => setListCollapsed((collapsed) => !collapsed)}
                  >
                    {showProblemList ? 'Hide problems' : 'Change problem'}
                  </Button>
                </Group>
              )}

              {showProblemList && (
                <Stack gap="xs">
                  <TextInput
                    aria-label="Search problems"
                    size="xs"
                    placeholder="Search problems by topic or keyword…"
                    value={query}
                    onChange={(event) => setQuery(event.currentTarget.value)}
                  />
                  <Group gap="xs" grow>
                    <Select
                      aria-label="Track filter"
                      size="xs"
                      allowDeselect={false}
                      value={trackFilter}
                      onChange={(value) => value && setTrackFilter(value)}
                      data={[
                        { value: 'all', label: 'All tracks' },
                        ...state.tracks.map((track) => ({ value: track.id, label: track.title })),
                      ]}
                    />
                    <Select
                      aria-label="Difficulty filter"
                      size="xs"
                      allowDeselect={false}
                      value={difficultyFilter}
                      onChange={(value) => value && setDifficultyFilter(value)}
                      data={[
                        { value: 'all', label: 'All difficulties' },
                        { value: 'Easy', label: 'Easy' },
                        { value: 'Medium', label: 'Medium' },
                        { value: 'Hard', label: 'Hard' },
                      ]}
                    />
                    <Select
                      aria-label="Status filter"
                      size="xs"
                      allowDeselect={false}
                      value={statusFilter}
                      onChange={(value) => value && setStatusFilter(value)}
                      data={[
                        { value: 'all', label: 'All statuses' },
                        { value: 'unreviewed', label: 'Unreviewed' },
                        { value: 'retry', label: 'Needs retry' },
                        { value: 'reviewed', label: 'Reviewed' },
                      ]}
                    />
                  </Group>
                  {activeTrack && (
                    <Text size="xs" c="dimmed">
                      {activeTrack.summary}
                    </Text>
                  )}
                  <ProblemList problems={filteredProblems} />
                </Stack>
              )}

              {state.selected ? (
                <ProblemView key={state.selected.id} state={state} problem={state.selected} onOpenGuide={openGuide} />
              ) : (
                <Text c="dimmed" size="sm">
                  Choose a problem to start a design attempt.
                </Text>
              )}
            </Stack>
          </div>
        </Stack>
      </Box>
    </MantineProvider>
  );
}

function ProblemList({ problems }: { problems: ProblemItem[] }) {
  if (problems.length === 0) {
    return (
      <Paper withBorder p="sm">
        <Text size="sm" c="dimmed">
          No problems match the current filters.
        </Text>
      </Paper>
    );
  }
  return (
    <Paper withBorder className="problem-list" role="list" aria-label="Problems">
      {problems.map((problem) => (
        <div key={problem.id} role="listitem">
          <NavLink
            component="button"
            active={problem.selected}
            aria-current={problem.selected ? 'true' : undefined}
            variant="light"
            label={problem.title}
            description={problem.summary}
            onClick={() => send({ type: 'selectProblem', id: problem.id })}
            rightSection={
              <Group gap={4} wrap="nowrap">
                {problem.reviewed && (
                  <Badge
                    color={VERDICT_BADGE_COLORS[problem.verdict] ?? 'gray'}
                    variant="light"
                    size="xs"
                    aria-label={problem.verdict ? `Verdict: ${problem.verdict}` : 'Reviewed (unparsed)'}
                  >
                    {problem.verdict || 'Reviewed'}
                  </Badge>
                )}
                <Badge color={difficultyColor(problem.difficulty)} variant="light" size="xs">
                  {problem.difficulty}
                </Badge>
              </Group>
            }
          />
        </div>
      ))}
    </Paper>
  );
}

function mantineTheme(theme: Theme) {
  return createTheme({
    fontFamily: theme.fontFamily,
    headings: { fontFamily: theme.fontFamily },
    primaryColor: 'blue',
    defaultRadius: 'sm',
  });
}

function variables(theme: Theme): CSSVariablesResolver {
  const colors = {
    '--mantine-color-body': theme.background,
    '--mantine-color-text': theme.foreground,
    '--mantine-color-dimmed': theme.secondary,
    '--mantine-color-default-border': theme.border,
    '--mantine-color-anchor': theme.accent,
  };
  return () => ({ variables: {}, light: colors, dark: colors });
}