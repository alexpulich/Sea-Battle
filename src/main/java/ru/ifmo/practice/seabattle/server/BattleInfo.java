package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.battle.Battle;

class BattleInfo {
    private Battle battle;
    private Thread thread;

    public BattleInfo(Battle battle, Thread thread) {
        this.battle = battle;
        this.thread = thread;
    }

    public Battle getBattle() {
        return battle;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    protected void finalize() throws Throwable {
        if (thread.isAlive()) thread.interrupt();
        super.finalize();
    }
}
