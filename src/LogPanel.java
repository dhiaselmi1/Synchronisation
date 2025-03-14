import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class LogPanel extends VBox {

    // Conteneur pour les messages de log
    private VBox logContainer = new VBox(5);

    /**
     * Constructeur de la classe LogPanel.
     * Initialise le panneau de logs avec un titre et une zone de défilement.
     */
    public LogPanel() {
        super(10); // Espacement vertical entre les éléments
        setPadding(new Insets(10)); // Marge intérieure du panneau
        setPrefWidth(300); // Largeur préférée du panneau

        // Crée un label pour le titre "Logs"
        Label logsLabel = new Label("Logs");
        logsLabel.setStyle("-fx-font-weight: bold;"); // Style en gras

        // Applique une police monospace pour les logs (alignement des caractères)
        logContainer.setStyle("-fx-font-family: monospace;");

        // Crée un ScrollPane pour permettre le défilement des logs
        ScrollPane scrollPane = new ScrollPane(logContainer);
        scrollPane.setFitToWidth(true); // Ajuste la largeur du contenu
        scrollPane.setPrefHeight(300); // Hauteur préférée du ScrollPane
        scrollPane.vvalueProperty().bind(logContainer.heightProperty()); // Défilement automatique vers le bas

        // Ajoute le titre et le ScrollPane au panneau principal
        getChildren().addAll(logsLabel, scrollPane);
    }

    /**
     * Ajoute un message de log au conteneur.
     * Si le nombre de logs dépasse 100, le plus ancien est supprimé.
     * @param message Le message de log à ajouter.
     */
    public void addLog(String message) {
        Label logLabel = new Label(message); // Crée un label pour le message
        if (logContainer.getChildren().size() > 100) {
            logContainer.getChildren().remove(0); // Supprime le plus ancien log si nécessaire
        }
        logContainer.getChildren().add(logLabel); // Ajoute le nouveau log
    }
}
