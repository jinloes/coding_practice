import { Badge, Group, NavLink, Paper, Progress, Stack, Text, Title } from '@mantine/core';
import { IconCircle, IconCircleCheckFilled } from '@tabler/icons-react';
import { send } from './bridge';
import { difficultyColor, Html, Memberships, SectionLabel } from './common';
import type { Guide, GuideLevel, Membership } from './types';

export function GuideView({ guide }: { guide: Guide }) {
  const exercises = guide.levels.flatMap((level) => level.exercises);
  const passed = exercises.filter((exercise) => exercise.passed).length;
  return (
    <Stack gap="md">
      <Stack gap={4}>
        <SectionLabel>{guide.kind === 'track' ? 'Practice track' : 'Study guide'}</SectionLabel>
        <Title order={3}>{guide.title}</Title>
        <Text c="dimmed" fs="italic" component="div">
          <span dangerouslySetInnerHTML={{ __html: guide.summaryHtml }} />
        </Text>
      </Stack>

      <Paper withBorder p="sm">
        <Group justify="space-between">
          <Text size="sm" fw={600}>
            Progress
          </Text>
          <Text size="sm" c="dimmed">
            {passed}/{exercises.length} passed
          </Text>
        </Group>
        <Progress
          mt={6}
          color="teal"
          value={exercises.length === 0 ? 0 : (100 * passed) / exercises.length}
          aria-label="Guide progress"
        />
      </Paper>

      <Html html={guide.lessonHtml} />

      {guide.levels.map((level) => (
        <LevelSection key={level.level} level={level} />
      ))}
    </Stack>
  );
}

function LevelSection({ level }: { level: GuideLevel }) {
  const passed = level.exercises.filter((exercise) => exercise.passed).length;
  return (
    <Stack gap={4}>
      <Group gap="xs">
        <Title order={5}>{level.level}</Title>
        <Badge color={difficultyColor(level.level)} variant="light">
          {passed}/{level.exercises.length} passed
        </Badge>
      </Group>
      {level.exercises.length === 0 ? (
        <Text size="sm" c="dimmed">
          No {level.level} problems in this guide yet.
        </Text>
      ) : (
        level.exercises.map((exercise) => (
          <NavLink
            key={exercise.id}
            label={exercise.title}
            description={exercise.topic}
            active={exercise.selected}
            variant="light"
            leftSection={
              exercise.passed ? (
                <IconCircleCheckFilled size={16} color="var(--mantine-color-teal-6)" aria-label="Passed" />
              ) : (
                <IconCircle size={16} color="var(--mantine-color-dimmed)" aria-label="Not passed" />
              )
            }
            onClick={() => send({ type: 'selectExercise', id: exercise.id })}
          />
        ))
      )}
    </Stack>
  );
}

export function ChooserView({ selected }: { selected: Membership | null }) {
  return (
    <Stack gap="md">
      <Text>Choose a guide above to study a topic, or a track to practice its list.</Text>
      {selected && (
        <Paper withBorder p="sm">
          <Stack gap="xs">
            <Text fw={600}>{selected.title}</Text>
            <Memberships guides={selected.guides} tracks={selected.tracks} />
          </Stack>
        </Paper>
      )}
    </Stack>
  );
}
