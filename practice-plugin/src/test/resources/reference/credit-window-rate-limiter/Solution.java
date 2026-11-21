package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private static final class ClientState {
        long windowIndex;
        int remainingBase;
        int credits;

        ClientState(long windowIndex, int remainingBase) {
            this.windowIndex = windowIndex;
            this.remainingBase = remainingBase;
        }
    }

    private final int baseLimit;
    private final long windowSeconds;
    private final int maxCredits;
    private final Map<String, ClientState> states = new HashMap<>();

    public Solution(int baseLimit, int windowSeconds, int maxCredits) {
        if (baseLimit < 1 || windowSeconds < 1 || maxCredits < 0) {
            throw new IllegalArgumentException("Invalid rate limiter configuration.");
        }
        this.baseLimit = baseLimit;
        this.windowSeconds = windowSeconds;
        this.maxCredits = maxCredits;
    }

    public boolean allow(String clientId, long timestampSeconds) {
        if (clientId == null || clientId.isBlank() || timestampSeconds < 0) {
            throw new IllegalArgumentException("Invalid request arguments.");
        }
        long currentWindow = timestampSeconds / windowSeconds;
        ClientState state = states.get(clientId);
        if (state == null) {
            state = new ClientState(currentWindow, baseLimit);
            states.put(clientId, state);
        } else if (currentWindow > state.windowIndex) {
            long idleWindows = currentWindow - state.windowIndex - 1;
            long carried = Math.min((long) maxCredits, (long) state.credits + state.remainingBase);
            long needed = maxCredits - carried;
            if (needed <= 0 || (idleWindows > 0 && idleWindows >= (needed + baseLimit - 1) / baseLimit)) {
                state.credits = maxCredits;
            } else {
                state.credits = (int) (carried + idleWindows * baseLimit);
            }
            state.windowIndex = currentWindow;
            state.remainingBase = baseLimit;
        }
        if (state.remainingBase > 0) {
            state.remainingBase--;
            return true;
        }
        if (state.credits > 0) {
            state.credits--;
            return true;
        }
        return false;
    }
}
