import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class UIVehicle {
    private Rectangle vehicle;
    private String direction;
    private Intersection intersection;
    private VehicleManager vehicleManager;
    private boolean hasPassedIntersection = false;
    private SmartTrafficLight smartTrafficLight;

    public UIVehicle(Rectangle vehicle, String direction, Intersection intersection, VehicleManager vehicleManager) {
        this.vehicle = vehicle;
        this.direction = direction;
        this.intersection = intersection;
        this.vehicleManager = vehicleManager;
        this.smartTrafficLight = null;
    }
    
    public UIVehicle(Rectangle vehicle, String direction, Intersection intersection, VehicleManager vehicleManager, SmartTrafficLight smartTrafficLight) {
        this.vehicle = vehicle;
        this.direction = direction;
        this.intersection = intersection;
        this.vehicleManager = vehicleManager;
        this.smartTrafficLight = smartTrafficLight;
        
        // Signaler qu'un véhicule est en attente pour cette direction
        if (smartTrafficLight != null) {
            smartTrafficLight.incrementWaitingVehicles(direction);
        }
    }

    public void move() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(5), vehicle);
        
        // Configuration du mouvement en fonction de la direction pour aller jusqu'à la fin de la rue
        switch (direction) {
            case "North-South":
                transition.setByY(500); // Augmenter la distance pour aller jusqu'à la fin
                break;
            case "South-North":
                transition.setByY(-500); // Augmenter la distance pour aller jusqu'à la fin
                break;
            case "East-West":
                transition.setByX(-500); // Augmenter la distance pour aller jusqu'à la fin
                break;
            case "West-East":
                transition.setByX(500); // Augmenter la distance pour aller jusqu'à la fin
                break;
        }

        // Gestion de la fin de l'animation
        transition.setOnFinished(e -> {
            // Si nous utilisons des feux intelligents, signaler qu'un véhicule a terminé son passage
            if (smartTrafficLight != null) {
                smartTrafficLight.decrementPassingVehicles(direction);
                System.out.println("Véhicule " + direction + " a terminé son trajet");
            }
            vehicleManager.removeVehicle(vehicle);
        });

        // Mode sans synchronisation - démarrer immédiatement
        if (intersection == null) {
            transition.play();
            return;
        }
        
        // Mode avec synchronisation - vérifier l'état du feu
        Thread checkLightThread = new Thread(() -> {
            try {
                while (!hasPassedIntersection && !Thread.currentThread().isInterrupted()) {
                    // Vérifier si le feu est vert
                    boolean canPass = intersection.canPass(direction);
                    
                    if (canPass) {
                        hasPassedIntersection = true;
                        
                        // Si nous utilisons des feux intelligents, mettre à jour les compteurs
                        if (smartTrafficLight != null) {
                            smartTrafficLight.decrementWaitingVehicles(direction);
                            smartTrafficLight.incrementPassingVehicles(direction);
                            System.out.println("Véhicule " + direction + " commence à traverser l'intersection");
                        }
                        
                        Platform.runLater(() -> {
                            // Ajouter un effet visuel pour indiquer que le véhicule est en mouvement
                            vehicle.setOpacity(0.8);
                            transition.play();
                        });
                        break;
                    } else {
                        // Si le véhicule est en attente, le faire clignoter légèrement
                        if (smartTrafficLight != null) {
                            Platform.runLater(() -> {
                                if (vehicle.getOpacity() == 1.0) {
                                    vehicle.setOpacity(0.7);
                                } else {
                                    vehicle.setOpacity(1.0);
                                }
                            });
                        }
                    }
                    Thread.sleep(500); // Vérifier plus fréquemment
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
        
        checkLightThread.setDaemon(true); // Pour que le thread se termine quand l'application se ferme
        checkLightThread.start();
    }

    public Rectangle getVehicle() {
        return vehicle;
    }

    public String getDirection() {
        return direction;
    }
}
