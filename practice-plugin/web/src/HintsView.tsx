import { Alert, Button, Group, Paper, Stack, Text, Title } from '@mantine/core';
import { IconBulb, IconLock } from '@tabler/icons-react';
import { send } from './bridge';
import { Markdown } from './common';
import type { Hints } from './types';

export function HintsView({ hints }: { hints: Hints }) {
  if (!hints.exerciseTitle) {
    return (
      <Stack gap="sm" align="flex-start">
        <Text size="sm" c="dimmed">
          Select an exercise to view hints.
        </Text>
      </Stack>
    );
  }

  const allRevealed = hints.revealedCount >= hints.totalCount;
  const buttonLabel = !hints.hasAttempt
    ? 'Reveal Hint'
    : allRevealed
      ? `All ${hints.totalCount} Hints Revealed`
      : `Reveal Hint ${hints.revealedCount + 1} of ${hints.totalCount}`;

  return (
    <Stack gap="md">
      <Group justify="space-between" align="center" wrap="wrap" gap="xs">
        <Group gap="xs" align="center">
          <IconBulb size={18} />
          <Title order={2} size="h3">
            Hints — {hints.exerciseTitle}
          </Title>
        </Group>
        <Text size="xs" c="dimmed">
          {hints.revealedCount} / {hints.totalCount} revealed
        </Text>
      </Group>

      {!hints.hasAttempt ? (
        <Alert icon={<IconLock size={16} />} title="Start an attempt first" variant="light">
          <Text size="sm">Start or resume an attempt to reveal hints.</Text>
        </Alert>
      ) : hints.revealedHints.length === 0 ? (
        <Text size="sm" c="dimmed">
          Hints are optional. Reveal one only when you want help.
        </Text>
      ) : (
        <Stack gap="sm">
          {hints.revealedHints.map((markdown, index) => (
            <Paper key={index} withBorder p="sm" radius="sm">
              <Stack gap="xs">
                <Text size="xs" fw={600} c="dimmed">
                  Hint {index + 1} of {hints.totalCount}
                </Text>
                <Markdown content={markdown} />
              </Stack>
            </Paper>
          ))}
        </Stack>
      )}

      <Group>
        <Button
          size="xs"
          variant={allRevealed ? 'default' : 'light'}
          leftSection={<IconBulb size={14} />}
          disabled={!hints.hasAttempt || allRevealed}
          title={
            !hints.hasAttempt
              ? 'Start or resume an attempt to reveal hints'
              : allRevealed
                ? 'All hints for this exercise have been revealed'
                : undefined
          }
          onClick={() => send({ type: 'revealHint' })}
        >
          {buttonLabel}
        </Button>
      </Group>
    </Stack>
  );
}
