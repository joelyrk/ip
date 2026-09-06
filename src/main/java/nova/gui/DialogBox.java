package nova.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one message with an avatar identifying its speaker.
 */
public class DialogBox extends HBox {
    private static final double MAXIMUM_DIALOG_WIDTH = 340.0;
    private static final String USER_INITIAL = "Y";
    private static final String NOVA_INITIAL = "N";

    private DialogBox(String message, boolean isUser) {
        Label dialog = new Label(message);
        dialog.setWrapText(true);
        dialog.setMaxWidth(MAXIMUM_DIALOG_WIDTH);
        dialog.getStyleClass().add(isUser ? "user-dialog" : "nova-dialog");

        Label avatar = new Label(isUser ? USER_INITIAL : NOVA_INITIAL);
        avatar.getStyleClass().addAll("avatar", isUser ? "user-avatar" : "nova-avatar");

        getStyleClass().add("dialog-box");
        setAlignment(Pos.TOP_RIGHT);
        if (isUser) {
            getChildren().addAll(dialog, avatar);
        } else {
            setAlignment(Pos.TOP_LEFT);
            getChildren().addAll(avatar, dialog);
        }
    }

    /**
     * Creates a dialog showing a command entered by the user.
     *
     * @param message command text to display.
     * @return user-side dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, true);
    }

    /**
     * Creates a dialog showing Nova's response.
     *
     * @param message response text to display.
     * @return Nova-side dialog box.
     */
    public static DialogBox getNovaDialog(String message) {
        return new DialogBox(message, false);
    }
}
