package hlt;

import java.util.Map;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Strategy {

    public static double getDistance(Entity e1, Entity e2) {
        return Math.sqrt(Math.pow(e1.getXPos() - e2.getXPos(), 2) + Math.pow(e1.getYPos() - e2.getYPos(), 2));
    }

    public Map<Planet, Double> getNearPlanet(Entity e, Map<Integer, Planet> planets) {
        Map<Planet, Double> result = new TreeMap<>((o1, o2) -> (int) (Strategy.getDistance(e, o1) - Strategy.getDistance(e, o2)));
        planets.forEach((integer, planet) -> {
            result.put(planet, Strategy.getDistance(e, planet));

        });
        return result;
    }

    //ближайшая планета
    //на вход список планет
    //



    /**
     * ближайшая планета для корабля
     * на вход список планет
     *         корабль
     */

    /**
     * дистанция между объектами
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
//       Planet planet =  getNearPlanet(ship,planets);
       Planet planet =  new Planet();
       return  new DockMove(ship, planet);
    }





}
