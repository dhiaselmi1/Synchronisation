import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.HashMap;

/**
 * Classe représentant un feu de circulation intelligent qui s'adapte à la présence de véhicules.
 * Le feu ne passe au vert que s'il y a des véhicules en attente et attend que tous les véhicules
 * aient traversé avant de passer au feu suivant.
 */
public class SmartTrafficLight extends TrafficLight {
    private Map<String, Integer> waitingVehicles;
    private Map<String, Integer> passingVehicles;
    private String myDirection;
    private AtomicBoolean myRunning;
    private Semaphore mySemaphore;
    private TrafficLightsPanel myPanel;
    private SmartTrafficStatsPanel statsPanel;
    
    /**
     * Constructeur pour un feu de circulation intelligent.
     * 
     * @param direction La direction du feu
     * @param running Indicateur d'exécution
     * @param semaphore Sémaphore pour synchroniser les feux
     * @param panel Panneau d'affichage des feux
     * @param statsPanel Panneau d'affichage des statistiques
     */
    public SmartTrafficLight(String direction, AtomicBoolean running, Semaphore semaphore, 
                             TrafficLightsPanel panel, SmartTrafficStatsPanel statsPanel) {
        super(running, semaphore, panel, direction);
        this.myDirection = direction;
        this.myRunning = running;
        this.mySemaphore = semaphore;
        this.myPanel = panel;
        this.statsPanel = statsPanel;
        this.waitingVehicles = new HashMap<>();
        this.passingVehicles = new HashMap<>();
        
        // Initialiser les compteurs pour chaque direction
        waitingVehicles.put("North-South", 0);
        waitingVehicles.put("South-North", 0);
        waitingVehicles.put("East-West", 0);
        waitingVehicles.put("West-East", 0);
        
        passingVehicles.put("North-South", 0);
        passingVehicles.put("South-North", 0);
        passingVehicles.put("East-West", 0);
        passingVehicles.put("West-East", 0);
    }
    
    /**
     * Incrémente le compteur de véhicules en attente pour une direction donnée.
     * 
     * @param direction La direction du véhicule
     */
    public synchronized void incrementWaitingVehicles(String direction) {
        int count = waitingVehicles.getOrDefault(direction, 0);
        waitingVehicles.put(direction, count + 1);
        System.out.println("Véhicule en attente ajouté pour " + direction + ": " + (count + 1));
        
        // Mettre à jour le panneau de statistiques
        if (statsPanel != null) {
            statsPanel.updateWaitingCount(direction, count + 1);
        }
    }
    
    /**
     * Décrémente le compteur de véhicules en attente pour une direction donnée.
     * 
     * @param direction La direction du véhicule
     */
    public synchronized void decrementWaitingVehicles(String direction) {
        int count = waitingVehicles.getOrDefault(direction, 0);
        if (count > 0) {
            waitingVehicles.put(direction, count - 1);
            System.out.println("Véhicule en attente retiré pour " + direction + ": " + (count - 1));
            
            // Mettre à jour le panneau de statistiques
            if (statsPanel != null) {
                statsPanel.updateWaitingCount(direction, count - 1);
            }
        }
    }
    
    /**
     * Incrémente le compteur de véhicules en train de passer pour une direction donnée.
     * 
     * @param direction La direction du véhicule
     */
    public synchronized void incrementPassingVehicles(String direction) {
        int count = passingVehicles.getOrDefault(direction, 0);
        passingVehicles.put(direction, count + 1);
        System.out.println("Véhicule en passage ajouté pour " + direction + ": " + (count + 1));
        
        // Mettre à jour le panneau de statistiques
        if (statsPanel != null) {
            statsPanel.updatePassingCount(direction, count + 1);
        }
    }
    
