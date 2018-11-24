import hlt.*;

import java.util.ArrayList;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Fairy");
//        final GameMap gameMap = networking.initialize("Tamagocchi");

        // We now have 1 full minute to analyse the initial map.
        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                        "; height: " + gameMap.getHeight() +
                        "; players: " + gameMap.getAllPlayers().size() +
                        "; planets: " + gameMap.getAllPlanets().size();
//        Log.log(initialMapIntelligence);

        Log.log("+++++++++++++Fairy01++++++++++++++");

        final ArrayList<Move> moveList = new ArrayList<>();


        for (; ; ) {
            moveList.clear();
            networking.updateMap(gameMap);

            Strategy strategy = new Strategy(gameMap);
            int shipsCount = gameMap.getMyPlayer().getShips().size();
            int permissionToAttackCount = 20;
            double dockerPercentage = 0.3;
            boolean permissionToAttack = false;
            int iterator = 0;
            if (shipsCount > permissionToAttackCount) {
                permissionToAttack = true;
            }
            Strategy.ShipRole role = Strategy.ShipRole.Docker;
            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                iterator++;
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }

                if ((permissionToAttack && iterator > dockerPercentage * shipsCount) || strategy.getDockerPlanets().size()==0) {
                    role = Strategy.ShipRole.Rider;
                }
                Planet planet;
                Ship enemyShip;
                Position target = null;
                if (role == Strategy.ShipRole.Rider) {
                    //атаковать докеров противника
                    if (strategy.getEnemyPlanets().size() > 0) {
                        planet = Strategy.getNearPlanet(strategy.getEnemyPlanets(), ship);
                        if (planet.getDockedShips().size() > 0 && planet.getDockedShips().size() <= 4) {
                            enemyShip = gameMap.getAllShips().get(planet.getDockedShips().iterator().next());
                            target = enemyShip;
                        } else {
                            target = planet;
                        }
                    } else {
                        continue;
                    }
                    ThrustMove move = Navigation.navigateShipTowardsTarget(gameMap, ship, target, Constants.MAX_SPEED,
                            true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI / 180.0);
                    if (move != null) {
                        moveList.add(move);
                    }
                    continue;
                }
                //test

                planet = Strategy.getNearPlanet(strategy.getDockerPlanets(), ship);
                if (ship.canDock(planet)) {
                    moveList.add(new DockMove(ship, planet));
                    break;
                }
                final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                if (newThrustMove != null) {
                    moveList.add(newThrustMove);
                }
            }
            Networking.sendMoves(moveList);

        }
    }
}