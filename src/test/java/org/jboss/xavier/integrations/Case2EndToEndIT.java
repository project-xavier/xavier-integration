package org.jboss.xavier.integrations;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Case2EndToEndIT {

    @Test
    public void end2endTest() throws Exception {
        Integer a = 1;
        Integer b = 1;

        assertThat(a + b).isEqualTo(2);
    }
}
