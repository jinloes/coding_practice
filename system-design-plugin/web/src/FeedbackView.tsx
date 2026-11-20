import { useState } from 'react';
import { Alert, Badge, Button, Code, Group, List, Paper, Select, Stack, Table, Text, Textarea } from '@mantine/core';
import { send } from './bridge';
import { SectionLabel } from './ProblemView';
import type { Feedback } from './types';

const VERDICT_COLORS: Record<string, string> = {
  'Strong hire': 'teal',
  Hire: 'green',
  'Lean hire': 'lime',
  'Lean no hire': 'orange',
  'No hire': 'red',
};

function formatTimestamp(createdAt: string): string {
  if (createdAt === '') {
    return '';
  }
  const parsed = Date.parse(createdAt);
  if (!Number.isFinite(parsed)) {
    return createdAt;
  }
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(parsed));
}

/** Interviewer feedback. All text is model output or plain learner text and is never inserted as HTML. */
export function FeedbackView({
  attemptId,
  running,
  reviews,
}: {
  attemptId: string;
  running: boolean;
  reviews: Feedback[];
}) {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [followUpDraft, setFollowUpDraft] = useState('');
  if (reviews.length === 0) {
    return null;
  }
  const index = selectedIndex < reviews.length ? selectedIndex : 0;
  const feedback = reviews[index];
  const formattedDate = formatTimestamp(feedback.createdAt);
  const canProbe = index === 0 && attemptId !== '' && feedback.parsed && feedback.followUpQuestions.length > 0;

  return (
    <Paper withBorder p="sm">
      <Stack gap="sm">
        <Group gap="xs" justify="space-between" wrap="wrap">
          <Group gap={6}>
            <SectionLabel>Interviewer feedback</SectionLabel>
            {feedback.level !== '' && (
              <Badge size="xs" variant="outline">
                {feedback.level}
              </Badge>
            )}
            {feedback.round === 'follow-up' && (
              <Badge size="xs" variant="light" color="violet">
                Follow-up probe
              </Badge>
            )}
          </Group>
          <Text size="xs" c="dimmed">
            {[feedback.model, formattedDate].filter((part) => part !== '').join(' · ')}
          </Text>
        </Group>
        {reviews.length > 1 && (
          <Select
            aria-label="Review history"
            size="xs"
            allowDeselect={false}
            data={reviews.map((item, i) => ({
              value: String(i),
              label: `Review ${reviews.length - i}${i === 0 ? ' (latest)' : ''}${
                item.round === 'follow-up' ? ' · Follow-up' : ''
              }${item.verdict ? ` — ${item.verdict}` : ''}`,
            }))}
            value={String(index)}
            onChange={(value) => value !== null && setSelectedIndex(Number(value))}
          />
        )}
        {index === 0 && feedback.stale && (
          <Alert color="yellow" title="Out of date">
            Your diagram or notes changed after this review. Ask for feedback again to grade the latest version.
          </Alert>
        )}
        {index > 0 && (
          <Text size="xs" c="dimmed">
            Viewing an earlier review of this attempt.
          </Text>
        )}
        {feedback.followUpAnswers !== '' && (
          <Stack gap={4}>
            <SectionLabel>Candidate follow-up answers</SectionLabel>
            <Paper withBorder p="xs">
              <Text size="xs" style={{ whiteSpace: 'pre-wrap' }}>
                {feedback.followUpAnswers}
              </Text>
            </Paper>
          </Stack>
        )}
        {feedback.parsed ? <Structured feedback={feedback} /> : <Raw raw={feedback.raw} />}
        {canProbe && (
          <Stack gap="xs">
            <SectionLabel>Interactive follow-up round</SectionLabel>
            <Text size="xs" c="dimmed">
              Defend your tradeoffs or answer the follow-up questions above (optionally update your diagram or notes first), then submit for a probed debrief.
            </Text>
            <Textarea
              aria-label="Follow-up answers"
              size="xs"
              rows={3}
              placeholder="Write your answers to the interviewer's follow-up questions…"
              value={followUpDraft}
              disabled={running}
              onChange={(event) => setFollowUpDraft(event.currentTarget.value)}
            />
            <Group gap="xs">
              <Button
                size="xs"
                variant="light"
                disabled={running || followUpDraft.trim() === ''}
                onClick={() => send({ type: 'submitFollowUp', id: attemptId, answers: followUpDraft })}
              >
                Submit follow-up answers
              </Button>
            </Group>
          </Stack>
        )}
      </Stack>
    </Paper>
  );
}

function Structured({ feedback }: { feedback: Feedback }) {
  return (
    <Stack gap="sm">
      <Group gap="xs">
        <Badge size="lg" color={VERDICT_COLORS[feedback.verdict] ?? 'gray'}>
          {feedback.verdict}
        </Badge>
      </Group>
      <Text size="sm" style={{ whiteSpace: 'pre-wrap' }}>
        {feedback.summary}
      </Text>
      <Table withTableBorder verticalSpacing={6} fz="sm">
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Dimension</Table.Th>
            <Table.Th>Score</Table.Th>
            <Table.Th>Strengths and gaps</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {feedback.dimensions.map((dimension) => (
            <Table.Tr key={dimension.name}>
              <Table.Td>{dimension.name}</Table.Td>
              <Table.Td>
                <Badge variant="light" color={dimension.score >= 3 ? 'teal' : dimension.score === 2 ? 'yellow' : 'red'}>
                  {dimension.score} / 4
                </Badge>
              </Table.Td>
              <Table.Td>
                <Items items={dimension.strengths} prefix="+" />
                <Items items={dimension.gaps} prefix="−" />
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
      <Bullets label="Follow-up questions" items={feedback.followUpQuestions} />
      <Bullets label="Missed expectations" items={feedback.missedExpectations} />
    </Stack>
  );
}

function Items({ items, prefix }: { items: string[]; prefix: string }) {
  return (
    <>
      {items.map((item, index) => (
        <Text key={index} size="sm">
          {prefix} {item}
        </Text>
      ))}
    </>
  );
}

function Bullets({ label, items }: { label: string; items: string[] }) {
  if (items.length === 0) {
    return null;
  }
  return (
    <Stack gap={4}>
      <SectionLabel>{label}</SectionLabel>
      <List size="sm" spacing={2}>
        {items.map((item, index) => (
          <List.Item key={index}>{item}</List.Item>
        ))}
      </List>
    </Stack>
  );
}

function Raw({ raw }: { raw: string }) {
  return (
    <Stack gap="xs">
      <Text size="sm" c="dimmed">
        The interviewer's reply was not in the expected format, so it is shown as returned.
      </Text>
      <Code block style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
        {raw}
      </Code>
    </Stack>
  );
}