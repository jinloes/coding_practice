import { useState } from 'react';
import {
  Alert,
  Badge,
  Button,
  Group,
  Loader,
  Paper,
  Stack,
  Text,
  Textarea,
  Title,
} from '@mantine/core';
import { send } from './bridge';
import { Markdown } from './common';
import type { Coach } from './types';

const MAX_FOLLOW_UP_CHARS = 2000;

export function CoachView({ coach }: { coach: Coach }) {
  const [draft, setDraft] = useState('');
  const canAsk = coach.unlocked && !coach.running;
  const trimmedDraft = draft.trim();

  function submitFollowUp() {
    if (!canAsk || trimmedDraft.length === 0) {
      return;
    }
    send({ type: 'coachFollowUp', text: trimmedDraft });
    setDraft('');
  }

  return (
    <Stack gap="md">
      <Group justify="space-between" align="flex-start" wrap="wrap" gap="xs">
        <Stack gap={4}>
          <Title order={2}>
            {coach.exerciseTitle ? `AI Coach — ${coach.exerciseTitle}` : 'AI Coach'}
          </Title>
          <Group gap="xs" wrap="wrap">
            {coach.checkSummary && (
              <Badge variant="light" color="blue">
                {coach.checkSummary}
              </Badge>
            )}
            <Badge variant="outline" color="gray">
              Model: {coach.modelLabel || 'CLI default'}
            </Badge>
          </Group>
        </Stack>
        <Group gap="xs">
          <Button
            size="xs"
            variant="default"
            onClick={() => send({ type: 'coachSettings' })}
          >
            Settings
          </Button>
          <Button
            size="xs"
            variant="subtle"
            color="gray"
            disabled={!coach.unlocked || (coach.turns.length === 0 && !coach.running)}
            onClick={() => send({ type: 'coachClear' })}
          >
            Clear
          </Button>
        </Group>
      </Group>

      {!coach.unlocked && (
        <Alert color="blue" title="AI Coach is locked">
          {coach.lockReason || 'Run Check on this attempt at least once to unlock the AI Coach.'}
        </Alert>
      )}

      {coach.unlocked && coach.stale && (
        <Alert color="yellow" title="Edited since last check">
          Your solution changed after the last check. AI Coach will use your current editor code
          alongside the last check result.
        </Alert>
      )}

      <Stack gap={6}>
        <Group gap="xs" wrap="wrap">
          <Button
            size="xs"
            variant="filled"
            disabled={!canAsk}
            onClick={() => send({ type: 'coachDiagnose' })}
          >
            Diagnose my attempt
          </Button>
          <Button
            size="xs"
            variant="light"
            disabled={!canAsk}
            onClick={() => send({ type: 'coachTranslate' })}
          >
            Translate my approach
          </Button>
          <Button
            size="xs"
            variant="light"
            disabled={!canAsk}
            onClick={() => send({ type: 'coachCompare' })}
          >
            Compare with optimal
          </Button>
          {coach.running && (
            <Button
              size="xs"
              variant="outline"
              color="red"
              onClick={() => send({ type: 'coachCancel' })}
            >
              Cancel
            </Button>
          )}
        </Group>
        <Text size="xs" c="dimmed">
          Compare with optimal describes a model-inferred optimal approach from the problem statement
          and complexity targets, not a bundled reference solution.
        </Text>
      </Stack>

      {coach.running && (
        <Group gap="xs">
          <Loader size="xs" />
          <Text size="sm" c="dimmed">
            AI Coach is analyzing your attempt...
          </Text>
        </Group>
      )}

      {coach.error && (
        <Alert color="red" title="Coach error">
          {coach.error}
        </Alert>
      )}

      {coach.turns.length === 0 ? (
        <Text size="sm" c="dimmed">
          {coach.unlocked
            ? 'Choose a coaching action above or ask a follow-up question about your checked attempt.'
            : 'Once you run Check, you can diagnose failures, translate your approach step-by-step, or compare with an optimal solution.'}
        </Text>
      ) : (
        <Stack gap="sm">
          {coach.turns.map((turn, index) => {
            const isAssistant = turn.role === 'assistant';
            return (
              <Paper key={`${turn.createdAt}-${index}`} withBorder p="sm" radius="sm">
                <Text size="xs" fw={600} c={isAssistant ? 'blue' : 'dimmed'} mb={6}>
                  {isAssistant ? 'AI Coach' : 'You'}
                </Text>
                <Markdown content={turn.markdown} />
              </Paper>
            );
          })}
        </Stack>
      )}

      <Stack gap="xs">
        <Textarea
          aria-label="Follow-up question"
          placeholder={
            coach.unlocked
              ? 'Ask a follow-up question (e.g., how do I turn my memoized recursion into a bottom-up loop?)'
              : 'Run Check first to ask follow-up questions'
          }
          rows={3}
          maxLength={MAX_FOLLOW_UP_CHARS}
          disabled={!canAsk}
          value={draft}
          onChange={(event) => setDraft(event.currentTarget.value)}
          onKeyDown={(event) => {
            if ((event.metaKey || event.ctrlKey) && event.key === 'Enter') {
              event.preventDefault();
              submitFollowUp();
            }
          }}
        />
        <Group justify="space-between" align="center">
          <Text size="xs" c="dimmed">
            {draft.length}/{MAX_FOLLOW_UP_CHARS}
          </Text>
          <Button
            size="xs"
            disabled={!canAsk || trimmedDraft.length === 0}
            onClick={submitFollowUp}
          >
            Ask
          </Button>
        </Group>
      </Stack>
    </Stack>
  );
}
