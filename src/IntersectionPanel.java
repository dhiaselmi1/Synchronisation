import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

public class IntersectionPanel extends Pane {
    private Pane vehicleLayer;
    private Pane effectLayer;

    public IntersectionPanel() {
        // Définit la taille préférée du panneau
        setPrefSize(500, 500);

        // Crée un fond pour le panneau
        Rectangle background = new Rectangle(500, 500, Color.LIGHTGRAY);
        
        // Crée la route horizontale avec ombre
        Rectangle horizontalRoad = new Rectangle(500, 60, Color.DARKGRAY);
        horizontalRoad.setLayoutY(220);
        DropShadow roadShadow = new DropShadow();
        roadShadow.setRadius(5.0);
        roadShadow.setOffsetY(3.0);
        horizontalRoad.setEffect(roadShadow);

        // Crée la route verticale avec ombre
        Rectangle verticalRoad = new Rectangle(60, 500, Color.DARKGRAY);
        verticalRoad.setLayoutX(220);
        verticalRoad.setEffect(roadShadow);

        // Marquages de la route horizontale
        Line centerLineH1 = new Line(0, 250, 220, 250);
        Line centerLineH2 = new Line(280, 250, 500, 250);
        centerLineH1.setStroke(Color.WHITE);
        centerLineH1.setStrokeWidth(2);
        centerLineH1.getStrokeDashArray().addAll(10.0, 5.0);
        centerLineH2.setStroke(Color.WHITE);
        centerLineH2.setStrokeWidth(2);
        centerLineH2.getStrokeDashArray().addAll(10.0, 5.0);

        // Marquages de la route verticale
        Line centerLineV1 = new Line(250, 0, 250, 220);
        Line centerLineV2 = new Line(250, 280, 250, 500);
        centerLineV1.setStroke(Color.WHITE);
        centerLineV1.setStrokeWidth(2);
        centerLineV1.getStrokeDashArray().addAll(10.0, 5.0);
        centerLineV2.setStroke(Color.WHITE);
        centerLineV2.setStrokeWidth(2);
        centerLineV2.getStrokeDashArray().addAll(10.0, 5.0);

        // Crée les passages piétons
        createCrosswalk(220, 215, true);
        createCrosswalk(220, 275, true);
        createCrosswalk(215, 220, false);
        createCrosswalk(275, 220, false);

        // Crée des couches séparées pour les véhicules et les effets
        vehicleLayer = new Pane();
        effectLayer = new Pane();
        
        // Configure les couches pour occuper tout l'espace
        vehicleLayer.setPrefSize(500, 500);
        effectLayer.setPrefSize(500, 500);
        
        // Ajoute tous les éléments au panneau dans l'ordre correct
        getChildren().addAll(
            background,
            horizontalRoad,
            verticalRoad,
            centerLineH1, centerLineH2,
            centerLineV1, centerLineV2,
            vehicleLayer,
            effectLayer
        );
    }

    public Pane getVehicleLayer() {
        return vehicleLayer;
    }

    public Pane getEffectLayer() {
        return effectLayer;
    }

    private void createCrosswalk(double x, double y, boolean isHorizontal) {
        for (int i = 0; i < 6; i++) {
            Rectangle line = new Rectangle(isHorizontal ? 60 : 5, isHorizontal ? 5 : 60, Color.WHITE);
            if (isHorizontal) {
                line.setLayoutX(x);
                line.setLayoutY(y - (i * 10));
            } else {
                line.setLayoutX(x + (i * 10));
                line.setLayoutY(y);
            }
            getChildren().add(line);
        }
    }

    public void addDirectionLabels() {
        // Nord-Sud
        Label nsLabel = new Label("Nord-Sud");
        nsLabel.setLayoutX(280);
        nsLabel.setLayoutY(30);
        nsLabel.setTextFill(Color.BLUE);

        // Est-Ouest
        Label ewLabel = new Label("Est-Ouest");
        ewLabel.setLayoutX(410);
        ewLabel.setLayoutY(200);
        ewLabel.setTextFill(Color.RED);

        // Sud-Nord
        Label snLabel = new Label("Sud-Nord");
        snLabel.setLayoutX(180);
        snLabel.setLayoutY(450);
        snLabel.setTextFill(Color.GREEN);

        // Ouest-Est
        Label weLabel = new Label("Ouest-Est");
        weLabel.setLayoutX(30);
        weLabel.setLayoutY(280);
        weLabel.setTextFill(Color.ORANGE);

        // Flèches directionnelles
        createDirectionalArrow(250, 70, 250, 130, Color.BLUE, false);    // Nord-Sud
        createDirectionalArrow(430, 250, 370, 250, Color.RED, true);     // Est-Ouest
        createDirectionalArrow(250, 430, 250, 370, Color.GREEN, false);  // Sud-Nord
        createDirectionalArrow(70, 250, 130, 250, Color.ORANGE, true);   // Ouest-Est

        getChildren().addAll(nsLabel, ewLabel, snLabel, weLabel);
    }

    private void createDirectionalArrow(double startX, double startY, double endX, double endY, Color color, boolean isHorizontal) {
        Line arrow = new Line(startX, startY, endX, endY);
        arrow.setStroke(color);
        arrow.setStrokeWidth(2);

        double arrowSize = 10;
        Line arrowHead1, arrowHead2;

        if (isHorizontal) {
            arrowHead1 = new Line(endX, endY, endX + arrowSize, endY - arrowSize);
            arrowHead2 = new Line(endX, endY, endX + arrowSize, endY + arrowSize);
        } else {
            arrowHead1 = new Line(endX, endY, endX - arrowSize, endY + arrowSize);
            arrowHead2 = new Line(endX, endY, endX + arrowSize, endY + arrowSize);
        }

        arrowHead1.setStroke(color);
        arrowHead1.setStrokeWidth(2);
        arrowHead2.setStroke(color);
        arrowHead2.setStrokeWidth(2);

        getChildren().addAll(arrow, arrowHead1, arrowHead2);
    }

    public void addVehicle(javafx.scene.Node vehicle) {
        vehicleLayer.getChildren().add(vehicle);
        vehicle.toFront();
    }
    
    public void removeVehicle(javafx.scene.Node vehicle) {
        vehicleLayer.getChildren().remove(vehicle);
    }
}
