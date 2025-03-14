import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrafficLight extends Thread {
    private String direction; // Direction du feu (North-South, South-North, East-West, West-East)
    private String lightState; // État actuel du feu (RED, YELLOW, GREEN)
    private AtomicBoolean running; // Flag pour contrôler l'exécution du thread
    private Semaphore semaphore; // Sémaphore pour la synchronisation entre les feux
    private TrafficLightsPanel panel; // Panneau pour afficher les feux
    private static final int GREEN_DURATION = 8000; // Durée du feu vert en millisecondes
    private static final int YELLOW_DURATION = 2000; // Durée du feu jaune en millisecondes
    private static final int RED_DURATION = 3000; // Durée minimale du feu rouge en millisecondes

    /**
     * Constructeur de la classe TrafficLight.
     * @param running Variable de contrôle pour l'exécution du thread
     * @param semaphore Sémaphore pour la synchronisation
     * @param panel Panneau pour afficher les feux
     * @param direction Direction du feu (North-South, South-North, East-West, West-East)
     */
    public TrafficLight(AtomicBoolean running, Semaphore semaphore, TrafficLightsPanel panel, String direction) {
        this.direction = direction;
        this.lightState = "RED";
        this.running = running;
        this.semaphore = semaphore;
        this.panel = panel;
        updateTrafficLightUI();
    }

    @Override
    public void run() {
        try {
            // Tous les feux commencent en rouge
            Thread.sleep(RED_DURATION);
            
            // Déterminer l'axe du feu
            boolean isNorthSouthAxis = direction.equals("North-South") || direction.equals("South-North");
            boolean isEastWestAxis = direction.equals("East-West") || direction.equals("West-East");
            
            // Décalage initial pour alterner entre les axes
            if (isEastWestAxis) {
                Thread.sleep(GREEN_DURATION + YELLOW_DURATION);
            }
            
            while (running.get()) {
                // Acquérir le sémaphore uniquement pour le premier feu de chaque axe
                // pour éviter les conflits de synchronisation
                if (direction.equals("North-South") || direction.equals("East-West")) {
                    semaphore.acquire();
                    
                    // Passage au vert pour les feux du même axe
                    if (isNorthSouthAxis) {
                        panel.updateTrafficLight("North-South", "GREEN");
                        panel.updateTrafficLight("South-North", "GREEN");
                        panel.updateTrafficLight("East-West", "RED");
                        panel.updateTrafficLight("West-East", "RED");
                    } else {
                        panel.updateTrafficLight("North-South", "RED");
                        panel.updateTrafficLight("South-North", "RED");
                        panel.updateTrafficLight("East-West", "GREEN");
                        panel.updateTrafficLight("West-East", "GREEN");
                    }
                }
                
                // Mettre à jour l'état du feu actuel
                changeState("GREEN");
                
                // Vert pendant la durée définie
                Thread.sleep(GREEN_DURATION);
                
                // Passage au jaune uniquement pour le premier feu de chaque axe
                if (direction.equals("North-South") || direction.equals("East-West")) {
                    if (isNorthSouthAxis) {
                        panel.updateTrafficLight("North-South", "YELLOW");
                        panel.updateTrafficLight("South-North", "YELLOW");
                    } else {
                        panel.updateTrafficLight("East-West", "YELLOW");
                        panel.updateTrafficLight("West-East", "YELLOW");
                    }
                }
                
                // Mettre à jour l'état du feu actuel
                changeState("YELLOW");
                
                // Jaune pendant la durée définie
                Thread.sleep(YELLOW_DURATION);
                
                // Passage au rouge pour tous les feux
                if (direction.equals("North-South") || direction.equals("East-West")) {
                    panel.updateTrafficLight("North-South", "RED");
                    panel.updateTrafficLight("South-North", "RED");
                    panel.updateTrafficLight("East-West", "RED");
                    panel.updateTrafficLight("West-East", "RED");
                    
                    // Libérer le sémaphore pour permettre à l'autre axe de passer
                    semaphore.release();
                }
                
                // Mettre à jour l'état du feu actuel
                changeState("RED");
                
                // Attente pendant que l'autre axe est vert et jaune
                Thread.sleep(GREEN_DURATION + YELLOW_DURATION + RED_DURATION);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Change l'état du feu et met à jour l'interface.
     */
    protected void changeState(String newState) {
        this.lightState = newState;
        updateTrafficLightUI();
    }

    /**
     * Met à jour l'interface graphique du feu.
     */
    private void updateTrafficLightUI() {
        if (panel != null) {
            panel.updateTrafficLight(direction, lightState);
        }
    }

    /**
     * Retourne l'état actuel du feu.
     */
    public String getLightState() {
        return lightState;
    }

    /**
     * Retourne la direction du feu.
     */
    public String getDirection() {
        return direction;
    }
}
