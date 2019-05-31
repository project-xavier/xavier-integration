package org.jboss.xavier.integrations.route;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class InvalidTest {

    @Test
    public void getStatus() throws IOException {
        Assert.assertEquals("hello".toUpperCase(), "HELLO");
    }

}
