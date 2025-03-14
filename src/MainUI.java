import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainUI extends Application {
    private Stage stage;
    private boolean isRunning = false;
    private IntersectionPanel intersectionPanel;
    private LogPanel logPanel;
    private VehicleManager vehicleManager;
    private SimulationController simulationController;
    private Thread vehicleGeneratorThread;
    private AtomicBoolean running;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        showSelectionScreen();
        stage.show();
    }

    /**
     * Affiche l'écran de sélection de la simulation.
     */
    public void showSelectionScreen() {
        isRunning = false;
        running = new AtomicBoolean(false);
        
        // Création du layout principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        
        // Titre
        Label titleLabel = new Label("Simulation de Trafic");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Description
        Label descriptionLabel = new Label("Choisissez le mode de simulation :");
        descriptionLabel.setStyle("-fx-font-size: 16px;");
        
        // Boutons de sélection
        Button synchronizedButton = new Button("Avec synchronisation");
        synchronizedButton.setPrefWidth(200);
        synchronizedButton.setOnAction(e -> showSynchronizedUI());
        
        Button unsynchronizedButton = new Button("Sans synchronisation");
        unsynchronizedButton.setPrefWidth(200);
        unsynchronizedButton.setOnAction(e -> showUnsynchronizedUI());
        
        Button smartSyncButton = new Button("Synchronisation intelligente");
        smartSyncButton.setPrefWidth(200);
        smartSyncButton.setOnAction(e -> showSmartSynchronizedUI());
        
        // Descriptions des modes
        Label syncDesc = new Label("Les feux changent à intervalles réguliers");
        syncDesc.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        
        Label unsyncDesc = new Label("Pas de feux, risque de collision");
        unsyncDesc.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        
        Label smartDesc = new Label("Les feux s'adaptent à la présence de véhicules");
        smartDesc.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
        
        // Assemblage de l'interface
        VBox syncBox = new VBox(5, synchronizedButton, syncDesc);
        syncBox.setAlignment(Pos.CENTER);
        
        VBox unsyncBox = new VBox(5, unsynchronizedButton, unsyncDesc);
        unsyncBox.setAlignment(Pos.CENTER);
        
        VBox smartBox = new VBox(5, smartSyncButton, smartDesc);
        smartBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(
            titleLabel,
            descriptionLabel,
            new VBox(15, syncBox, unsyncBox, smartBox)
        );
        
        // Création de la scène
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Simulation de Trafic - Menu Principal");
        stage.setScene(scene);
    }

    private void showSynchronizedUI() {
        isRunning = true;
        running = new AtomicBoolean(true);
        
        // Initialisation des composants
        intersectionPanel = new IntersectionPanel();
        intersectionPanel.addDirectionLabels();
        logPanel = new LogPanel();
        vehicleManager = new VehicleManager(intersectionPanel, logPanel, true);
        simulationController = new SimulationController(this);

        // Configuration des feux
        TrafficLightsPanel trafficLightsPanel = new TrafficLightsPanel();
        
        // Création d'un sémaphore partagé pour les feux
        Semaphore semaphore = new Semaphore(1, true);
        
        // Création des feux de circulation
        TrafficLight[] trafficLights = simulationController.createTrafficLights(running, semaphore, trafficLightsPanel);
        
        // Création de l'intersection
        Intersection intersection = new Intersection(trafficLights);
        vehicleManager.setIntersection(intersection);

        // Création du layout principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Section du haut avec les feux
        VBox topSection = new VBox(10, trafficLightsPanel);
        topSection.setAlignment(Pos.CENTER);

        // Bouton de retour
        Button returnButton = new Button("Retour au menu principal");
        returnButton.setOnAction(e -> {
            isRunning = false;
            running.set(false);
            if (vehicleGeneratorThread != null) {
                vehicleGeneratorThread.interrupt();
            }
            showSelectionScreen();
        });
        
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10));
        bottomSection.getChildren().add(returnButton);

        // Assemblage de l'interface
        root.setTop(topSection);
        root.setCenter(intersectionPanel);
        root.setBottom(bottomSection);
        root.setRight(logPanel);

        // Création de la scène
        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Simulation avec synchronisation");
        stage.setScene(scene);

        // Démarrage des feux de circulation
        for (TrafficLight light : trafficLights) {
            light.start();
        }

        // Démarrage de la génération de véhicules
        vehicleGeneratorThread = simulationController.startVehicleGenerator(running, intersection, vehicleManager, logPanel);

        // Nettoyage lors de la fermeture
        stage.setOnCloseRequest(e -> {
            isRunning = false;
            running.set(false);
            if (vehicleGeneratorThread != null) {
                vehicleGeneratorThread.interrupt();
            }
        });
    }

    private void showUnsynchronizedUI() {
        isRunning = true;
        running = new AtomicBoolean(true);
        
        // Initialisation des composants
        intersectionPanel = new IntersectionPanel();
        intersectionPanel.addDirectionLabels();
        logPanel = new LogPanel();
        vehicleManager = new VehicleManager(intersectionPanel, logPanel, true);
        simulationController = new SimulationController(this);
        
        // Stocker une référence à cette instance dans les données utilisateur de la scène
        stage.setUserData(this);

        // Configuration des feux tous au vert
        TrafficLightsPanel trafficLightsPanel = new TrafficLightsPanel();
        trafficLightsPanel.setAllGreen();

        // Création du layout principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Section du haut avec les feux
        VBox topSection = new VBox(10, trafficLightsPanel);
        topSection.setAlignment(Pos.CENTER);

        // Bouton de retour
        Button returnButton = new Button("Retour au menu principal");
        returnButton.setOnAction(e -> {
            isRunning = false;
            running.set(false);
            if (vehicleGeneratorThread != null) {
                vehicleGeneratorThread.interrupt();
            }
            showSelectionScreen();
        });
        
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10));
        bottomSection.getChildren().add(returnButton);

        // Assemblage de l'interface
        root.setTop(topSection);
        root.setCenter(intersectionPanel);
        root.setBottom(bottomSection);
        root.setRight(logPanel);

        // Création de la scène
        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Simulation sans synchronisation");
        stage.setScene(scene);

        // Créer un thread qui génère des véhicules plus fréquemment pour provoquer des collisions
        vehicleGeneratorThread = new Thread(() -> {
            String[] directions = {"North-South", "South-North", "East-West", "West-East"};
            int vehicleCounter = 0;
            
            while (isRunning && !vehicleManager.isCollisionDetected()) {
                try {
                    // Génération de plusieurs véhicules
                    for (int i = 0; i < 2; i++) {
                        String direction = directions[(int)(Math.random() * directions.length)];
                        int id = ++vehicleCounter;
                        
                        Platform.runLater(() -> {
                            Rectangle vehicle = vehicleManager.createVehicle(id, direction);
                            vehicleManager.queueVehicle(vehicle, direction);
                            vehicleManager.moveVehicle(vehicle, direction);
                            logPanel.addLog("Nouveau véhicule créé (ID: " + id + ", Direction: " + direction + ")");
                        });
                    }
                    
                    // Attendre avant de générer les prochains véhicules
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Afficher le bouton de retour rouge après une collision
            if (vehicleManager.isCollisionDetected()) {
                Platform.runLater(() -> {
                    Button collisionReturnButton = new Button("Retour au menu principal");
                    collisionReturnButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                    collisionReturnButton.setOnAction(e -> {
                        isRunning = false;
                        running.set(false);
                        showSelectionScreen();
                    });
                    
                    bottomSection.getChildren().clear();
                    bottomSection.getChildren().add(collisionReturnButton);
                });
            }
        });
        vehicleGeneratorThread.setDaemon(true);
        vehicleGeneratorThread.start();

        // Nettoyage lors de la fermeture
        stage.setOnCloseRequest(e -> {
            isRunning = false;
            running.set(false);
            if (vehicleGeneratorThread != null) {
                vehicleGeneratorThread.interrupt();
            }
        });
    }

    private void showSmartSynchronizedUI() {
        // Arrêter la simulation en cours si nécessaire
        stopSimulation();
        
        isRunning = true;
        running = new AtomicBoolean(true);
        
        // Initialisation des composants
        BorderPane root = new BorderPane();
        intersectionPanel = new IntersectionPanel();
        intersectionPanel.addDirectionLabels();
        logPanel = new LogPanel();
        vehicleManager = new VehicleManager(intersectionPanel, logPanel, true);
        
        // Titre de la simulation
        Label titleLabel = new Label("Simulation avec Synchronisation Intelligente");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox topSection = new HBox(20);
        topSection.setPadding(new Insets(10));
        topSection.getChildren().add(titleLabel);
        topSection.setAlignment(Pos.CENTER);

        // Bouton de retour
        Button returnButton = new Button("Retour au menu principal");
        returnButton.setOnAction(e -> {
            isRunning = false;
            running.set(false);
            if (vehicleGeneratorThread != null) {
                vehicleGeneratorThread.interrupt();
            }
            showSelectionScreen();
        });
        
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10));
        bottomSection.getChildren().add(returnButton);
        
        // Créer l'intersection et les feux intelligents
        Intersection intersection = new Intersection();
        vehicleManager.setIntersection(intersection);
        
        // Créer les feux de circulation intelligents
        AtomicBoolean running = new AtomicBoolean(true);
        this.running = running;
        Semaphore semaphore = new Semaphore(1, true);
        TrafficLightsPanel trafficLightsPanel = new TrafficLightsPanel();
        
        // Créer le panneau de statistiques
        SmartTrafficStatsPanel statsPanel = new SmartTrafficStatsPanel();
        
        // Initialiser tous les feux au rouge
        trafficLightsPanel.updateTrafficLight("North-South", "RED");
        trafficLightsPanel.updateTrafficLight("South-North", "RED");
        trafficLightsPanel.updateTrafficLight("East-West", "RED");
        trafficLightsPanel.updateTrafficLight("West-East", "RED");
        
        // Créer les feux intelligents
        SmartTrafficLight northSouthLight = new SmartTrafficLight("North-South", running, semaphore, trafficLightsPanel, statsPanel);
        SmartTrafficLight southNorthLight = new SmartTrafficLight("South-North", running, semaphore, trafficLightsPanel, statsPanel);
        SmartTrafficLight eastWestLight = new SmartTrafficLight("East-West", running, semaphore, trafficLightsPanel, statsPanel);
        SmartTrafficLight westEastLight = new SmartTrafficLight("West-East", running, semaphore, trafficLightsPanel, statsPanel);
        
        // Ajouter les feux à l'intersection
        intersection.addTrafficLight(northSouthLight);
        intersection.addTrafficLight(southNorthLight);
        intersection.addTrafficLight(eastWestLight);
        intersection.addTrafficLight(westEastLight);
        
        // Créer un tableau avec les feux intelligents
        SmartTrafficLight[] smartLights = {northSouthLight, southNorthLight, eastWestLight, westEastLight};
        
        // Créer et démarrer le générateur de véhicules intelligent
        SmartVehicleGenerator vehicleGenerator = new SmartVehicleGenerator(intersectionPanel, vehicleManager, intersection, smartLights);
        vehicleGeneratorThread = vehicleGenerator;
        vehicleGeneratorThread.start();
        
        // Démarrer les feux
        northSouthLight.start();
        southNorthLight.start();
        eastWestLight.start();
        westEastLight.start();
        
        // Créer un panneau pour les informations à droite
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.getChildren().addAll(trafficLightsPanel, statsPanel);
        
        // Assemblage de l'interface
        root.setTop(topSection);
        root.setCenter(intersectionPanel);
        root.setBottom(bottomSection);
        root.setRight(rightPanel);
        
        // Création de la scène
        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Simulation avec synchronisation intelligente");
        stage.setScene(scene);
        stage.show();
        
        // Ajouter un log
        logPanel.addLog("Simulation avec synchronisation intelligente démarrée");
        
        // Nettoyage lors de la fermeture
        stage.setOnCloseRequest(e -> {
            isRunning = false;
            running.set(false);
            if (vehicleGeneratorThread != null) {
                vehicleGeneratorThread.interrupt();
            }
        });
    }

    /**
     * Arrête la simulation en cours.
     */
    private void stopSimulation() {
        if (running != null) {
            running.set(false);
        }
        
        if (vehicleGeneratorThread != null) {
            vehicleGeneratorThread.interrupt();
        }
        
        isRunning = false;
        
        // Nettoyer les ressources
        if (intersectionPanel != null) {
            intersectionPanel.getVehicleLayer().getChildren().clear();
        }
        
        if (logPanel != null) {
            logPanel.addLog("Simulation arrêtée");
        }
    }

    /**
     * Méthode qui sera appelée lorsque l'application sera fermée.
     */
    @Override
    public void stop() {
        isRunning = false;
        if (running != null) {
            running.set(false);
        }
        if (vehicleGeneratorThread != null) {
            vehicleGeneratorThread.interrupt();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
