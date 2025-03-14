import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleManager {
    private IntersectionPanel intersectionPanel;
    private LogPanel logPanel;
    private ConcurrentHashMap<Rectangle, String> vehicleDirections = new ConcurrentHashMap<>();
    private boolean collisionDetection;
    private volatile boolean collisionDetected = false;
    private Intersection intersection;
    private List<UIVehicle> movingVehicles = new ArrayList<>();
    
    // Listes des véhicules en attente par direction
    private Map<String, List<Rectangle>> vehicleQueues = new HashMap<>();

    /**
     * Constructeur pour le gestionnaire de véhicules.
     * 
     * @param intersectionPanel Le panneau d'intersection
     */
    public VehicleManager(IntersectionPanel intersectionPanel) {
        this.intersectionPanel = intersectionPanel;
        this.logPanel = new LogPanel();
        this.collisionDetection = false;
        
        // Initialiser les files d'attente pour chaque direction
        vehicleQueues.put("North-South", new ArrayList<>());
        vehicleQueues.put("South-North", new ArrayList<>());
        vehicleQueues.put("East-West", new ArrayList<>());
        vehicleQueues.put("West-East", new ArrayList<>());
    }

    /**
     * Constructeur pour le gestionnaire de véhicules.
     * 
     * @param intersectionPanel Le panneau d'intersection
     * @param logPanel Le panneau de logs
     * @param collisionDetection Activation de la détection de collision
     */
    public VehicleManager(IntersectionPanel intersectionPanel, LogPanel logPanel, boolean collisionDetection) {
        this.intersectionPanel = intersectionPanel;
        this.logPanel = logPanel;
        this.collisionDetection = collisionDetection;
        
        // Initialiser les files d'attente pour chaque direction
        vehicleQueues.put("North-South", new ArrayList<>());
        vehicleQueues.put("South-North", new ArrayList<>());
        vehicleQueues.put("East-West", new ArrayList<>());
        vehicleQueues.put("West-East", new ArrayList<>());
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public Rectangle createVehicle(int id, String direction) {
        Rectangle vehicle = new Rectangle(20, 20);
        vehicle.setId(String.valueOf(id));

        // Définir la couleur en fonction de la direction
        switch (direction) {
            case "North-South":
                vehicle.setFill(Color.BLUE);
                break;
            case "South-North":
                vehicle.setFill(Color.GREEN);
                break;
            case "East-West":
                vehicle.setFill(Color.RED);
                break;
            case "West-East":
                vehicle.setFill(Color.ORANGE);
                break;
        }

        return vehicle;
    }

    public void queueVehicle(Rectangle vehicle, String direction) {
        vehicleDirections.put(vehicle, direction);
        
        // Ajouter le véhicule à la file d'attente correspondante
        List<Rectangle> queue = vehicleQueues.get(direction);
        queue.add(vehicle);
        
        // Positionner le véhicule au point de départ selon sa direction
        switch (direction) {
            case "North-South":
                vehicle.setTranslateX(250);
                vehicle.setTranslateY(-30 - (queue.size() - 1) * 40); // Décaler les véhicules en file d'attente
                break;
            case "South-North":
                vehicle.setTranslateX(220);
                vehicle.setTranslateY(530 + (queue.size() - 1) * 40);
                break;
            case "East-West":
                vehicle.setTranslateX(530 + (queue.size() - 1) * 40);
                vehicle.setTranslateY(250);
                break;
            case "West-East":
                vehicle.setTranslateX(-30 - (queue.size() - 1) * 40);
                vehicle.setTranslateY(220);
                break;
        }

        intersectionPanel.addVehicle(vehicle);
    }
    
    /**
     * Ajoute un véhicule à la simulation sans le mettre en file d'attente.
     * Utilisé par le générateur de véhicules intelligent.
     * 
     * @param vehicle Le véhicule à ajouter
     * @param direction La direction du véhicule
     */
    public void addVehicle(Rectangle vehicle, String direction) {
        vehicleDirections.put(vehicle, direction);
        
        // Démarrer la détection de collision si activée
        if (collisionDetection) {
            startCollisionDetection(vehicle);
        }
    }

    public void moveVehicle(Rectangle vehicle, String direction) {
        if (vehicleDirections.containsKey(vehicle)) {
            UIVehicle uiVehicle = new UIVehicle(vehicle, direction, intersection, this);
            uiVehicle.move();
            
            // Ajouter le véhicule à la liste des véhicules en mouvement
            movingVehicles.add(uiVehicle);
            
            // Démarrer un thread pour vérifier les collisions
            if (collisionDetection) {
                startCollisionDetection(vehicle);
            }
        }
    }

    private void startCollisionDetection(Rectangle vehicle) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> checkCollisions(vehicle)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void checkCollisions(Rectangle vehicle) {
        if (!intersectionPanel.getVehicleLayer().getChildren().contains(vehicle) || collisionDetected) {
            return;
        }
        
        // Vérifier les collisions avec les autres véhicules
        for (Rectangle otherVehicle : vehicleDirections.keySet()) {
            if (vehicle != otherVehicle && 
                intersectionPanel.getVehicleLayer().getChildren().contains(otherVehicle)) {
                
                String direction1 = vehicleDirections.get(vehicle);
                String direction2 = vehicleDirections.get(otherVehicle);
                
                // Vérifier si les directions peuvent entrer en collision
                if (directionsCanCollide(direction1, direction2)) {
                    // Vérifier si les véhicules se chevauchent
                    if (vehicle.getBoundsInParent().intersects(otherVehicle.getBoundsInParent())) {
                        
                        // Créer un effet d'explosion
                        createExplosionEffect(
                            (vehicle.getBoundsInParent().getMinX() + vehicle.getBoundsInParent().getMaxX()) / 2,
                            (vehicle.getBoundsInParent().getMinY() + vehicle.getBoundsInParent().getMaxY()) / 2
                        );
                        
                        // Marquer la collision
                        collisionDetected = true;
                        
                        // Afficher l'écran de fin de jeu
                        Platform.runLater(() -> showGameOverScreen());
                        
                        break;
                    }
                }
            }
        }
    }

    private boolean directionsCanCollide(String dir1, String dir2) {
        if (intersection != null) {
            return intersection.directionsCanCollide(dir1, dir2);
        }
        
        // Implémentation par défaut si l'intersection n'est pas définie
        // Les directions opposées ne peuvent pas entrer en collision
        if ((dir1.equals("North-South") && dir2.equals("South-North")) ||
            (dir1.equals("South-North") && dir2.equals("North-South")) ||
            (dir1.equals("East-West") && dir2.equals("West-East")) ||
            (dir1.equals("West-East") && dir2.equals("East-West"))) {
            return false;
        }
        
        // Les directions sur le même axe ne peuvent pas entrer en collision
        boolean dir1IsNorthSouth = dir1.equals("North-South") || dir1.equals("South-North");
        boolean dir2IsNorthSouth = dir2.equals("North-South") || dir2.equals("South-North");
        
        // Si les deux directions sont sur le même axe, elles ne peuvent pas entrer en collision
        return dir1IsNorthSouth != dir2IsNorthSouth;
    }

    public void createExplosionEffect(double x, double y) {
        List<Circle> explosionParticles = new ArrayList<>();
        
        // Créer plusieurs particules d'explosion avec des couleurs et tailles variées
        for (int i = 0; i < 30; i++) {
            double size = Math.random() * 8 + 3; // Taille entre 3 et 11
            Color color;
            
            // Variation de couleurs pour un effet plus réaliste
            double rand = Math.random();
            if (rand < 0.4) {
                color = Color.ORANGE;
            } else if (rand < 0.7) {
                color = Color.RED;
            } else if (rand < 0.9) {
                color = Color.YELLOW;
            } else {
                color = Color.WHITE;
            }
            
            // Position aléatoire autour du point de collision
            double offsetX = Math.random() * 40 - 20;
            double offsetY = Math.random() * 40 - 20;
            
            Circle particle = new Circle(x + offsetX, y + offsetY, size, color);
            explosionParticles.add(particle);
            
            // Ajouter la particule à la couche d'effets
            Platform.runLater(() -> intersectionPanel.getEffectLayer().getChildren().add(particle));
        }
        
        // Ajouter un cercle plus grand au centre pour l'onde de choc
        Circle shockwave = new Circle(x, y, 5, Color.WHITE);
        Platform.runLater(() -> intersectionPanel.getEffectLayer().getChildren().add(shockwave));
        
        // Animation pour faire disparaître progressivement les particules
        Timeline timeline = new Timeline();
        
        // Animation de l'onde de choc
        KeyFrame[] shockwaveFrames = new KeyFrame[10];
        for (int i = 0; i < 10; i++) {
            final int frameIndex = i;
            shockwaveFrames[i] = new KeyFrame(Duration.millis(i * 50), e -> {
                shockwave.setRadius(5 + frameIndex * 5);
                shockwave.setOpacity(1.0 - (frameIndex * 0.1));
            });
            timeline.getKeyFrames().add(shockwaveFrames[i]);
        }
        
        // Animation des particules
        for (Circle particle : explosionParticles) {
            KeyFrame[] particleFrames = new KeyFrame[10];
            for (int i = 0; i < 10; i++) {
                final int frameIndex = i;
                particleFrames[i] = new KeyFrame(Duration.millis(i * 100), e -> {
                    // Déplacer les particules vers l'extérieur
                    double angle = Math.random() * 2 * Math.PI;
                    double distance = frameIndex * 5;
                    particle.setCenterX(particle.getCenterX() + Math.cos(angle) * distance);
                    particle.setCenterY(particle.getCenterY() + Math.sin(angle) * distance);
                    
                    // Réduire l'opacité progressivement
                    particle.setOpacity(1.0 - (frameIndex * 0.1));
                });
                timeline.getKeyFrames().add(particleFrames[i]);
            }
        }
        
        // Supprimer les particules à la fin de l'animation
        timeline.setOnFinished(e -> {
            Platform.runLater(() -> {
                intersectionPanel.getEffectLayer().getChildren().removeAll(explosionParticles);
                intersectionPanel.getEffectLayer().getChildren().remove(shockwave);
            });
        });
        
        timeline.play();
        
        // Ajouter un son d'explosion (si disponible)
        logPanel.addLog("BOUM ! Collision à l'intersection !");
    }

    public void removeVehicle(Rectangle vehicle) {
        if (vehicleDirections.containsKey(vehicle)) {
            String direction = vehicleDirections.get(vehicle);
            
            // Retirer le véhicule de la file d'attente
            if (vehicleQueues.containsKey(direction)) {
                vehicleQueues.get(direction).remove(vehicle);
            }
            
            // Retirer le véhicule de la map des directions
            vehicleDirections.remove(vehicle);
            
            // Retirer le véhicule du panneau
            Platform.runLater(() -> {
                intersectionPanel.removeVehicle(vehicle);
            });
        }
    }

    public boolean isCollisionDetected() {
        return collisionDetected;
    }

    private void showGameOverScreen() {
        // Créer un panneau semi-transparent pour le message de fin
        Pane gameOverPane = new Pane();
        gameOverPane.setPrefSize(500, 500);
        gameOverPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        
        // Créer un texte "Game Over"
        Text gameOverText = new Text("ACCIDENT FATAL !");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gameOverText.setLayoutX(110);
        gameOverText.setLayoutY(200);
        
        // Créer un bouton de retour au menu principal
        Button returnButton = new Button("Retour au menu principal");
        returnButton.setLayoutX(170);
        returnButton.setLayoutY(250);
        returnButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ff6b6b; -fx-text-fill: white;");
        
        // Ajouter une action au bouton
        returnButton.setOnAction(e -> {
            // Retirer le panneau de fin
            intersectionPanel.getChildren().remove(gameOverPane);
            
            // Appeler la méthode de retour au menu principal
            if (intersectionPanel.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) intersectionPanel.getScene().getWindow();
                MainUI mainUI = (MainUI) stage.getUserData();
                if (mainUI != null) {
                    mainUI.showSelectionScreen();
                }
            }
        });
        
        // Ajouter les éléments au panneau
        gameOverPane.getChildren().addAll(gameOverText, returnButton);
        
        // Ajouter le panneau à l'interface
        intersectionPanel.getChildren().add(gameOverPane);
    }

    /**
     * Récupère le panneau de logs.
     * 
     * @return Le panneau de logs
     */
    public LogPanel getLogPanel() {
        return logPanel;
    }
}
