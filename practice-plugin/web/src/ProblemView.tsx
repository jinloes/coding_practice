import { Accordion, Badge, Code, Group, Paper, Stack, Text, ThemeIcon, Title, Tooltip } from '@mantine/core';
import { IconCheck } from '@tabler/icons-react';
import { DifficultyBadge, Html, Memberships, SectionLabel } from './common';
import type { Problem } from './types';

export function ProblemView({ problem }: { problem: Problem | null }) {
  if (!problem) {
    return <Text c="dimmed">No exercises match these filters.</Text>;
  }
  return (
    <Stack gap="md">
      <Stack gap={6}>
        <Group gap="xs" wrap="nowrap">
          <Title order={3}>{problem.title}</Title>
          {problem.passed && (
            <Tooltip label="Passed a full check before">
              <ThemeIcon color="teal" size="sm" radius="xl" aria-label="Passed">
                <IconCheck size={14} />
              </ThemeIcon>
            </Tooltip>
          )}
        </Group>
        <Group gap={6}>
          <DifficultyBadge difficulty={problem.difficulty} />
          <Badge color="gray" variant="light" style={{ textTransform: 'none' }}>
            {problem.topic}
          </Badge>
        </Group>
        <Text size="sm" c="dimmed">
          Target: {problem.intendedTime} time, {problem.intendedSpace} space
        </Text>
      </Stack>

      <Html html={problem.statementHtml} />

      <Stack gap="xs">
        <SectionLabel>Visible examples</SectionLabel>
        {problem.examples.map((example, index) => (
          <Paper key={index} withBorder p="sm">
            <Stack gap={6}>
              <Text size="sm" fw={600}>
                Example {index + 1}
              </Text>
              <Field label="Input" value={example.input} />
              <Field label="Output" value={example.output} />
            </Stack>
          </Paper>
        ))}
      </Stack>

      <Accordion variant="contained">
        <Accordion.Item value="input">
          <Accordion.Control>Custom input format</Accordion.Control>
          <Accordion.Panel>
            <Code block>{problem.inputSyntax}</Code>
          </Accordion.Panel>
        </Accordion.Item>
      </Accordion>

      <Memberships guides={problem.guides} tracks={problem.tracks} />
    </Stack>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <Text size="xs" c="dimmed">
        {label}
      </Text>
      <Code block style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
        {value}
      </Code>
    </div>
  );
}
