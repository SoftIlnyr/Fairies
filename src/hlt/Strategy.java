package hlt;

import java.util.*;

import java.util.Map;

public class Strategy {
    /**
     * дистанция между объектами
     */
    private GameMap gameMap;

    //наши планеты
    private Map<Integer, Planet> allyPlanets;
    //свободные планеты
    private Map<Integer, Planet> emptyPlanets;
    // вражеские планеты
    private Map<Integer, Planet> enemyPlanets;

    public enum ShipRole { Docker, Rider}

    public Strategy(GameMap gameMap) {
        this.gameMap = gameMap;
        initPlanetsMap();
    }

    public static double getDistance(Entity e1, Entity e2) {
        return Math.sqrt(Math.pow(e1.getXPos() - e2.getXPos(), 2) + Math.pow(e1.getYPos() - e2.getYPos(), 2));
    }

    private void initPlanetsMap(){
        emptyPlanets = new HashMap<>();
        allyPlanets = new HashMap<>();
        enemyPlanets = new HashMap<>();
        for (final Planet planet : gameMap.getAllPlanets().values()) {
            if (!planet.isOwned()) {
                emptyPlanets.put(planet.getId(), planet);
                continue;
            }
            int owner = planet.getOwner();
            if (gameMap.getMyPlayer().getId() != owner) {
                enemyPlanets.put(planet.getId(), planet);
//                        continue;
            }
//                    allyPlanets.add(planet);

        }
    }



//    public Map<Planet, Double> sortedPlanets(Entity e, Map<Integer, Planet> planets) {
//        Map<Planet, Double> result = new TreeMap<>((o1, o2) -> (int) (Strategy.getDistance(e, o1) - Strategy.getDistance(e, o2)));
//        planets.forEach((integer, planet) -> {
//            result.put(planet, Strategy.getDistance(e, planet));
//
//        });
//        return result;
//    }

    //ближайшая планета
    //на вход список планет
    //
    public static Planet getNearPlanet(Map<Integer, Planet> planets, Entity e1) {
        Planet result = planets.get(0);
        double min = e1.getDistanceTo(result);
        for (Integer pId : planets.keySet()) {
            double distance = e1.getDistanceTo(planets.get(pId));
            if (distance < min) {
                result = planets.get(pId);
                min = distance;
            }
        }
        return result;
    }


    /**
     * ближайшая планета для корабля
     * на вход список планет
     *         корабль
     */


    //Захватчик
    //Перехватчик - сбивает в полёте
    //рейдер - захват захваченых планет
    //

    /**
     * стратегия для рейдера
     * поиск ближайших планет
     */
    public DockMove shipStrategy(Map<Integer, Planet> planets, Ship ship) {
        Planet planet = getNearPlanet(planets, ship);
        return new DockMove(ship, planet);
    }


    public Move attackNearPlanet(Map<Integer, Planet> planets, Ship ship) {
        Planet planet = getNearPlanet(planets, ship);
        if (ship.canDock(planet)) {
            return new DockMove(ship, planet);
        }
        //контроль скорости

        return Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED / 2);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Map<Integer, Planet> getAllyPlanets() {
        return allyPlanets;
    }

    public void setAllyPlanets(Map<Integer, Planet> allyPlanets) {
        this.allyPlanets = allyPlanets;
    }

    public Map<Integer, Planet> getEmptyPlanets() {
        return emptyPlanets;
    }

    public void setEmptyPlanets(Map<Integer, Planet> emptyPlanets) {
        this.emptyPlanets = emptyPlanets;
    }

    public Map<Integer, Planet> getEnemyPlanets() {
        return enemyPlanets;
    }

    public void setEnemyPlanets(Map<Integer, Planet> enemyPlanets) {
        this.enemyPlanets = enemyPlanets;
    }
}
