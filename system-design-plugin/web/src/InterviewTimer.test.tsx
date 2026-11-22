import { act, fireEvent, render, screen } from '@testing-library/react';
import { MantineProvider } from '@mantine/core';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { InterviewTimer } from './InterviewTimer';

function renderTimer() {
  return render(
    <MantineProvider>
      <InterviewTimer />
    </MantineProvider>,
  );
}

describe('InterviewTimer', () => {
  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-03-01T12:00:00Z'));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('anchors elapsed time to wall-clock Date.now across background throttling, pause/resume, overtime, and reset', () => {
    renderTimer();
    expect(screen.getByLabelText('Remaining interview time')).toHaveTextContent('45:00');

    // Start timer
    fireEvent.click(screen.getByRole('button', { name: 'Start 45m timer' }));
    expect(screen.getByRole('button', { name: 'Pause timer' })).toBeInTheDocument();

    // Simulate JCEF background throttling: jump wall clock by 10 minutes without firing interval ticks,
    // then fire visibilitychange so the timer immediately catches up to real elapsed time.
    act(() => {
      vi.setSystemTime(new Date('2026-03-01T12:10:00Z'));
      document.dispatchEvent(new Event('visibilitychange'));
    });
    expect(screen.getByLabelText('Remaining interview time')).toHaveTextContent('35:00');
    expect(screen.getByText(/3\. API & interface/)).toBeInTheDocument();

    // Pause after another 2 minutes via Date.now()
    act(() => {
      vi.setSystemTime(new Date('2026-03-01T12:12:00Z'));
      fireEvent.click(screen.getByRole('button', { name: 'Pause timer' }));
    });
    expect(screen.getByLabelText('Remaining interview time')).toHaveTextContent('33:00');
    expect(screen.getByRole('button', { name: 'Resume timer' })).toBeInTheDocument();

    // While paused, advancing system time by 20 minutes must not advance elapsed time.
    act(() => {
      vi.setSystemTime(new Date('2026-03-01T12:32:00Z'));
      window.dispatchEvent(new Event('focus'));
    });
    expect(screen.getByLabelText('Remaining interview time')).toHaveTextContent('33:00');

    // Resume and advance 34 minutes into overtime (+01:00) via interval tick.
    fireEvent.click(screen.getByRole('button', { name: 'Resume timer' }));
    act(() => {
      vi.setSystemTime(new Date('2026-03-01T13:06:00Z'));
      vi.advanceTimersByTime(500);
    });
    expect(screen.getByLabelText('Remaining interview time')).toHaveTextContent('+01:00');
    expect(screen.getByText(/Time up/)).toBeInTheDocument();

    // Reset restores 45:00 paused state.
    fireEvent.click(screen.getByRole('button', { name: 'Reset' }));
    expect(screen.getByLabelText('Remaining interview time')).toHaveTextContent('45:00');
    expect(screen.getByRole('button', { name: 'Start 45m timer' })).toBeInTheDocument();
  });
});
