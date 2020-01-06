package org.jboss.xavier.integrations.jpa.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GreetingsService {

    public String greetings(String username, String queryValue, Map<String, String> body) {
        return "Greetings to username=" + username + " queryValue=" + queryValue + " body=" + body;
    }

}