    /**
     * Décrémente le compteur de véhicules en train de passer pour une direction donnée.
     * 
     * @param direction La direction du véhicule
     */
    public synchronized void decrementPassingVehicles(String direction) {
        int count = passingVehicles.getOrDefault(direction, 0);
        if (count > 0) {
            passingVehicles.put(direction, count - 1);
            System.out.println("Véhicule en passage retiré pour " + direction + ": " + (count - 1));
            
            // Mettre à jour le panneau de statistiques
            if (statsPanel != null) {
                statsPanel.updatePassingCount(direction, count - 1);
            }
        }
    }
    
    /**
     * Vérifie s'il y a des véhicules en attente pour une direction donnée.
     * 
     * @param direction La direction à vérifier
     * @return true s'il y a des véhicules en attente, false sinon
     */
    public synchronized boolean hasWaitingVehicles(String direction) {
        return waitingVehicles.getOrDefault(direction, 0) > 0;
    }
    
    /**
     * Vérifie s'il y a des véhicules en train de passer pour une direction donnée.
     * 
     * @param direction La direction à vérifier
     * @return true s'il y a des véhicules en train de passer, false sinon
     */
    public synchronized boolean hasPassingVehicles(String direction) {
        return passingVehicles.getOrDefault(direction, 0) > 0;
    }
    
    /**
     * Vérifie s'il y a des véhicules en attente pour l'axe Nord-Sud/Sud-Nord.
     * 
     * @return true s'il y a des véhicules en attente, false sinon
     */
    public synchronized boolean hasNorthSouthWaitingVehicles() {
        return hasWaitingVehicles("North-South") || hasWaitingVehicles("South-North");
    }
    
    /**
     * Vérifie s'il y a des véhicules en attente pour l'axe Est-Ouest/Ouest-Est.
     * 
     * @return true s'il y a des véhicules en attente, false sinon
     */
    public synchronized boolean hasEastWestWaitingVehicles() {
        return hasWaitingVehicles("East-West") || hasWaitingVehicles("West-East");
    }
    
    /**
     * Vérifie s'il y a des véhicules en train de passer pour l'axe Nord-Sud/Sud-Nord.
     * 
     * @return true s'il y a des véhicules en train de passer, false sinon
     */
    public synchronized boolean hasNorthSouthPassingVehicles() {
        return hasPassingVehicles("North-South") || hasPassingVehicles("South-North");
    }
    
    /**
     * Vérifie s'il y a des véhicules en train de passer pour l'axe Est-Ouest/Ouest-Est.
     * 
     * @return true s'il y a des véhicules en train de passer, false sinon
     */
    public synchronized boolean hasEastWestPassingVehicles() {
        return hasPassingVehicles("East-West") || hasPassingVehicles("West-East");
    }
    
