import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TeamPhotoTest {

    @Test
    void validPlacementExists() {
        List<Integer> team1 = Lists.newArrayList(10, 11, 12, 13, 14, 15, 16, 17);
        List<Integer> team2 = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> team3 = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 18);

        Collections.shuffle(team1);
        Collections.shuffle(team2);
        Collections.shuffle(team3);

        assertThat(TeamPhoto.validPlacementExists(team1, team2)).isTrue();
        assertThat(TeamPhoto.validPlacementExists(team1, team3)).isFalse();
    }
}