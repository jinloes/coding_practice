import { Alert, Badge, Button, Group, Loader, Select, Stack, Text, Title, Typography } from '@mantine/core';
import { send } from './bridge';
import { FeedbackView } from './FeedbackView';
import type { PageState, SelectedProblem } from './types';

const DIFFICULTY_COLORS: Record<string, string> = { Easy: 'teal', Medium: 'yellow', Hard: 'red' };

export function difficultyColor(difficulty: string): string {
  return DIFFICULTY_COLORS[difficulty] ?? 'gray';
}

/** Renders HTML the plugin already escaped with MarkdownHtml. */
export function Html({ html, className }: { html: string; className?: string }) {
  return (
    <Typography className={className}>
      <div dangerouslySetInnerHTML={{ __html: html }} />
    </Typography>
  );
}

export function SectionLabel({ children }: { children: string }) {
  return (
    <Text size="xs" fw={700} tt="uppercase" c="dimmed">
      {children}
    </Text>
  );
}

export function ProblemView({ state, problem }: { state: PageState; problem: SelectedProblem }) {
  const attempt = problem.selectedAttempt;
  const hasHintsLeft = problem.hintsHtml.length < problem.hintCount;
  return (
    <Stack gap="md">
      <Stack gap={6}>
        <Title order={3}>{problem.title}</Title>
        <Group gap={6}>
          <Badge color={difficultyColor(problem.difficulty)} variant="light">
            {problem.difficulty}
          </Badge>
        </Group>
      </Stack>

      <Html className="statement" html={problem.statementHtml} />

      <Stack gap="xs">
        <SectionLabel>Hints</SectionLabel>
        {problem.hintsHtml.map((hint, index) => (
          <Text key={index} size="sm" component="div">
            <b>Hint {index + 1}:</b> <span dangerouslySetInnerHTML={{ __html: hint }} />
          </Text>
        ))}
        {hasHintsLeft && attempt !== '' && (
          <Button variant="subtle" size="compact-sm" w="fit-content" onClick={() => send({ type: 'revealHint', id: attempt })}>
            Reveal hint ({problem.hintsHtml.length + 1} of {problem.hintCount})
          </Button>
        )}
        {hasHintsLeft && attempt === '' && (
          <Text size="sm" c="dimmed">
            Start an attempt to reveal hints.
          </Text>
        )}
      </Stack>

      <Stack gap="xs">
        <SectionLabel>Your attempt</SectionLabel>
        <Group gap="xs" align="end">
          {problem.attempts.length > 0 && (
            <Select
              aria-label="Attempt"
              size="xs"
              allowDeselect={false}
              data={problem.attempts.map((item) => ({ value: item.id, label: item.label }))}
              value={attempt === '' ? null : attempt}
              onChange={(value) => value && send({ type: 'selectAttempt', id: value })}
            />
          )}
          <Button size="xs" variant="default" onClick={() => send({ type: 'newAttempt', id: problem.id })}>
            New attempt
          </Button>
          {attempt !== '' && (
            <Button size="xs" variant="default" onClick={() => send({ type: 'openAttempt', id: attempt })}>
              Open diagram and notes
            </Button>
          )}
        </Group>
      </Stack>

      <Group gap="xs">
        {state.running ? (
          <>
            <Loader size="xs" />
            <Text size="sm">The interviewer is reviewing your design…</Text>
            <Button size="xs" variant="default" onClick={() => send({ type: 'cancelFeedback' })}>
              Cancel
            </Button>
          </>
        ) : (
          <Button size="xs" disabled={attempt === ''} onClick={() => send({ type: 'requestFeedback', id: attempt })}>
            Get feedback
          </Button>
        )}
        <Button size="xs" variant="subtle" onClick={() => send({ type: 'openSettings' })}>
          Interviewer settings
        </Button>
      </Group>

      {state.error !== '' && (
        <Alert color="red" title="Feedback failed" style={{ whiteSpace: 'pre-wrap' }}>
          {state.error}
        </Alert>
      )}

      {state.feedback && <FeedbackView feedback={state.feedback} stale={state.stale} />}
    </Stack>
  );
}