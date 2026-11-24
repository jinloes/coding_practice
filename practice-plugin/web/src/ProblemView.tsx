import { useEffect, useRef, useState } from 'react';
import { Accordion, Badge, Button, Code, Group, Paper, Stack, Text, ThemeIcon, Title, Tooltip } from '@mantine/core';
import { IconCheck } from '@tabler/icons-react';
import { send } from './bridge';
import { DifficultyBadge, Markdown, Memberships, SectionLabel } from './common';
import type { Problem } from './types';

export function ProblemView({ problem }: { problem: Problem | null }) {
  const [copied, setCopied] = useState(false);
  const copyTimer = useRef<number | null>(null);

  useEffect(() => () => {
    if (copyTimer.current !== null) {
      window.clearTimeout(copyTimer.current);
    }
  }, []);

  if (!problem) {
    return (
      <Stack gap="xs" align="flex-start">
        <Text c="dimmed">No exercises match these filters.</Text>
        <Button variant="light" size="xs" onClick={() => send({ type: 'clearFilters' })}>
          Reset filters
        </Button>
      </Stack>
    );
  }

  const runDisabledReason = problem.hasAttempt
    ? 'Wait for the active run to finish'
    : 'Start an attempt to run examples';

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
          <Badge color="gray" variant="light" size="sm" style={{ textTransform: 'none' }}>
            {problem.topic}
          </Badge>
        </Group>
        <Text size="sm" c="dimmed">
          Target: {problem.intendedTime} time, {problem.intendedSpace} space
        </Text>
        <Group gap="xs" mt={4}>
          <Button size="sm" onClick={() => send({ type: 'startOrOpen' })}>
            {problem.hasAttempt ? 'Open Solution.java' : 'Start Attempt'}
          </Button>
          <Tooltip label={runDisabledReason} disabled={problem.canRunExamples}>
            <span style={{ display: 'inline-block' }}>
              <Button
                size="sm"
                variant="default"
                disabled={!problem.canRunExamples}
                onClick={() => send({ type: 'runExamples' })}
              >
                Run Examples
              </Button>
            </span>
          </Tooltip>
        </Group>
      </Stack>

      <Markdown content={problem.statement} />

      <Stack gap="xs">
        <SectionLabel>Visible examples</SectionLabel>
        {problem.examples.map((example, index) => (
          <Paper key={`${problem.id}-example-${index}`} withBorder p="sm">
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

      <Accordion variant="contained" transitionDuration={0}>
        <Accordion.Item value="input">
          <Accordion.Control>Custom input format</Accordion.Control>
          <Accordion.Panel>
            <Stack gap="xs">
              <Field label="Format" value={problem.inputSyntax} />
              <Field label="Sample input" value={problem.sampleInput} />
              <Group gap="xs">
                <Button
                  size="sm"
                  variant="default"
                  onClick={() => {
                    void copyText(problem.sampleInput).then((ok) => {
                      if (!ok) {
                        return;
                      }
                      setCopied(true);
                      if (copyTimer.current !== null) {
                        window.clearTimeout(copyTimer.current);
                      }
                      copyTimer.current = window.setTimeout(() => {
                        setCopied(false);
                        copyTimer.current = null;
                      }, 2000);
                    });
                  }}
                >
                  Copy sample input
                </Button>
                <Tooltip label={runDisabledReason} disabled={problem.canRunExamples}>
                  <span style={{ display: 'inline-block' }}>
                    <Button
                      size="sm"
                      variant="light"
                      disabled={!problem.canRunExamples}
                      onClick={() => send({ type: 'runSample' })}
                    >
                      Run sample
                    </Button>
                  </span>
                </Tooltip>
                {copied && (
                  <Text size="sm" c="teal">
                    Copied
                  </Text>
                )}
              </Group>
            </Stack>
          </Accordion.Panel>
        </Accordion.Item>
      </Accordion>

      <Memberships guides={problem.guides} tracks={problem.tracks} />
    </Stack>
  );
}

async function copyText(value: string): Promise<boolean> {
  const clipboard = navigator.clipboard;
  if (clipboard && typeof clipboard.writeText === 'function') {
    try {
      await clipboard.writeText(value);
      return true;
    } catch {
      return fallbackCopy(value);
    }
  }
  return fallbackCopy(value);
}

function fallbackCopy(value: string): boolean {
  const area = document.createElement('textarea');
  area.value = value;
  area.setAttribute('readonly', 'true');
  area.style.position = 'fixed';
  area.style.opacity = '0';
  document.body.appendChild(area);
  area.select();
  try {
    return document.execCommand?.('copy') === true;
  } catch {
    return false;
  } finally {
    document.body.removeChild(area);
  }
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <Text size="sm" c="dimmed">
        {label}
      </Text>
      <Code block style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
        {value}
      </Code>
    </div>
  );
}
