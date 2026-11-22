import { useState } from 'react';
import {
  Alert,
  Badge,
  Button,
  Checkbox,
  Group,
  Loader,
  Paper,
  SegmentedControl,
  Select,
  Stack,
  Text,
  TextInput,
  Title,
  Typography,
} from '@mantine/core';
import { send } from './bridge';
import { FeedbackView } from './FeedbackView';
import { InterviewTimer } from './InterviewTimer';
import type { PageState, SelectedProblem } from './types';

const DIFFICULTY_COLORS: Record<string, string> = { Easy: 'teal', Medium: 'yellow', Hard: 'red' };

const SHARED_SELF_CHECK_ITEMS = [
  'Scoped top 3–4 functional & non-functional targets',
  'Defined core entities and primary/partition keys',
  'Wrote concrete API endpoints, auth & pagination',
] as const;

const MID_LEVEL_SELF_CHECK_ITEMS = [
  ...SHARED_SELF_CHECK_ITEMS,
  'Diagram shows end-to-end request & data flow',
  'Addressed primary bottleneck & basic failure handling',
] as const;

const SENIOR_SELF_CHECK_ITEMS = [
  ...SHARED_SELF_CHECK_ITEMS,
  'Diagram shows end-to-end flow, partitioning & async boundaries',
  'Tied capacity estimates to scaling, consistency & failure tradeoffs',
] as const;

const STAFF_SELF_CHECK_ITEMS = [
  ...SHARED_SELF_CHECK_ITEMS,
  'Justified architecture simplicity vs. scale & blast-radius isolation',
  'Quantified capacity, multi-region/degradation tradeoffs & migration cost',
] as const;

export function selfCheckItemsForLevel(targetLevel: string): readonly string[] {
  if (targetLevel === 'Mid-Level') {
    return MID_LEVEL_SELF_CHECK_ITEMS;
  }
  if (targetLevel === 'Staff+') {
    return STAFF_SELF_CHECK_ITEMS;
  }
  return SENIOR_SELF_CHECK_ITEMS;
}

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

