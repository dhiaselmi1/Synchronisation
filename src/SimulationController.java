import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationController {

    private static final String[] DIRECTIONS = {"North-South", "South-North", "East-West", "West-East"};
    private static final Random random = new Random();
    private int vehicleCounter = 0;
    private int maxVehiclesPerDirection = 5; // Limite le nombre de véhicules par direction
    private int[] vehiclesInDirection = new int[4]; // Compte les véhicules dans chaque direction

    public SimulationController(MainUI mainUI) {
        // Le mainUI n'est plus utilisé, nous pouvons supprimer le champ
    }

    /**
     * Crée et initialise les feux de circulation avec leur représentation graphique.
     * @param running Un AtomicBoolean pour contrôler l'exécution des feux.
     * @param semaphore Un Semaphore pour gérer la synchronisation des feux.
     * @param panel Le panneau graphique des feux de circulation.
     * @return Un tableau de feux de circulation initialisés.
     */
    public TrafficLight[] createTrafficLights(AtomicBoolean running, Semaphore semaphore, TrafficLightsPanel panel) {
        TrafficLight[] lights = new TrafficLight[4];
        lights[0] = new TrafficLight(running, semaphore, panel, "North-South");
        lights[1] = new TrafficLight(running, semaphore, panel, "South-North");
        lights[2] = new TrafficLight(running, semaphore, panel, "East-West");
        lights[3] = new TrafficLight(running, semaphore, panel, "West-East");
        
        return lights;
    }

    /**
     * Démarre un générateur de véhicules qui crée des véhicules à intervalles aléatoires.
     * @param running Un AtomicBoolean pour contrôler l'exécution du générateur.
     * @param intersection L'intersection à laquelle les véhicules sont liés.
     * @param vehicleManager Le gestionnaire de véhicules pour la création graphique.
     * @param logPanel Le panneau de logs pour afficher les messages.
     * @return Le thread du générateur de véhicules.
     */
    public Thread startVehicleGenerator(AtomicBoolean running, Intersection intersection, VehicleManager vehicleManager, LogPanel logPanel) {
        Thread generator = new Thread(() -> {
            while (running.get()) {
                try {
                    // Attente aléatoire entre la création de véhicules
                    Thread.sleep(random.nextInt(1500) + 500);

                    if (running.get()) {
                        // Sélection aléatoire d'une direction avec contrôle du nombre de véhicules
                        int dirIndex = random.nextInt(DIRECTIONS.length);
                        String direction = DIRECTIONS[dirIndex];
                        
                        // Vérifier si nous n'avons pas trop de véhicules dans cette direction
                        if (vehiclesInDirection[dirIndex] < maxVehiclesPerDirection) {
                            vehiclesInDirection[dirIndex]++;
                            int id = ++vehicleCounter;

                            Platform.runLater(() -> {
                                Rectangle vehicle = vehicleManager.createVehicle(id, direction);
                                vehicleManager.queueVehicle(vehicle, direction);
                                vehicleManager.moveVehicle(vehicle, direction);
                                logPanel.addLog("Nouveau véhicule créé (ID: " + id + ", Direction: " + direction + ")");
                                
                                // Réduire le compteur quand le véhicule est retiré
                                vehicle.setOnMouseClicked(e -> {
                                    vehicleManager.removeVehicle(vehicle);
                                    vehiclesInDirection[dirIndex]--;
                                });
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        generator.setDaemon(true);
        generator.start();
        return generator;
    }
    
    /**
     * Réinitialise les compteurs de véhicules.
     */
    public void resetVehicleCounts() {
        for (int i = 0; i < vehiclesInDirection.length; i++) {
            vehiclesInDirection[i] = 0;
        }
    }
}