    @Override
    public void run() {
        try {
            // Décalage initial pour les feux Nord-Sud/Sud-Nord
            if (myDirection.equals("North-South") || myDirection.equals("South-North")) {
                Thread.sleep(1000);
            }
            
            while (myRunning.get()) {
                boolean isMainDirection = myDirection.equals("North-South") || myDirection.equals("East-West");
                
                if (isMainDirection) {
                    // Vérifier s'il y a des véhicules en attente pour cet axe
                    boolean hasWaitingVehicles = false;
                    
                    if (myDirection.equals("North-South")) {
                        hasWaitingVehicles = hasNorthSouthWaitingVehicles();
                        if (hasWaitingVehicles) {
                            System.out.println("[SMART] Véhicules en attente sur l'axe Nord-Sud/Sud-Nord");
                        }
                    } else { // East-West
                        hasWaitingVehicles = hasEastWestWaitingVehicles();
                        if (hasWaitingVehicles) {
                            System.out.println("[SMART] Véhicules en attente sur l'axe Est-Ouest/Ouest-Est");
                        }
                    }
                    
                    if (hasWaitingVehicles) {
                        // Acquérir le sémaphore avant de passer au vert
                        System.out.println("[SMART] " + myDirection + " : Tentative d'acquisition du sémaphore");
                        mySemaphore.acquire();
                        System.out.println("[SMART] " + myDirection + " : Sémaphore acquis, passage au vert");
                        
                        // Passer au vert pour cet axe
                        if (myDirection.equals("North-South")) {
                            myPanel.updateTrafficLight("North-South", "GREEN");
                            myPanel.updateTrafficLight("South-North", "GREEN");
                            myPanel.updateTrafficLight("East-West", "RED");
                            myPanel.updateTrafficLight("West-East", "RED");
                            changeState("GREEN");
                            System.out.println("[SMART] Axe Nord-Sud/Sud-Nord passé au VERT");
                        } else { // East-West
                            myPanel.updateTrafficLight("East-West", "GREEN");
                            myPanel.updateTrafficLight("West-East", "GREEN");
                            myPanel.updateTrafficLight("North-South", "RED");
                            myPanel.updateTrafficLight("South-North", "RED");
                            changeState("GREEN");
                            System.out.println("[SMART] Axe Est-Ouest/Ouest-Est passé au VERT");
                        }
                        
                        // Attendre que tous les véhicules aient traversé ou un temps maximum
                        boolean vehiclesStillPassing = true;
                        int maxWaitTime = 0; // Limite de temps maximum (8 secondes)
                        
                        System.out.println("[SMART] Attente du passage des véhicules...");
                        while (vehiclesStillPassing && maxWaitTime < 80) {
                            if (myDirection.equals("North-South")) {
                                vehiclesStillPassing = hasNorthSouthPassingVehicles() || hasNorthSouthWaitingVehicles();
                            } else { // East-West
                                vehiclesStillPassing = hasEastWestPassingVehicles() || hasEastWestWaitingVehicles();
                            }
                            
                            // Afficher un log toutes les 10 itérations (1 seconde)
                            if (maxWaitTime % 10 == 0) {
                                System.out.println("[SMART] " + myDirection + " : Véhicules toujours en passage: " + vehiclesStillPassing + " (temps écoulé: " + (maxWaitTime / 10) + "s)");
                            }
                            
                            Thread.sleep(100); // Vérifier toutes les 100ms
                            maxWaitTime++;
                        }
                        
                        // Passer au jaune
                        if (myDirection.equals("North-South")) {
                            myPanel.updateTrafficLight("North-South", "YELLOW");
                            myPanel.updateTrafficLight("South-North", "YELLOW");
                            changeState("YELLOW");
                            System.out.println("[SMART] Axe Nord-Sud/Sud-Nord passé au JAUNE");
                        } else { // East-West
                            myPanel.updateTrafficLight("East-West", "YELLOW");
                            myPanel.updateTrafficLight("West-East", "YELLOW");
                            changeState("YELLOW");
                            System.out.println("[SMART] Axe Est-Ouest/Ouest-Est passé au JAUNE");
                        }
                        
                        // Jaune pendant 2 secondes
                        Thread.sleep(2000);
                        
                        // Passer au rouge
                        if (myDirection.equals("North-South")) {
                            myPanel.updateTrafficLight("North-South", "RED");
                            myPanel.updateTrafficLight("South-North", "RED");
                            changeState("RED");
                            System.out.println("[SMART] Axe Nord-Sud/Sud-Nord passé au ROUGE");
                        } else { // East-West
                            myPanel.updateTrafficLight("East-West", "RED");
                            myPanel.updateTrafficLight("West-East", "RED");
                            changeState("RED");
                            System.out.println("[SMART] Axe Est-Ouest/Ouest-Est passé au ROUGE");
                        }
                        
                        // Libérer le sémaphore
                        mySemaphore.release();
                        System.out.println("[SMART] " + myDirection + " : Sémaphore libéré");
                    }
                    
                    // Attendre un peu avant de vérifier à nouveau
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[SMART] " + myDirection + " : Thread interrompu");
        }
    }
}
