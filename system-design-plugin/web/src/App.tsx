import { useEffect, useMemo, useSyncExternalStore } from 'react';
import {
  Badge,
  Box,
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
import { difficultyColor, ProblemView } from './ProblemView';
import type { ProblemItem, Theme } from './types';


export function App() {
  const state = useSyncExternalStore(subscribe, snapshot);
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
  return (
    <MantineProvider theme={mantine} cssVariablesResolver={resolver} forceColorScheme={theme.dark ? 'dark' : 'light'}>
      <Box p="md">
        <Stack gap="md">
          <ProblemList problems={state.problems} />
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
        <NavLink
          key={problem.id}
          role="listitem"
          active={problem.selected}
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