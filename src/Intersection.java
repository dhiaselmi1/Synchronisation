import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private List<TrafficLight> trafficLights;
    private final Semaphore intersectionSemaphore;
    private final Map<String, String> oppositeDirections;

    public Intersection(TrafficLight[] trafficLights) {
        this.trafficLights = new ArrayList<>();
        for (TrafficLight light : trafficLights) {
            this.trafficLights.add(light);
        }
        
        // Permet à 2 véhicules de traverser l'intersection en même temps si leurs directions ne se croisent pas
        this.intersectionSemaphore = new Semaphore(2, true);
        
        // Initialiser les directions opposées
        this.oppositeDirections = new HashMap<>();
        oppositeDirections.put("North-South", "South-North");
        oppositeDirections.put("South-North", "North-South");
        oppositeDirections.put("East-West", "West-East");
        oppositeDirections.put("West-East", "East-West");
    }
    
    /**
     * Constructeur sans paramètres pour l'intersection.
     * Initialise une intersection sans feux de circulation.
     */
    public Intersection() {
        this.trafficLights = new ArrayList<>();
        this.intersectionSemaphore = new Semaphore(2, true);
        
        // Initialiser les directions opposées
        this.oppositeDirections = new HashMap<>();
        oppositeDirections.put("North-South", "South-North");
        oppositeDirections.put("South-North", "North-South");
        oppositeDirections.put("East-West", "West-East");
        oppositeDirections.put("West-East", "East-West");
    }
    
    /**
     * Ajoute un feu de circulation à l'intersection.
     * @param trafficLight Le feu de circulation à ajouter
     */
    public void addTrafficLight(TrafficLight trafficLight) {
        this.trafficLights.add(trafficLight);
    }

    /**
     * Vérifie si un véhicule peut traverser l'intersection dans une direction donnée.
     * @param direction La direction du véhicule
     * @return true si le passage est autorisé, false sinon
     */
    public boolean canPass(String direction) {
        // Vérifier l'état du feu pour la direction donnée
        for (TrafficLight light : trafficLights) {
            // Pour les directions Nord-Sud et Sud-Nord, on vérifie le feu Nord-Sud
            if (direction.equals("North-South") || direction.equals("South-North")) {
                if (light.getDirection().equals("North-South")) {
                    boolean isGreen = light.getLightState().equals("GREEN");
                    return isGreen;
                }
            }
            // Pour les directions Est-Ouest et Ouest-Est, on vérifie le feu Est-Ouest
            else if (direction.equals("East-West") || direction.equals("West-East")) {
                if (light.getDirection().equals("East-West")) {
                    boolean isGreen = light.getLightState().equals("GREEN");
                    return isGreen;
                }
            }
        }
        
        // Par défaut, on ne passe pas
        return false;
    }
    
    /**
     * Acquiert le sémaphore de l'intersection pour une direction donnée.
     * @param direction La direction du véhicule
     * @throws InterruptedException Si l'acquisition est interrompue
     */
    public void enter(String direction) throws InterruptedException {
        intersectionSemaphore.acquire();
    }
    
    /**
     * Libère le sémaphore de l'intersection pour une direction donnée.
     * @param direction La direction du véhicule
     */
    public void exit(String direction) {
        intersectionSemaphore.release();
    }

    /**
     * Tente d'acquérir le sémaphore pour traverser l'intersection.
     * @throws InterruptedException si le thread est interrompu pendant l'attente
     */
    public void acquireIntersection() throws InterruptedException {
        intersectionSemaphore.acquire();
    }

    /**
     * Libère le sémaphore après avoir traversé l'intersection.
     */
    public void releaseIntersection() {
        intersectionSemaphore.release();
    }

    /**
     * Retourne les feux de circulation de l'intersection.
     * @return Le tableau des feux de circulation
     */
    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }
    
    /**
     * Vérifie si deux directions peuvent entrer en collision.
     * @param dir1 Première direction
     * @param dir2 Deuxième direction
     * @return true si les directions peuvent entrer en collision, false sinon
     */
    public boolean directionsCanCollide(String dir1, String dir2) {
        // Les directions opposées ne peuvent pas entrer en collision
        if (oppositeDirections.containsKey(dir1) && oppositeDirections.get(dir1).equals(dir2)) {
            return false;
        }
        
        // Les directions sur le même axe ne peuvent pas entrer en collision
        boolean dir1IsNorthSouth = dir1.equals("North-South") || dir1.equals("South-North");
        boolean dir2IsNorthSouth = dir2.equals("North-South") || dir2.equals("South-North");
        
        // Si les deux directions sont sur le même axe, elles ne peuvent pas entrer en collision
        return dir1IsNorthSouth != dir2IsNorthSouth;
    }
}
