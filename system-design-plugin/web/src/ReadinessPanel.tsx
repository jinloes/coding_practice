import { useState } from 'react';
import { Badge, Button, Group, Paper, Stack, Text } from '@mantine/core';
import type { DimensionSummary } from './types';

function scoreBadgeColor(averageScore: number): string {
  if (averageScore >= 3.0) {
    return 'teal';
  }
  if (averageScore >= 2.0) {
    return 'yellow';
  }
  return 'red';
}

export function ReadinessPanel({
  dimensionSummary,
  onOpenGuide,
}: {
  dimensionSummary: DimensionSummary[];
  onOpenGuide: (guideId: string) => void;
}) {
  const [open, setOpen] = useState(false);
  const scored = dimensionSummary.filter((item) => item.reviewedCount > 0);
  if (scored.length === 0) {
    return null;
  }
  const weakestCount = scored.filter((item) => item.averageScore < 3.0).length;

  return (
    <Paper withBorder p="xs">
      <Stack gap="xs">
        <Group justify="space-between" align="center" gap="xs" wrap="wrap">
          <Group gap={6} align="center">
            <Button
              size="compact-xs"
              variant="subtle"
              aria-expanded={open}
              onClick={() => setOpen((expanded) => !expanded)}
            >
              Rubric readiness
            </Button>
            <Text size="xs" c="dimmed">
              {weakestCount > 0 ? `${weakestCount} focus area${weakestCount === 1 ? '' : 's'} below 3.0` : 'All dimensions >= 3.0'}
            </Text>
          </Group>
        </Group>
        {open && (
          <Stack gap={6}>
            {dimensionSummary.map((item) => (
              <Group key={item.name} justify="space-between" align="center" gap="xs" wrap="wrap">
                <Text size="xs">{item.name}</Text>
                <Group gap={6} align="center" wrap="nowrap">
                  {item.reviewedCount > 0 ? (
                    <>
                      <Badge size="xs" variant="light" color={scoreBadgeColor(item.averageScore)}>
                        {`${item.averageScore.toFixed(1)} / 4`}
                      </Badge>
                      {item.averageScore < 3.0 && item.guideId !== '' && (
                        <Button
                          size="compact-xs"
                          variant="light"
                          onClick={() => onOpenGuide(item.guideId)}
                        >
                          {`Study: ${item.guideTitle}`}
                        </Button>
                      )}
                    </>
                  ) : (
                    <Text size="xs" c="dimmed">
                      Unscored
                    </Text>
                  )}
                </Group>
              </Group>
            ))}
          </Stack>
        )}
      </Stack>
    </Paper>
  );
}
