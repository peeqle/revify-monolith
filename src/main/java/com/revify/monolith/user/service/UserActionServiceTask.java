package com.revify.monolith.user.service;

import com.revify.monolith.commons.models.DTO.AppUserDTO;
import com.revify.monolith.user.models.UserActionTaskStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class UserActionServiceTask extends ServiceTask {
    private UserActionTaskStatus userActionTaskStatus;
    private AppUserDTO dto;
}

