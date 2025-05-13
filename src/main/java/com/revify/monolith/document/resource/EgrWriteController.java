package com.revify.monolith.document.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/egr")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_MODERATOR')")
public class EgrWriteController {
    //update and create methods for licenses and system-agreements
}
