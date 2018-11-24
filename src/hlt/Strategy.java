package hlt;

import java.util.Map;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Strategy {
    /**
     * дистанция между объектами
     */
    private GameMap gameMap;

    //наши планеты
    private ArrayList<Planet> allyPlanets = new ArrayList<>();;
    //свободные планеты
    private ArrayList<Planet> emptyPlanets = new ArrayList<>();
    // вражеские планеты
    private  ArrayList<Planet> enemyPlanets = new ArrayList<>();;

    public Strategy(GameMap gameMap) {
        this.gameMap = gameMap;
        initPlanetsMap();
    }

    private static double getDistance(Entity e1, Entity e2) {
        return Math.sqrt(Math.pow(e1.getXPos() - e2.getXPos(), 2) + Math.pow(e1.getYPos() - e2.getYPos(), 2));
    }

    private void initPlanetsMap(){
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
    private static Planet getNearPlanet(Map<Integer, Planet> planets, Entity e1) {
        Planet result = planets.get(0);
        double min = Strategy.getDistance(e1, result);
        for (Integer pId : planets.keySet()) {
            double distance = Strategy.getDistance(e1, planets.get(pId));
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
    public DockMove shipStrategy(Map<Integer, Planet> planets, Ship ship){
       Planet planet =  getNearPlanet(planets, ship);
       return new DockMove(ship, planet);
    }

    public ThrustMove attackNearPlanet(Ship ship) {
        Planet planet = getNearPlanet(gameMap.getAllPlanets(), ship);
        return Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
    }
}
