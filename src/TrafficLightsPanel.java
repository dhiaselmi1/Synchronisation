import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TrafficLightsPanel extends HBox {

    // Déclaration des cercles représentant les feux de circulation pour chaque direction
    private Circle northSouthLight = new Circle(20); // Feu pour Nord-Sud
    private Circle eastWestLight = new Circle(20); // Feu pour Est-Ouest
    private Circle southNorthLight = new Circle(20); // Feu pour Sud-Nord
    private Circle westEastLight = new Circle(20); // Feu pour Ouest-Est

    // Constructeur de la classe TrafficLightsPanel
    public TrafficLightsPanel() {
        super(30); // Espace de 30 pixels entre les éléments de l'HBox
        setAlignment(Pos.CENTER); // Alignement des éléments au centre

        // Initialisation des couleurs des feux à "RED"
        updateLightColor(northSouthLight, "RED");
        updateLightColor(eastWestLight, "RED");
        updateLightColor(southNorthLight, "RED");
        updateLightColor(westEastLight, "RED");

        // Création des VBox pour chaque direction avec un label et le cercle du feu
        VBox nsLight = new VBox(5, new Label("Nord-Sud"), northSouthLight);
        VBox ewLight = new VBox(5, new Label("Est-Ouest"), eastWestLight);
        VBox snLight = new VBox(5, new Label("Sud-Nord"), southNorthLight);
        VBox weLight = new VBox(5, new Label("Ouest-Est"), westEastLight);

        // Alignement des VBox au centre
        nsLight.setAlignment(Pos.CENTER);
        ewLight.setAlignment(Pos.CENTER);
        snLight.setAlignment(Pos.CENTER);
        weLight.setAlignment(Pos.CENTER);

        // Ajout des VBox dans l'HBox principale
        getChildren().addAll(nsLight, ewLight, snLight, weLight);
    }

    // Méthode statique pour mettre à jour la couleur du feu (RED, YELLOW, GREEN)
    public static void updateLightColor(Circle light, String state) {
        Platform.runLater(() -> {
            Color color;
            switch (state) {
                case "RED":
                    color = Color.RED; // Feu rouge
                    break;
                case "YELLOW":
                    color = Color.YELLOW; // Feu jaune
                    break;
                case "GREEN":
                    color = Color.GREEN; // Feu vert
                    break;
                default:
                    color = Color.GRAY;
            }
            light.setFill(color);
        });
    }

    // Méthode pour mettre à jour le feu de circulation en fonction de la direction et de l'état
    public void updateTrafficLight(String direction, String state) {
        Circle light = null;
        switch (direction) {
            case "North-South":
                light = northSouthLight;
                break;
            case "East-West":
                light = eastWestLight;
                break;
            case "South-North":
                light = southNorthLight;
                break;
            case "West-East":
                light = westEastLight;
                break;
        }
        if (light != null) {
            updateLightColor(light, state);
        }
    }

    // Méthode pour mettre tous les feux au vert
    public void setAllGreen() {
        updateLightColor(northSouthLight, "GREEN");
        updateLightColor(eastWestLight, "GREEN");
        updateLightColor(southNorthLight, "GREEN");
        updateLightColor(westEastLight, "GREEN");
    }

    // Méthodes d'accès pour récupérer les cercles représentant les feux de circulation
    public Circle getNorthSouthLight() {
        return northSouthLight; // Retourne le feu pour Nord-Sud
    }

    public Circle getEastWestLight() {
        return eastWestLight; // Retourne le feu pour Est-Ouest
    }

    public Circle getSouthNorthLight() {
        return southNorthLight; // Retourne le feu pour Sud-Nord
    }

    public Circle getWestEastLight() {
        return westEastLight; // Retourne le feu pour Ouest-Est
    }
}
