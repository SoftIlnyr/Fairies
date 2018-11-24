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

            int shipsCount = gameMap.getMyPlayer().getShips().size();
            int permissionToAttackCount = 15;
            double dockerPercentage = 0.75;
            boolean permissionToAttack = false;
            int iterator = 0;
            if(shipsCount > permissionToAttackCount){
                permissionToAttack = true;
            }
            Strategy.ShipRole role = Strategy.ShipRole.Docker;
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (permissionToAttack && iterator > dockerPercentage * shipsCount) {
                    role = Strategy.ShipRole.Rider;
                }

                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;// пишем что-то сюда
                }


                ThrustMove thrustMove = null;
                if (role == Strategy.ShipRole.Docker) {
                    thrustMove = strategy.attackNearPlanet(strategy.getEmptyPlanets(), ship);
                } else if (role == Strategy.ShipRole.Rider) {
                    thrustMove = strategy.attackNearPlanet(strategy.getEnemyPlanets(), ship);
                }

//                    if (strategy.getEmptyPlanets().size() > 0) {
//                        thrustMove = strategy.attackNearPlanet(strategy.getEmptyPlanets(), ship);
//                    } else if (strategy.getEnemyPlanets().size() > 0) {
//                        thrustMove = strategy.attackNearPlanet(strategy.getEnemyPlanets(), ship);
//                    }

                if (thrustMove != null) {
                    moveList.add(thrustMove);
                }
                Networking.sendMoves(moveList);
                iterator++;
            }
        }
    }
}
