import {
  Alert,
  Badge,
  Box,
  Button,
  Code,
  Group,
  Loader,
  Paper,
  SimpleGrid,
  Stack,
  Text,
  Title,
} from '@mantine/core';
import {
  IconAlertTriangle,
  IconCircleCheck,
  IconCircleX,
  IconClock,
  IconInfoCircle,
  IconPlayerPlay,
  IconSparkles,
} from '@tabler/icons-react';
import { send } from './bridge';
import type { Results } from './types';

type ScalingSample = {
  n: number;
  timeText: string;
  timeNanos: number;
  allocated: string;
};

type ComplexityMetric = {
  measured: string;
  note: string;
  status: 'match' | 'mismatch' | 'inconclusive' | 'neutral';
};

type ParsedScaling = {
  unit: string;
  samples: ScalingSample[];
  time?: ComplexityMetric;
  space?: ComplexityMetric;
  notes: string[];
};

type ParsedFailure = {
  heading: string;
  input?: string;
  expected?: string;
  actual?: string;
  error?: string;
  location?: string;
  extraLines: string[];
};

type ParsedOutput = {
  summaryLine: string;
  scaling: ParsedScaling | null;
  failure: ParsedFailure | null;
  unstructured: string;
};

export function ResultsView({ results }: { results: Results }) {
  const parsed = parseOutput(results.output);
  const accentColor = toneColor(results.tone);
  const isEmpty = !results.banner && !results.output.trim();
  const isNotChecked = results.tone === 'idle' && !results.output.trim();

  if (isEmpty) {
    return (
      <Stack gap="sm" align="flex-start" maw={720}>
        <Text size="sm" c="dimmed">
          Select an exercise to view check results.
        </Text>
      </Stack>
    );
  }

  const hasStructured = parsed.scaling !== null || parsed.failure !== null;

  return (
    <Stack gap="md" maw={760}>
      <Paper
        withBorder
        p="md"
        radius="sm"
        className="results-hero"
        style={{ borderLeftColor: accentColor }}
      >
        <Group justify="space-between" align="flex-start" wrap="wrap" gap="sm">
          <Group gap="sm" align="flex-start" wrap="nowrap" style={{ flex: '1 1 240px', minWidth: 0 }}>
            <Box mt={2} style={{ color: accentColor, flexShrink: 0 }}>
              <ToneIcon tone={results.tone} />
            </Box>
            <Stack gap={4} style={{ minWidth: 0 }}>
              <Title order={2} size="h3">
                {results.banner || 'Results'}
              </Title>
              {parsed.summaryLine && (
                <Text size="sm" c="dimmed">
                  {parsed.summaryLine}
                </Text>
              )}
              {(results.checkedAt || results.lastPassedAt) && (
                <Group gap="md" wrap="wrap" mt={2}>
                  <Timestamp label="Last check" value={results.checkedAt} />
                  <Timestamp label="Last pass" value={results.lastPassedAt} />
                </Group>
              )}
            </Stack>
          </Group>

          <Button
            size="xs"
            variant={results.askCoachEnabled ? 'light' : 'default'}
            leftSection={<IconSparkles size={14} />}
            disabled={!results.askCoachEnabled}
            title={
              results.askCoachEnabled
                ? 'Open the post-check AI Coach for this attempt'
                : 'Run Check on this attempt to unlock the AI Coach'
            }
            onClick={() => send({ type: 'openCoach' })}
          >
            Ask AI Coach
          </Button>
        </Group>
      </Paper>

      {results.stale && (
        <Alert
          color="yellow"
          variant="light"
          icon={<IconAlertTriangle size={16} />}
          title="Result may be out of date"
        >
          <Text size="sm">
            {results.staleReason || 'Solution was edited after this result; run or check again.'}
          </Text>
        </Alert>
      )}

      {isNotChecked && (
        <Paper withBorder p="md" radius="sm">
          <Text size="sm" c="dimmed">
            No check results yet. Use <strong>Run Examples</strong> for fast feedback on visible
            cases, or <strong>Check Solution</strong> for full verification and complexity scaling.
          </Text>
        </Paper>
      )}

      {parsed.scaling && <ScalingSection scaling={parsed.scaling} />}

      {parsed.failure && <FailureSection failure={parsed.failure} />}

      {!hasStructured && parsed.unstructured && (
        <Paper withBorder p="sm" radius="sm">
          <Code block>{parsed.unstructured}</Code>
        </Paper>
      )}

      {hasStructured && results.output.trim() && (
        <Box>
          <details>
            <summary className="results-raw-toggle">Raw output</summary>
            <Box mt="xs">
              <Code block>{results.output.trim()}</Code>
            </Box>
          </details>
        </Box>
      )}
    </Stack>
  );
}

