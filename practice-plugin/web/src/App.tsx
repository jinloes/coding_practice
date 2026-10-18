import { useEffect, useMemo, useSyncExternalStore } from 'react';
import { Box, createTheme, MantineProvider, type CSSVariablesResolver } from '@mantine/core';
import { snapshot, subscribe } from './bridge';
import { ChooserView, GuideView } from './GuideView';
import { ProblemView } from './ProblemView';
import type { Page, Theme } from './types';

export function App() {
  const state = useSyncExternalStore(subscribe, snapshot);
  const theme = state?.theme;
  const mantine = useMemo(() => (theme ? mantineTheme(theme) : undefined), [theme]);
  const resolver = useMemo(() => (theme ? variables(theme) : undefined), [theme]);

  useEffect(() => {
    if (theme) {
      // Rem-based Mantine sizes follow the IDE label font size.
      document.documentElement.style.fontSize = `${theme.fontSize}px`;
    }
  }, [theme]);

  // Refreshes of the same page (progress, theme) keep the scroll position; a different page starts at the top.
  const identity = state ? pageIdentity(state.page) : '';
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [identity]);

  if (!state || !theme) {
    return null;
  }
  return (
    <MantineProvider theme={mantine} cssVariablesResolver={resolver} forceColorScheme={theme.dark ? 'dark' : 'light'}>
      <Box p="md">
        <PageView page={state.page} />
      </Box>
    </MantineProvider>
  );
}

function PageView({ page }: { page: Page }) {
  switch (page.kind) {
    case 'problem':
      return <ProblemView problem={page.problem} />;
    case 'guide':
      return <GuideView guide={page.guide} />;
    case 'chooser':
      return <ChooserView selected={page.selected} />;
  }
}

function pageIdentity(page: Page): string {
  switch (page.kind) {
    case 'problem':
      return `problem:${page.problem?.id ?? ''}`;
    case 'guide':
      return `guide:${page.guide.id}`;
    case 'chooser':
      return `chooser:${page.selected?.title ?? ''}`;
  }
}

function mantineTheme(theme: Theme) {
  return createTheme({
    fontFamily: theme.fontFamily,
    fontFamilyMonospace: theme.codeFontFamily,
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