export function ProblemView({
  state,
  problem,
  onOpenGuide,
}: {
  state: PageState;
  problem: SelectedProblem;
  onOpenGuide?: (guideId: string) => void;
}) {
  const attempt = problem.selectedAttempt;
  const currentAttempt = problem.attempts.find((item) => item.id === attempt);
  const [trackedAttempt, setTrackedAttempt] = useState(attempt);
  const [timerResetCount, setTimerResetCount] = useState(0);
  const [promptMode, setPromptMode] = useState<'full' | 'ambiguous'>('full');
  const [scopeRevealed, setScopeRevealed] = useState(false);
  const [showChecklist, setShowChecklist] = useState(false);
  const [checkedItems, setCheckedItems] = useState<Record<string, boolean>>({});
  const [renaming, setRenaming] = useState(false);
  const [draftLabel, setDraftLabel] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(false);

  if (trackedAttempt !== attempt) {
    setTrackedAttempt(attempt);
    if (trackedAttempt !== '') {
      setTimerResetCount((count) => count + 1);
      setScopeRevealed(false);
      setCheckedItems({});
      setRenaming(false);
      setDraftLabel('');
      setConfirmDelete(false);
    }
  }

  const selfCheckItems = selfCheckItemsForLevel(state.targetLevel);
  const relatedGuides = state.guides.filter((guide) => guide.relatedProblemIds.includes(problem.id));
  const hasHintsLeft = problem.hintsHtml.length < problem.hintCount;
  const checkedCount = selfCheckItems.filter((item) => Boolean(checkedItems[item])).length;
  const hideDetailedScope = promptMode === 'ambiguous' && !scopeRevealed;

  const startRename = () => {
    setConfirmDelete(false);
    setDraftLabel(currentAttempt?.customLabel ?? '');
    setRenaming(true);
  };

  const commitRename = () => {
    if (attempt !== '') {
      send({ type: 'renameAttempt', id: attempt, label: draftLabel });
    }
    setRenaming(false);
  };

  return (
    <Stack gap="md">
      <Stack gap={6}>
        <Group justify="space-between" align="center" gap="xs" wrap="wrap">
          <Title order={3}>{problem.title}</Title>
          <SegmentedControl
            size="xs"
            aria-label="Prompt mode"
            value={promptMode}
            onChange={(mode) => {
              setPromptMode(mode as 'full' | 'ambiguous');
              setScopeRevealed(false);
            }}
            data={[
              { value: 'full', label: 'Full prompt' },
              { value: 'ambiguous', label: 'Ambiguous prompt' },
            ]}
          />
        </Group>
        <Group gap={6} align="center" wrap="wrap">
          <Badge color={difficultyColor(problem.difficulty)} variant="light">
            {problem.difficulty}
          </Badge>
          {relatedGuides.length > 0 && onOpenGuide && (
            <Group gap={6} align="center" wrap="wrap">
              <Text size="xs" c="dimmed">
                Related guides:
              </Text>
              {relatedGuides.map((guide) => (
                <Button key={guide.id} size="compact-xs" variant="light" onClick={() => onOpenGuide(guide.id)}>
                  {guide.title}
                </Button>
              ))}
            </Group>
          )}
        </Group>
      </Stack>

      <InterviewTimer key={`${problem.id}:${timerResetCount}`} />

      <Html className="statement" html={hideDetailedScope ? problem.promptHtml : problem.statementHtml} />
      {hideDetailedScope && (
        <Paper withBorder p="xs">
          <Group justify="space-between" align="center" gap="xs" wrap="wrap">
            <Text size="xs" c="dimmed">
              Real interviews start ambiguous—write your clarifying questions &amp; scale assumptions in notes first.
            </Text>
            <Button size="compact-xs" variant="light" onClick={() => setScopeRevealed(true)}>
              Reveal requirements &amp; scale
            </Button>
          </Group>
        </Paper>
      )}

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
        <Group gap="xs" align="end" wrap="wrap">
          {problem.attempts.length > 0 && (
            <Select
              aria-label="Attempt"
              size="xs"
              allowDeselect={false}
              data={problem.attempts.map((item) => ({ value: item.id, label: item.label }))}
              value={attempt === '' ? null : attempt}
              onChange={(value) => {
                setRenaming(false);
                setConfirmDelete(false);
                if (value) {
                  send({ type: 'selectAttempt', id: value });
                }
              }}
            />
          )}
          <Button
            size="xs"
            variant={problem.attempts.length === 0 ? 'filled' : 'default'}
            onClick={() => {
              setRenaming(false);
              setConfirmDelete(false);
              send({ type: 'newAttempt', id: problem.id });
            }}
          >
            New attempt
          </Button>
          {attempt !== '' && (
            <>
              <Button size="xs" variant="default" onClick={() => send({ type: 'openAttempt', id: attempt })}>
                Open diagram and notes
              </Button>
              <Button size="xs" variant="subtle" onClick={startRename}>
                Rename
              </Button>
              <Button
                size="xs"
                variant="subtle"
                color="red"
                disabled={state.running}
                onClick={() => {
                  setRenaming(false);
                  setConfirmDelete(true);
                }}
              >
                Delete
              </Button>
            </>
          )}
        </Group>

        {renaming && attempt !== '' && (
          <Group gap="xs" align="end">
            <TextInput
              aria-label="Attempt name"
              size="xs"
              placeholder="Custom name (leave empty to reset)"
              maxLength={80}
              value={draftLabel}
              onChange={(event) => setDraftLabel(event.currentTarget.value)}
              onKeyDown={(event) => {
                if (event.key === 'Enter') {
                  commitRename();
                } else if (event.key === 'Escape') {
                  setRenaming(false);
                }
              }}
            />
            <Button size="xs" onClick={commitRename}>
              Save name
            </Button>
            <Button size="xs" variant="subtle" onClick={() => setRenaming(false)}>
              Cancel
            </Button>
          </Group>
        )}

        {confirmDelete && attempt !== '' && (
          <Alert color="red" title="Delete this attempt?">
            <Stack gap="xs">
              <Text size="xs">
                Permanently deletes this attempt&apos;s diagram, notes, and saved reviews.
              </Text>
              <Group gap="xs">
                <Button
                  size="compact-xs"
                  color="red"
                  onClick={() => {
                    setConfirmDelete(false);
                    send({ type: 'deleteAttempt', id: attempt });
                  }}
                >
                  Confirm delete
                </Button>
                <Button size="compact-xs" variant="subtle" onClick={() => setConfirmDelete(false)}>
                  Keep attempt
                </Button>
              </Group>
            </Stack>
          </Alert>
        )}
      </Stack>

      <Stack gap="xs">
        <Group justify="space-between" align="center">
          <SectionLabel>{`Pre-review self-check (${checkedCount}/${selfCheckItems.length})`}</SectionLabel>
          <Button size="compact-xs" variant="subtle" onClick={() => setShowChecklist((open) => !open)}>
            {showChecklist ? 'Hide checklist' : 'Show checklist'}
          </Button>
        </Group>
        {showChecklist && (
          <Paper withBorder p="xs">
            <Stack gap={6}>
              {selfCheckItems.map((item) => (
                <Checkbox
                  key={item}
                  size="xs"
                  label={item}
                  checked={Boolean(checkedItems[item])}
                  onChange={(event) => {
                    const checked = event.currentTarget.checked;
                    setCheckedItems((prev) => ({ ...prev, [item]: checked }));
                  }}
                />
              ))}
            </Stack>
          </Paper>
        )}
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
            {`Get feedback (${state.targetLevel})`}
          </Button>
        )}
        <Button size="xs" variant="subtle" onClick={() => send({ type: 'openSettings' })}>
          Interviewer settings
        </Button>
      </Group>

      {state.error !== '' && (
        <Alert color="red" title="Something went wrong" style={{ whiteSpace: 'pre-wrap' }}>
          {state.error}
        </Alert>
      )}

      {state.reviews.length > 0 && (
        <FeedbackView
          key={`${attempt}:${state.reviews.length}:${state.reviews[0]?.createdAt ?? ''}`}
          attemptId={attempt}
          running={state.running}
          reviews={state.reviews}
          dimensionSummary={state.dimensionSummary}
          onOpenGuide={onOpenGuide}
        />
      )}
    </Stack>
  );
}