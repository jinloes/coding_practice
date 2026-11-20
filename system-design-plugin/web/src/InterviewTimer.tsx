import { useEffect, useState } from 'react';
import { Badge, Button, Group, Paper, Text } from '@mantine/core';

const TOTAL_SECONDS = 45 * 60;

type Phase = { untilMinute: number; label: string; color: string };

const PHASES: readonly Phase[] = [
  { untilMinute: 5, label: '1. Requirements (~5m)', color: 'blue' },
  { untilMinute: 7, label: '2. Core entities (~2m)', color: 'cyan' },
  { untilMinute: 12, label: '3. API & interface (~5m)', color: 'indigo' },
  { untilMinute: 25, label: '4. High-level design (~13m)', color: 'teal' },
  { untilMinute: 45, label: '5. Deep dives & tradeoffs (~20m)', color: 'orange' },
];

export function phaseForElapsedSeconds(elapsedSeconds: number): { label: string; color: string } {
  const minute = elapsedSeconds / 60;
  for (const phase of PHASES) {
    if (minute < phase.untilMinute) {
      return phase;
    }
  }
  return { label: 'Time up — wrap up summary', color: 'red' };
}

export function formatClock(remainingSeconds: number): string {
  const clamped = Math.max(0, remainingSeconds);
  const mins = Math.floor(clamped / 60);
  const secs = clamped % 60;
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}

export function InterviewTimer() {
  const [elapsed, setElapsed] = useState(0);
  const [active, setActive] = useState(false);

  useEffect(() => {
    if (!active) {
      return undefined;
    }
    const handle = window.setInterval(() => {
      setElapsed((seconds) => seconds + 1);
    }, 1000);
    return () => window.clearInterval(handle);
  }, [active]);

  const remaining = TOTAL_SECONDS - elapsed;
  const phase = phaseForElapsedSeconds(elapsed);

  return (
    <Paper withBorder p="xs">
      <Group justify="space-between" gap="xs" wrap="wrap">
        <Group gap="xs">
          <Text size="xs" fw={700} ff="monospace" aria-label="Remaining interview time">
            {remaining >= 0 ? formatClock(remaining) : `+${formatClock(-remaining)}`}
          </Text>
          {(active || elapsed > 0) ? (
            <Badge size="xs" variant="light" color={phase.color}>
              {phase.label}
            </Badge>
          ) : (
            <Text size="xs" c="dimmed">
              45-min paced mock timer
            </Text>
          )}
        </Group>
        <Group gap={4}>
          <Button size="compact-xs" variant="light" onClick={() => setActive((running) => !running)}>
            {active ? 'Pause timer' : elapsed > 0 ? 'Resume timer' : 'Start 45m timer'}
          </Button>
          {elapsed > 0 && (
            <Button
              size="compact-xs"
              variant="subtle"
              onClick={() => {
                setActive(false);
                setElapsed(0);
              }}
            >
              Reset
            </Button>
          )}
        </Group>
      </Group>
    </Paper>
  );
}
