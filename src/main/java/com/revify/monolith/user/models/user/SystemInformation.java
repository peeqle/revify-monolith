package com.revify.monolith.user.models.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SystemInformation implements Serializable {
    private String ip;
    private String browserAccess;
}
