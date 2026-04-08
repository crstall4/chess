package websocket.messages;

public class ErrorMessage extends ServerMessage {
    public String errorString;

    public ErrorMessage(String errorString) {
        super(ServerMessageType.ERROR);
        this.errorString = errorString;
    }
}