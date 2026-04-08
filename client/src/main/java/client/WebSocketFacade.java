package client;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler notificationHandler;

    public WebSocketFacade(String url, ServerMessageHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson gson = new Gson();
                    ServerMessage base = gson.fromJson(message, ServerMessage.class);
                    ServerMessage full = switch (base.getServerMessageType()) {
                        case LOAD_GAME -> gson.fromJson(message, LoadGameMessage.class);
                        case NOTIFICATION -> gson.fromJson(message, NotificationMessage.class);
                        case ERROR -> gson.fromJson(message, ErrorMessage.class);
                    };
                    notificationHandler.notify(full);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
    }

    public void sendCommand(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

}
