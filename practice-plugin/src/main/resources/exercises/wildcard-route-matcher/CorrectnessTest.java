package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void backtracksToWildcardBranchWhenDeeperLiteralBranchDeadEnds() {
        Solution router = new Solution();
        router.register("/spaces/eng/pages/overview", "engOverview");
        router.register("/spaces/*/pages/*", "anyPage");

        assertThat(router.match("/spaces/eng/pages/roadmap")).isEqualTo("anyPage");
        assertThat(router.match("/spaces/eng/pages/overview")).isEqualTo("engOverview");
    }

    @Test
    void normalizesSingleTrailingSlashAndOverwritesDuplicatePattern() {
        Solution router = new Solution();
        router.register("/teams/*/boards/", "v1Boards");
        router.register("/teams/*/boards", "v2Boards");

        assertThat(router.match("/teams/platform/boards/")).isEqualTo("v2Boards");
        assertThat(router.match("/teams/platform/boards")).isEqualTo("v2Boards");
    }

    @Test
    void doesNotMatchShorterOrLongerPathsWithoutTerminalHandler() {
        Solution router = new Solution();
        router.register("/v1/users/*/profile", "profile");

        assertThat(router.match("/v1/users/42")).isEmpty();
        assertThat(router.match("/v1/users/42/profile/edit")).isEmpty();
    }

    @Test
    void treatsLiteralStarInRequestPathUsingLiteralPrecedenceThenWildcard() {
        Solution router = new Solution();
        router.register("/docs/*", "anyDoc");
        assertThat(router.match("/docs/*")).isEqualTo("anyDoc");
    }

    @Test
    void rejectsMalformedPathsPatternsAndBlankHandlers() {
        Solution router = new Solution();
        assertThatThrownBy(() -> router.register("relative/path", "h")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> router.register("/a//b", "h")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> router.register("/a/", "   ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> router.register(null, "h")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> router.match("no-slash")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> router.match("/a//b")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> router.match("//")).isInstanceOf(IllegalArgumentException.class);
    }
}
