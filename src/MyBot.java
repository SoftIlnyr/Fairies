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
            int permissionToAttackCount = 10;
            double dockerPercentage = 0.5;
            double riderPercentage = 0.9;

            boolean permissionToAttack = false;
            int iterator = 0;
            if (shipsCount > permissionToAttackCount) {
                permissionToAttack = true;
            }
            Ship enemyShip = null;
            Strategy.ShipRole role = Strategy.ShipRole.Docker;
            try{
                for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                    iterator++;
                    if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                        continue;
                    }

                    if ((permissionToAttack && iterator > dockerPercentage * shipsCount) || strategy.getDockerPlanets().size() == 0) {
                        role = Strategy.ShipRole.Rider;
                    }
                    if ((permissionToAttack && iterator > riderPercentage * shipsCount)) {
                        role = Strategy.ShipRole.Kamikaze;
                    }
                    Planet planet;
                    Position target = null;
                    switch (role) {
                        case Rider: {
                            planet = Strategy.getNearPlanet(strategy.getAllyPlanets(), ship);
                            try {
                                target = Strategy.getNearShip(gameMap.getAllShips(), planet, gameMap.getMyPlayer());
                            } catch (Exception e) {
                                target = Strategy.getNearPlanet(strategy.getEnemyPlanets(), ship);
                            }
                            ThrustMove move = Navigation.navigateShipTowardsTarget(gameMap, ship, target, Constants.MAX_SPEED,
                                    true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI / 180.0);
                            if (move != null) {
                                moveList.add(move);
                            }
                            continue;
                        }
                        case Kamikaze: {
                            if (strategy.getKamikazePlanets().size() > 0) {
                                planet = Strategy.getNearPlanet(strategy.getEnemyPlanets(), ship);
                                target = planet;
                                ThrustMove move = Navigation.navigateShipTowardsTarget(gameMap, ship, target, Constants.MAX_SPEED,
                                        true, Constants.MAX_NAVIGATION_CORRECTIONS, Math.PI / 180.0);
                                if (move != null) {
                                    moveList.add(move);
                                }
                            }
                        }
                        case Docker: {
                            if(strategy.getDockerPlanets().size() == 0){
                                if (strategy.getEmptyPlanets().size() > 0) {
                                    strategy.getDockerPlanets().putAll(strategy.getEmptyPlanets());
                                } else {
                                    strategy.getDockerPlanets().putAll(strategy.getAllyPlanets());
                                }
                            }
                            planet = Strategy.getNearPlanet(strategy.getDockerPlanets(), ship);
                            strategy.getDockerPlanets().remove(planet.getId());
                            if (ship.canDock(planet)) {
                                moveList.add(new DockMove(ship, planet));
                                break;
                            }
                            final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                            if (newThrustMove != null) {
                                moveList.add(newThrustMove);
                            }
                        }
                    }

                }
                }
            catch(Exception e){
                
            }
            Networking.sendMoves(moveList);

        }
    }
}