package ru.ifmo.practice.seabattle.server;

class Log {
    private static class LogHolder {
        private static Log instance = new Log();
    }

    public static Log getInstance() {
        return LogHolder.instance;
    }

    private Log() {}

    public void sendMessage(Class server, String id, String message) {
        System.out.println(server.getSimpleName() + ": "
                + Thread.currentThread().getName()
                + ": " + id + ": " + message);
    }

    public void sendMessage(Class server, String message) {
        System.out.println(server.getSimpleName() + ": "
                + Thread.currentThread().getName()
                + ": " + message);
    }
}
