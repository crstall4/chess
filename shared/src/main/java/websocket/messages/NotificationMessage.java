package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public String notificationString;

    public NotificationMessage(String notificationString) {
        super(ServerMessageType.NOTIFICATION);
        this.notificationString = notificationString;
    }
}