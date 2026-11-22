import { useState } from 'react';
import { Badge, Button, Group, NavLink, Paper, Stack, Text, Title } from '@mantine/core';
import { difficultyColor, Html, SectionLabel } from './ProblemView';
import type { GuideItem, ProblemItem } from './types';

export function GuideView({
  guides,
  problems,
  selectedGuideId,
  onSelectGuide,
  onSelectProblem,
}: {
  guides: GuideItem[];
  problems: ProblemItem[];
  selectedGuideId?: string;
  onSelectGuide?: (guideId: string) => void;
  onSelectProblem: (problemId: string) => void;
}) {
  const [internalSelectedId, setInternalSelectedId] = useState<string>(guides[0]?.id ?? '');
  if (guides.length === 0) {
    return (
      <Text size="sm" c="dimmed">
        No concept guides available.
      </Text>
    );
  }
  const effectiveId = selectedGuideId !== undefined && selectedGuideId !== '' ? selectedGuideId : internalSelectedId;
  const active = guides.find((guide) => guide.id === effectiveId) ?? guides[0];
  const selectGuide = (guideId: string) => {
    setInternalSelectedId(guideId);
    onSelectGuide?.(guideId);
  };
  const problemById = new Map(problems.map((item) => [item.id, item]));
  const related = active.relatedProblemIds
    .map((id) => problemById.get(id))
    .filter((item): item is ProblemItem => item !== undefined);

  return (
    <Stack gap="md">
      <Paper withBorder className="problem-list" role="list" aria-label="Concept guides">
        {guides.map((guide) => (
          <div key={guide.id} role="listitem">
            <NavLink
              component="button"
              active={guide.id === active.id}
              aria-current={guide.id === active.id ? 'true' : undefined}
              variant="light"
              label={guide.title}
              description={guide.summary}
              onClick={() => selectGuide(guide.id)}
            />
          </div>
        ))}
      </Paper>

      <Stack gap="xs">
        <Title order={3}>{active.title}</Title>
        <Text size="sm" c="dimmed">
          {active.summary}
        </Text>
      </Stack>

      <Html className="statement" html={active.bodyHtml} />

      {related.length > 0 && (
        <Stack gap={6}>
          <SectionLabel>Practice this pattern</SectionLabel>
          <Group gap={6} wrap="wrap">
            {related.map((problem) => (
              <Button
                key={problem.id}
                size="compact-xs"
                variant="light"
                rightSection={
                  <Badge size="xs" variant="transparent" color={difficultyColor(problem.difficulty)} p={0}>
                    {problem.difficulty}
                  </Badge>
                }
                onClick={() => onSelectProblem(problem.id)}
              >
                {problem.title}
              </Button>
            ))}
          </Group>
        </Stack>
      )}
    </Stack>
  );
}
