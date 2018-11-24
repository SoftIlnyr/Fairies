import hlt.*;

import java.util.ArrayList;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Tamagocchi");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();
        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;// пишим что-то сюда
                }

                //наши планеты
                ArrayList<Planet> allyPlanets = new ArrayList<>();;
                //свободные планеты
                ArrayList<Planet> emptyPlanets = new ArrayList<>();
                // вражеские планеты
                ArrayList<Planet> enemyPlanets = new ArrayList<>();;

                for (final Planet planet : gameMap.getAllPlanets().values()) {
                    if (!planet.isOwned()) {
                        emptyPlanets.add(planet);
                        continue;
                    }
                    int owner = planet.getOwner();
                    if(gameMap.getMyPlayer().getId() != owner){
                        enemyPlanets.add(planet);
//                        continue;
                    }
//                    allyPlanets.add(planet);

                }

                for (final Planet planet : gameMap.getAllPlanets().values()) {
                    if (planet.isOwned()) {
                        continue;
                    }

                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                    if (newThrustMove != null) {
                        moveList.add(newThrustMove);
                    }

                    break;
                }
            }
            Networking.sendMoves(moveList);
        }
    }
}
