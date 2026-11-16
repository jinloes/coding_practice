import { useState } from 'react';
import { Alert, Badge, Code, Group, List, Paper, Select, Stack, Table, Text } from '@mantine/core';
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

/** Interviewer feedback. All text is model output and is rendered as plain text, never as HTML. */
export function FeedbackView({ reviews }: { reviews: Feedback[] }) {
  const [selectedIndex, setSelectedIndex] = useState(0);
  if (reviews.length === 0) {
    return null;
  }
  const index = selectedIndex < reviews.length ? selectedIndex : 0;
  const feedback = reviews[index];
  const formattedDate = formatTimestamp(feedback.createdAt);

  return (
    <Paper withBorder p="sm">
      <Stack gap="sm">
        <Group gap="xs" justify="space-between">
          <SectionLabel>Interviewer feedback</SectionLabel>
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
              label: `Review ${reviews.length - i}${i === 0 ? ' (latest)' : ''}${item.verdict ? ` — ${item.verdict}` : ''}`,
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
        {feedback.parsed ? <Structured feedback={feedback} /> : <Raw raw={feedback.raw} />}
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