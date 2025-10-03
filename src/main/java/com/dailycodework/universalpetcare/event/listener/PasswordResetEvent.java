package com.dailycodework.universalpetcare.event.listener;

import com.dailycodework.universalpetcare.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PasswordResetEvent extends ApplicationEvent {
    private final User user;
    private final String token;

    public PasswordResetEvent(Object source, User user, String token) {
        super(source);
        this.user = user;
        this.token = token;
    }
}
