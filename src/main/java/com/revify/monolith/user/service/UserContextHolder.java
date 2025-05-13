package com.revify.monolith.user.service;

import com.revify.monolith.user.models.user.AppUser;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Data
@Component
@SessionScope
public class UserContextHolder {

    private AppUser appUser;
}
