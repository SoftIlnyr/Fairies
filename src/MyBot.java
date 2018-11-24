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

            Strategy strategy = new Strategy(gameMap);

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;// пишем что-то сюда
                }

                for (final Planet planet : gameMap.getAllPlanets().values()) {
                    if (planet.isOwned()) {
                        continue;
                    }

                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    ThrustMove thrustMove = null;
                    if (strategy.getEmptyPlanets().size() > 0) {
                        thrustMove = strategy.attackNearPlanet(strategy.getEmptyPlanets(), ship);
                    } else if (strategy.getEnemyPlanets().size() > 0) {
                        thrustMove = strategy.attackNearPlanet(strategy.getEnemyPlanets(), ship);
                    }

                    if (thrustMove != null) {
                        moveList.add(thrustMove);
                    }

                    break;
                }
                Networking.sendMoves(moveList);
            }
        }
    }
}
