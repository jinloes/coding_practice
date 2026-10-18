import { Badge, Group, Stack, Text, Typography } from '@mantine/core';
import { send } from './bridge';
import type { Link, PageEvent } from './types';

const DIFFICULTY_COLORS: Record<string, string> = { Easy: 'teal', Medium: 'yellow', Hard: 'red' };

export function difficultyColor(difficulty: string): string {
  return DIFFICULTY_COLORS[difficulty] ?? 'gray';
}

export function DifficultyBadge({ difficulty }: { difficulty: string }) {
  return (
    <Badge color={difficultyColor(difficulty)} variant="light">
      {difficulty}
    </Badge>
  );
}

/** Renders HTML the plugin already escaped with MarkdownHtml. */
export function Html({ html }: { html: string }) {
  return (
    <Typography>
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

/** Clickable badges that select a study guide or practice track in the tool window filters. */
export function Memberships({ guides, tracks }: { guides: Link[]; tracks: Link[] }) {
  return (
    <Stack gap="xs">
      <LinkRow label="Study guides" links={guides} type="selectGuide" empty="Not part of any guide yet." />
      {tracks.length > 0 && <LinkRow label="Practice tracks" links={tracks} type="selectTrack" />}
    </Stack>
  );
}

function LinkRow({ label, links, type, empty }: { label: string; links: Link[]; type: PageEvent['type']; empty?: string }) {
  return (
    <Stack gap={4}>
      <SectionLabel>{label}</SectionLabel>
      {links.length === 0 ? (
        <Text size="sm" c="dimmed">
          {empty}
        </Text>
      ) : (
        <Group gap={6}>
          {links.map((link) => (
            <Badge
              key={link.id}
              component="button"
              variant="outline"
              style={{ cursor: 'pointer', textTransform: 'none' }}
              onClick={() => send({ type, id: link.id })}
            >
              {link.title}
            </Badge>
          ))}
        </Group>
      )}
    </Stack>
  );
}