function ScalingSection({ scaling }: { scaling: ParsedScaling }) {
  const maxNanos = scaling.samples.reduce((max, s) => Math.max(max, s.timeNanos), 0);

  return (
    <Stack gap="sm">
      {(scaling.time || scaling.space) && (
        <SimpleGrid cols={{ base: 1, xs: 2 }} spacing="sm">
          {scaling.time && <MetricCard label="Time (measured)" metric={scaling.time} />}
          {scaling.space && <MetricCard label="Space (measured)" metric={scaling.space} />}
        </SimpleGrid>
      )}

      {scaling.samples.length > 0 && (
        <Paper withBorder p="sm" radius="sm">
          <Stack gap="xs">
            <Group justify="space-between" align="baseline" wrap="wrap" gap="xs">
              <Text size="sm" fw={600}>
                Measured scaling
              </Text>
              <Text size="xs" c="dimmed">
                per {scaling.unit}
              </Text>
            </Group>

            <table className="results-scaling-table">
              <thead>
                <tr>
                  <th style={{ textAlign: 'right' }}>n</th>
                  <th style={{ textAlign: 'right' }}>time</th>
                  <th aria-label="relative time bar" />
                  <th style={{ textAlign: 'right' }}>allocated</th>
                </tr>
              </thead>
              <tbody>
                {scaling.samples.map((sample) => {
                  const pct = maxNanos > 0 ? Math.max(4, Math.round((sample.timeNanos / maxNanos) * 100)) : 4;
                  return (
                    <tr key={sample.n}>
                      <td className="results-scaling-num">{sample.n.toLocaleString()}</td>
                      <td className="results-scaling-num">{sample.timeText}</td>
                      <td className="results-scaling-bar-cell">
                        <div className="results-scaling-track">
                          <div className="results-scaling-fill" style={{ width: `${pct}%` }} />
                        </div>
                      </td>
                      <td className="results-scaling-num">{sample.allocated}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>

            {scaling.notes.map((note, idx) => (
              <Text key={idx} size="xs" c="dimmed">
                {note}
              </Text>
            ))}
          </Stack>
        </Paper>
      )}
    </Stack>
  );
}

function MetricCard({ label, metric }: { label: string; metric: ComplexityMetric }) {
  const badgeColor =
    metric.status === 'match'
      ? 'teal'
      : metric.status === 'mismatch'
        ? 'yellow'
        : 'gray';

  return (
    <Paper withBorder p="sm" radius="sm">
      <Stack gap={6}>
        <Text size="xs" fw={600} c="dimmed" tt="uppercase">
          {label}
        </Text>
        <Text fw={600} ff="monospace" size="md">
          {metric.measured}
        </Text>
        {metric.note && (
          <Box>
            <Badge size="sm" variant="light" color={badgeColor}>
              {metric.note}
            </Badge>
          </Box>
        )}
      </Stack>
    </Paper>
  );
}

function FailureSection({ failure }: { failure: ParsedFailure }) {
  return (
    <Paper withBorder p="md" radius="sm">
      <Stack gap="sm">
        <Text size="sm" fw={600}>
          {failure.heading}
        </Text>

        {failure.input && <LabeledCode label="Input" value={failure.input} />}
        {failure.expected && <LabeledCode label="Expected" value={failure.expected} />}
        {failure.actual && <LabeledCode label="Actual" value={failure.actual} />}
        {failure.error && <LabeledCode label="Error" value={failure.error} />}
        {failure.location && (
          <Text size="xs" ff="monospace" c="dimmed">
            at {failure.location}
          </Text>
        )}
        {failure.extraLines.length > 0 && (
          <Code block>{failure.extraLines.join('\n')}</Code>
        )}
      </Stack>
    </Paper>
  );
}

function LabeledCode({ label, value }: { label: string; value: string }) {
  return (
    <Stack gap={4}>
      <Text size="xs" fw={600} c="dimmed">
        {label}
      </Text>
      <Code block>{value}</Code>
    </Stack>
  );
}

function Timestamp({ label, value }: { label: string; value: string }) {
  const formatted = formatTimestamp(value);
  return (
    <Group gap={4} wrap="nowrap" title={value || 'Never'}>
      <IconClock size={13} style={{ color: 'var(--mantine-color-dimmed)' }} />
      <Text size="xs" c="dimmed">
        {label}: {formatted}
      </Text>
    </Group>
  );
}

function ToneIcon({ tone }: { tone: string }) {
  switch (tone) {
    case 'passed':
      return <IconCircleCheck size={20} />;
    case 'failed':
    case 'error':
      return <IconCircleX size={20} />;
    case 'stale':
      return <IconAlertTriangle size={20} />;
    case 'running':
      return <Loader size={18} />;
    case 'info':
      return <IconInfoCircle size={20} />;
    default:
      return <IconPlayerPlay size={20} />;
  }
}

function toneColor(tone: string): string {
  switch (tone) {
    case 'passed':
      return 'var(--mantine-color-teal-5)';
    case 'failed':
    case 'error':
      return 'var(--mantine-color-red-5)';
    case 'stale':
      return 'var(--mantine-color-yellow-5)';
    case 'running':
    case 'info':
      return 'var(--mantine-color-blue-5)';
    default:
      return 'var(--mantine-color-default-border)';
  }
}

export function formatTimestamp(raw: string): string {
  if (!raw || !raw.trim()) {
    return 'never';
  }
  const date = new Date(raw);
  if (Number.isNaN(date.getTime())) {
    return raw;
  }
  try {
    return new Intl.DateTimeFormat(undefined, {
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
    }).format(date);
  } catch {
    return raw;
  }
}

function toNanos(value: number, unit: string): number {
  switch (unit) {
    case 'ns':
      return value;
    case 'us':
      return value * 1_000;
    case 'ms':
      return value * 1_000_000;
    case 's':
      return value * 1_000_000_000;
    default:
      return value;
  }
}

function parseComplexityMetric(raw: string): ComplexityMetric {
  const paren = raw.match(/^(.+?)\s*\(((?:consistent with |intended ).+)\)$/i);
  if (!paren) {
    return {
      measured: raw.trim(),
      note: '',
      status: raw.toLowerCase().includes('inconclusive') ? 'inconclusive' : 'neutral',
    };
  }
  const measured = paren[1].trim();
  const note = paren[2].trim();
  const lower = note.toLowerCase();
  if (lower.startsWith('consistent with the intended ')) {
    return {
      measured,
      note: `Matches ${note.slice('consistent with the '.length)}`,
      status: 'match',
    };
  }
  if (lower.includes('inconclusive')) {
    return { measured, note, status: 'inconclusive' };
  }
  if (lower.startsWith('intended ')) {
    return { measured, note, status: 'mismatch' };
  }
  return { measured, note, status: 'neutral' };
}

export function parseOutput(raw: string): ParsedOutput {
  const trimmed = (raw ?? '').trim();
  if (!trimmed) {
    return { summaryLine: '', scaling: null, failure: null, unstructured: '' };
  }

  const lines = trimmed.split(/\r?\n/);
  let summaryLine = '';
  for (const line of lines) {
    const s = line.trim();
    if (/^All \d+ supplied cases passed/.test(s) || /^\d+ of \d+ .*failed/.test(s)) {
      summaryLine = s;
      break;
    }
  }

  const scalingIdx = lines.findIndex((line) => /^Measured scaling \(per .+\)$/.test(line.trim()));
  let scaling: ParsedScaling | null = null;
  if (scalingIdx >= 0) {
    const headerMatch = lines[scalingIdx].trim().match(/^Measured scaling \(per (.+)\)$/);
    const unit = headerMatch ? headerMatch[1] : 'call';
    const samples: ScalingSample[] = [];
    const notes: string[] = [];
    let time: ComplexityMetric | undefined;
    let space: ComplexityMetric | undefined;

    for (let i = scalingIdx + 1; i < lines.length; i++) {
      const s = lines[i].trim();
      if (!s || /^n\s+time\s+allocated$/i.test(s) || s.startsWith('STALE:')) {
        continue;
      }
      const row = s.match(/^(\d+)\s+([\d.]+\s*(?:ns|us|ms|s)|-)\s+(.+)$/);
      if (row) {
        const n = Number.parseInt(row[1], 10);
        const timeText = row[2].trim();
        const numUnit = timeText.match(/^([\d.]+)\s*(ns|us|ms|s)$/);
        const timeNanos = numUnit ? toNanos(Number.parseFloat(numUnit[1]), numUnit[2]) : 0;
        samples.push({ n, timeText, timeNanos, allocated: row[3].trim() });
        continue;
      }
      if (s.startsWith('Time:')) {
        time = parseComplexityMetric(s.slice('Time:'.length).trim());
        continue;
      }
      if (s.startsWith('Space:')) {
        space = parseComplexityMetric(s.slice('Space:'.length).trim());
        continue;
      }
      notes.push(s);
    }

    scaling = { unit, samples, time, space, notes };
  }

  const failureIdx = lines.findIndex((line) => {
    const s = line.trim();
    return (
      /^Failed on (Example \d+|custom input):/.test(s) ||
      /^Visible example:/.test(s)
    );
  });

  let failure: ParsedFailure | null = null;
  if (failureIdx >= 0) {
    const heading = lines[failureIdx].trim().replace(/:$/, '');
    let input: string | undefined;
    let expected: string | undefined;
    let actual: string | undefined;
    let error: string | undefined;
    let location: string | undefined;
    const extraLines: string[] = [];

    for (let i = failureIdx + 1; i < lines.length; i++) {
      const s = lines[i].trim();
      if (!s || s.startsWith('STALE:')) {
        continue;
      }
      if (s.startsWith('Input:')) {
        input = s.slice('Input:'.length).trim();
      } else if (s.startsWith('Expected:')) {
        expected = s.slice('Expected:'.length).trim();
      } else if (s.startsWith('Actual:')) {
        actual = s.slice('Actual:'.length).trim();
      } else if (s.startsWith('Error:')) {
        error = s.slice('Error:'.length).trim();
      } else if (s.startsWith('at ') && !location) {
        location = s.slice(3).trim();
      } else {
        extraLines.push(lines[i]);
      }
    }

    failure = { heading, input, expected, actual, error, location, extraLines };
  }

  const visibleUnstructured = lines
    .filter((line) => {
      const s = line.trim();
      return !s.startsWith('Managed attempt:') && !s.startsWith('STALE:');
    })
    .join('\n')
    .trim();

  return {
    summaryLine,
    scaling,
    failure,
    unstructured: visibleUnstructured || trimmed,
  };
}
