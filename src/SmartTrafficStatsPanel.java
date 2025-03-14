import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Panneau d'affichage des statistiques pour les feux intelligents.
 * Affiche le nombre de véhicules en attente et en passage pour chaque direction.
 */
public class SmartTrafficStatsPanel extends VBox {
    private Label titleLabel;
    private GridPane statsGrid;
    
    // Labels pour les véhicules en attente
    private Label northSouthWaitingLabel;
    private Label southNorthWaitingLabel;
    private Label eastWestWaitingLabel;
    private Label westEastWaitingLabel;
    
    // Labels pour les véhicules en passage
    private Label northSouthPassingLabel;
    private Label southNorthPassingLabel;
    private Label eastWestPassingLabel;
    private Label westEastPassingLabel;
    
    /**
     * Constructeur du panneau de statistiques.
     */
    public SmartTrafficStatsPanel() {
        setPadding(new Insets(10));
        setSpacing(10);
        setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        
        // Titre
        titleLabel = new Label("Statistiques des Véhicules");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Grille pour les statistiques
        statsGrid = new GridPane();
        statsGrid.setHgap(10);
        statsGrid.setVgap(5);
        
        // En-têtes
        Label directionHeader = new Label("Direction");
        directionHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Label waitingHeader = new Label("En Attente");
        waitingHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Label passingHeader = new Label("En Passage");
        passingHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        statsGrid.add(directionHeader, 0, 0);
        statsGrid.add(waitingHeader, 1, 0);
        statsGrid.add(passingHeader, 2, 0);
        
        // Initialiser les labels pour chaque direction
        // Nord-Sud
        Label northSouthLabel = new Label("Nord-Sud");
        northSouthWaitingLabel = new Label("0");
        northSouthPassingLabel = new Label("0");
        
        statsGrid.add(northSouthLabel, 0, 1);
        statsGrid.add(northSouthWaitingLabel, 1, 1);
        statsGrid.add(northSouthPassingLabel, 2, 1);
        
        // Sud-Nord
        Label southNorthLabel = new Label("Sud-Nord");
        southNorthWaitingLabel = new Label("0");
        southNorthPassingLabel = new Label("0");
        
        statsGrid.add(southNorthLabel, 0, 2);
        statsGrid.add(southNorthWaitingLabel, 1, 2);
        statsGrid.add(southNorthPassingLabel, 2, 2);
        
        // Est-Ouest
        Label eastWestLabel = new Label("Est-Ouest");
        eastWestWaitingLabel = new Label("0");
        eastWestPassingLabel = new Label("0");
        
        statsGrid.add(eastWestLabel, 0, 3);
        statsGrid.add(eastWestWaitingLabel, 1, 3);
        statsGrid.add(eastWestPassingLabel, 2, 3);
        
        // Ouest-Est
        Label westEastLabel = new Label("Ouest-Est");
        westEastWaitingLabel = new Label("0");
        westEastPassingLabel = new Label("0");
        
        statsGrid.add(westEastLabel, 0, 4);
        statsGrid.add(westEastWaitingLabel, 1, 4);
        statsGrid.add(westEastPassingLabel, 2, 4);
        
        // Ajouter les composants au panneau
        getChildren().addAll(titleLabel, statsGrid);
    }
    
    /**
     * Met à jour le nombre de véhicules en attente pour une direction donnée.
     * 
     * @param direction La direction
     * @param count Le nombre de véhicules en attente
     */
    public void updateWaitingCount(String direction, int count) {
        Platform.runLater(() -> {
            switch (direction) {
                case "North-South":
                    northSouthWaitingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        northSouthWaitingLabel.setTextFill(Color.RED);
                    } else {
                        northSouthWaitingLabel.setTextFill(Color.BLACK);
                    }
                    break;
                case "South-North":
                    southNorthWaitingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        southNorthWaitingLabel.setTextFill(Color.RED);
                    } else {
                        southNorthWaitingLabel.setTextFill(Color.BLACK);
                    }
                    break;
                case "East-West":
                    eastWestWaitingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        eastWestWaitingLabel.setTextFill(Color.RED);
                    } else {
                        eastWestWaitingLabel.setTextFill(Color.BLACK);
                    }
                    break;
                case "West-East":
                    westEastWaitingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        westEastWaitingLabel.setTextFill(Color.RED);
                    } else {
                        westEastWaitingLabel.setTextFill(Color.BLACK);
                    }
                    break;
            }
        });
    }
    
    /**
     * Met à jour le nombre de véhicules en passage pour une direction donnée.
     * 
     * @param direction La direction
     * @param count Le nombre de véhicules en passage
     */
    public void updatePassingCount(String direction, int count) {
        Platform.runLater(() -> {
            switch (direction) {
                case "North-South":
                    northSouthPassingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        northSouthPassingLabel.setTextFill(Color.GREEN);
                    } else {
                        northSouthPassingLabel.setTextFill(Color.BLACK);
                    }
                    break;
                case "South-North":
                    southNorthPassingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        southNorthPassingLabel.setTextFill(Color.GREEN);
                    } else {
                        southNorthPassingLabel.setTextFill(Color.BLACK);
                    }
                    break;
                case "East-West":
                    eastWestPassingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        eastWestPassingLabel.setTextFill(Color.GREEN);
                    } else {
                        eastWestPassingLabel.setTextFill(Color.BLACK);
                    }
                    break;
                case "West-East":
                    westEastPassingLabel.setText(String.valueOf(count));
                    if (count > 0) {
                        westEastPassingLabel.setTextFill(Color.GREEN);
                    } else {
                        westEastPassingLabel.setTextFill(Color.BLACK);
                    }
                    break;
            }
        });
    }
}
