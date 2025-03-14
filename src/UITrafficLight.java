import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.shape.Circle;

/**
 * Extension de TrafficLight pour gérer l'interface graphique.
 */
public class UITrafficLight extends TrafficLight {
    private Circle lightCircle;

    /**
     * Constructeur de UITrafficLight.
     * @param direction Direction du feu
     * @param running État d'exécution
     * @param semaphore Sémaphore pour la synchronisation
     * @param lightCircle Cercle représentant le feu dans l'interface
     */
    public UITrafficLight(String direction, AtomicBoolean running, Semaphore semaphore, Circle lightCircle) {
        super(running, semaphore, null, direction);
        this.lightCircle = lightCircle;
    }

    /**
     * Surcharge de la méthode changeState pour mettre à jour l'interface graphique.
     */
    @Override
    protected void changeState(String newState) {
        super.changeState(newState);
        
        // Mise à jour de la couleur du cercle
        if (lightCircle != null) {
            TrafficLightsPanel.updateLightColor(lightCircle, newState);
        }
    }
}
