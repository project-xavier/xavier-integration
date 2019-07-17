package org.jboss.xavier.integrations.jpa.service;

import org.jboss.xavier.integrations.route.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserService
{
    public User findUser()
    {
        return User.builder().firstTimeCreatingReports(true).build();
    }

}
