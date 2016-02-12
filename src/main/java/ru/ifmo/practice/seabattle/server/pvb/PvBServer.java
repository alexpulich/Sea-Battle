package ru.ifmo.practice.seabattle.server.pvb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.ifmo.practice.seabattle.battle.Cell;
import ru.ifmo.practice.seabattle.battle.Coordinates;
import ru.ifmo.practice.seabattle.battle.Field;
import ru.ifmo.practice.seabattle.battle.bot.Bot;
import ru.ifmo.practice.seabattle.server.Player;

import javax.servlet.http.HttpServlet;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

@ServerEndpoint("/pvbserver")
public class PvBServer extends HttpServlet {
    private static HashSet<Session> sessions = new HashSet<>();
    private static HashMap<Session, Player> players = new HashMap<>();
    private static HashMap<Session, Bot> bots = new HashMap<>();
    private static HashMap<Player, Coordinates> shots = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        sessions.add(session);
        players.put(session, new Player("Игрок", new Field()));
        bots.put(session, new Bot("Бот"));
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session);
        players.remove(session);
        bots.remove(session);
    }

    @OnMessage
    public void parseMessage(String message, Session session) throws IOException {
        if (message.equals("random place")) {
            randomField(session);
        } else if (message.length() > 20) {
            Cell[][] field = new Gson().fromJson(message, Cell[][].class);
            placeReceivedShips(field, session);
        } else {
            Coordinates shot = new Gson().fromJson(message, Coordinates.class);
            nextRound(shot, session);
        }
    }

    private void sendMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    private void randomField(Session session) throws IOException {
        Player player = players.get(session);
        Field field = player.getField();
        field.placeShipsRandom();
        Cell[][] fieldCells = field.getCurrentConditions();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        sendMessage(gson.toJson(fieldCells), session);
    }

    private void placeReceivedShips(Cell[][] fieldCells, Session session) throws IOException {

    }

    private void nextRound(Coordinates shot, Session session) throws IOException {

    }
}
