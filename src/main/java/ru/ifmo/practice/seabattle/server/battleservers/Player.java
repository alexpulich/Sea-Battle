package ru.ifmo.practice.seabattle.server.battleservers;

import ru.ifmo.practice.seabattle.battle.*;
import ru.ifmo.practice.seabattle.exceptions.FieldAlreadySetException;
import ru.ifmo.practice.seabattle.server.Command;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.HashSet;

class Player implements Gamer {
    private String nickName;
    private Session session;
    private FirstField firstField = null;
    private SecondField secondField = null;
    private BattleInfo battleInfo = null;
    private Command lastCommand = null;
    private FirstFieldBuilder firstFieldBuilder = null;
    private boolean turn;
    private boolean inBattle = false;
    private boolean readyToBattle = false;

    private Coordinates shot = null;
    private Coordinates lastShot = null;
    private boolean firstTurn = true;
    private HashSet<Coordinates> blackList = new HashSet<>();

    synchronized void setShot(Coordinates shot) {
        if (!blackList.contains(shot)) {
            blackList.add(new Coordinates(shot.getX(), shot.getY()));
            this.shot = shot;
            this.notifyAll();
        } else throw new IllegalArgumentException("В данную клетку уже стреляли");
    }

    public Player(String nickName, FirstField firstField, SecondField secondField) {
        this.nickName = nickName;
        this.firstField = firstField;
        this.secondField = secondField;
    }

    public Player(Session session, String nickName) {
        this.session = session;
        this.nickName = nickName;
        turn = false;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public FirstField getFirstField() {
        return firstField;
    }

    public FirstFieldBuilder getFirstFieldBuilder() {
        return firstFieldBuilder;
    }

    public SecondField getSecondField() {
        return secondField;
    }

    public Session getSession() {
        return session;
    }

    public BattleInfo getBattleInfo() {
        return battleInfo;
    }

    public Command popLastCommand() {
        Command result = lastCommand;
        lastCommand = null;
        return result;
    }

    public boolean popTurn() {
        if (turn) {
            turn = false;
            return true;
        } else return false;
    }

    public boolean isInBattle() {
        return inBattle;
    }

    public boolean isReadyToBattle() {
        return readyToBattle;
    }

    public void readyToBattle() throws FieldAlreadySetException {
        if (!readyToBattle) readyToBattle = true;
        else throw new FieldAlreadySetException();
    }

    public void setFirstFieldBuilder(FirstFieldBuilder firstFieldBuilder) throws FieldAlreadySetException {
        if (!isInBattle()) this.firstFieldBuilder = firstFieldBuilder;
        else throw new FieldAlreadySetException();
    }

    public void createFirstField() throws FieldAlreadySetException {
        if (!isInBattle()) firstField = firstFieldBuilder.create();
        else throw new FieldAlreadySetException();
    }

    public void setSecondField(SecondField secondField) throws FieldAlreadySetException {
        if (!isInBattle()) this.secondField = secondField;
        else throw new FieldAlreadySetException();
    }

    public void setBattleInfo(BattleInfo battleInfo) throws FieldAlreadySetException {
        if (!isInBattle()) {
            this.battleInfo = battleInfo;
            inBattle = true;
        } else throw new FieldAlreadySetException();
    }

    public void setLastCommand(Command lastCommand) throws FieldAlreadySetException {
        if (this.lastCommand == null) this.lastCommand = lastCommand;
        else throw new FieldAlreadySetException();
    }

    public void yourTurn() {
        turn = true;
    }

    @Override
    synchronized public void setLastRoundResult(HashSet<Coordinates> resultOfPreviousShot) {
        HashMap<Coordinates, Cell> secondFieldChanges = new HashMap<>();

        if (!firstTurn) {
            if (resultOfPreviousShot == null) secondFieldChanges.put(lastShot, Cell.Miss);
            else {
                secondFieldChanges.put(lastShot, Cell.Hit);
                if (resultOfPreviousShot.size() > 1)
                    resultOfPreviousShot.forEach((coordinates) -> {
                        if (!coordinates.equals(lastShot)) {
                            secondFieldChanges.put(coordinates, Cell.Miss);
                        }
                    });
            }
            secondField.change(secondFieldChanges);
        }
    }

    @Override
    synchronized public Coordinates getShot() {
        try {
            if (shot == null) {
                this.wait();
            }
        } catch (InterruptedException e) {
            return null;
        }

        lastShot = new Coordinates(shot.getX(), shot.getY());
        shot = null;
        firstTurn = false;

        return new Coordinates(lastShot.getX(), lastShot.getY());
    }

    @Override
    protected void finalize() throws Throwable {
        if (session.isOpen()) session.close();
        super.finalize();
    }
}
