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
  Stack,
  Text,
  type CSSVariablesResolver,
} from '@mantine/core';
import { send, snapshot, subscribe } from './bridge';
import { difficultyColor, ProblemView, SectionLabel } from './ProblemView';
import type { ProblemItem, Theme } from './types';


export function App() {
  const state = useSyncExternalStore(subscribe, snapshot);
  const [listCollapsed, setListCollapsed] = useState(false);
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
  }, [selectedId]);

  if (!state || !theme) {
    return null;
  }
  const showProblemList = !state.selected || !listCollapsed;
  return (
    <MantineProvider theme={mantine} cssVariablesResolver={resolver} forceColorScheme={theme.dark ? 'dark' : 'light'}>
      <Box p="md">
        <Stack gap="md">
          {state.selected && (
            <Group justify="space-between" align="center">
              <SectionLabel>Problems</SectionLabel>
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
          {showProblemList && <ProblemList problems={state.problems} />}
          {state.selected ? (
            <ProblemView state={state} problem={state.selected} />
          ) : (
            <Text c="dimmed" size="sm">
              Choose a problem to start a design attempt.
            </Text>
          )}
        </Stack>
      </Box>
    </MantineProvider>
  );
}

function ProblemList({ problems }: { problems: ProblemItem[] }) {
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
                  <Badge color="teal" variant="light" size="xs" aria-label="Reviewed">
                    Reviewed
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