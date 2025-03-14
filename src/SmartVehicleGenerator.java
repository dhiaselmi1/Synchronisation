import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import java.util.Random;

/**
 * Classe qui génère des véhicules pour la simulation avec synchronisation intelligente.
 * Les véhicules sont générés en fonction de la présence de feux de circulation et de leur état.
 */
public class SmartVehicleGenerator extends Thread {
    private VehicleManager vehicleManager;
    private Intersection intersection;
    private SmartTrafficLight[] smartLights;
    private Random random;
    private LogPanel logPanel;
    
    /**
     * Constructeur pour le générateur de véhicules intelligent.
     * 
     * @param intersectionPanel Le panneau d'intersection
     * @param vehicleManager Le gestionnaire de véhicules
     * @param intersection L'intersection avec les feux
     * @param smartLights Les feux de circulation intelligents
     */
    public SmartVehicleGenerator(IntersectionPanel intersectionPanel, VehicleManager vehicleManager, 
                                Intersection intersection, SmartTrafficLight[] smartLights) {
        this.vehicleManager = vehicleManager;
        this.intersection = intersection;
        this.smartLights = smartLights;
        this.random = new Random();
        this.logPanel = vehicleManager.getLogPanel();
    }
    
    /**
     * Récupère le feu intelligent associé à une direction.
     * 
     * @param direction La direction
     * @return Le feu intelligent correspondant, ou null si non trouvé
     */
    private SmartTrafficLight getTrafficLightForDirection(String direction) {
        for (SmartTrafficLight light : smartLights) {
            if (light.getDirection().equals(direction)) {
                return light;
            }
        }
        return null;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Générer des véhicules aléatoirement pour chaque direction
                for (String direction : new String[]{"North-South", "South-North", "East-West", "West-East"}) {
                    // Probabilité variable de génération de véhicules
                    if (random.nextDouble() < getGenerationProbability(direction)) {
                        // Créer un véhicule avec un ID unique
                        int id = (int)(Math.random() * 10000);
                        Rectangle vehicle = vehicleManager.createVehicle(id, direction);
                        
                        // Déterminer quel feu intelligent est associé à cette direction
                        SmartTrafficLight trafficLight = getTrafficLightForDirection(direction);
                        
                        // Ajouter le véhicule à l'intersection avec le feu intelligent
                        Platform.runLater(() -> {
                            UIVehicle uiVehicle = new UIVehicle(vehicle, direction, intersection, vehicleManager, trafficLight);
                            vehicleManager.queueVehicle(vehicle, direction);
                            uiVehicle.move();
                            if (logPanel != null) {
                                logPanel.addLog("Nouveau véhicule créé (ID: " + id + ", Direction: " + direction + ")");
                            }
                        });
                    }
                }
                
                // Attendre un délai aléatoire entre 1 et 3 secondes avant de générer le prochain véhicule
                Thread.sleep(random.nextInt(2000) + 1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Générateur de véhicules intelligent interrompu");
        }
    }
    
    /**
     * Détermine la probabilité de génération d'un véhicule en fonction de la direction.
     * La probabilité est plus élevée pour les directions où le feu est rouge.
     * 
     * @param direction La direction
     * @return La probabilité de génération (entre 0 et 1)
     */
    private double getGenerationProbability(String direction) {
        // Probabilité de base
        double baseProbability = 0.3;
        
        // Augmenter la probabilité si le feu est rouge (pour créer une file d'attente)
        SmartTrafficLight light = getTrafficLightForDirection(direction);
        if (light != null && light.getState().equals("RED")) {
            return baseProbability * 1.5; // 50% de plus de véhicules quand le feu est rouge
        }
        
        return baseProbability;
    }
}
