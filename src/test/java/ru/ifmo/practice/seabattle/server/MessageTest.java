package ru.ifmo.practice.seabattle.server;

import org.junit.Assert;
import org.junit.Test;

public class MessageTest {
    @Test
    public void getTypeTest() {
        Assert.assertEquals("Notice", new Message<Notice>(Notice.OpponentFound).getType());
    }
}
