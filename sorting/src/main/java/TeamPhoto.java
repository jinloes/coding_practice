import java.util.List;

/**
 * You are a photographer for a soccer meet. You will be taking pictures of pairs of opposing teams. All teams have
 * the same number of players. A team photo consists of a front row of players and a back row of players.
 * A player in the back row must be taller than the player in front of him. All players in a row must be from the
 * same team.
 * <p>
 * Design an algorithm that takes as input two teams and the heights of the players in the teams and checks if it is
 * possible to place players to take the photo subject to the placement constraint.
 */
public class TeamPhoto {
    public static boolean validPlacementExists(List<Integer> team1, List<Integer> team2) {
        team1.sort(Integer::compareTo);
        team2.sort(Integer::compareTo);

        List<Integer> shorter;
        List<Integer> longer;

        if (team1.get(0) < team2.get(0)) {
            shorter = team1;
            longer = team2;
        } else {
            shorter = team2;
            longer = team1;
        }

        for (int i = 0; i < team1.size(); i++) {
            int shorterPlayer = shorter.get(i);
            int tallerPlayer = longer.get(i);

            if (tallerPlayer <= shorterPlayer) {
                return false;
            }
        }

        return true;
    }
}
