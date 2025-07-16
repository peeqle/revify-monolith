package com.revify.monolith.user.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.ValidationContext;
import com.revify.monolith.commons.exceptions.UserCreationException;
import com.revify.monolith.commons.exceptions.UserPersistenceException;
import com.revify.monolith.commons.geolocation.CountryCode;
import com.revify.monolith.commons.messaging.KafkaTopic;
import com.revify.monolith.commons.messaging.dto.finance.RecipientCreation;
import com.revify.monolith.commons.models.DTO.AppUserDTO;
import com.revify.monolith.commons.models.user.RegisterRequest;
import com.revify.monolith.commons.models.user.UserRole;
import com.revify.monolith.keycloak.KeycloakService;
import com.revify.monolith.user.models.UserActionTaskStatus;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.models.user.AppUserOptions;
import com.revify.monolith.user.models.user.SystemInformation;
import com.revify.monolith.user.models.user.additional.rating.UserRating;
import com.revify.monolith.user.service.data.AppUserWriteRepository;
import com.revify.monolith.user.service.phone_messaging.PhoneInteractionService;
import com.revify.monolith.user.service.util.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;


@Slf4j
@Service
@RequiredArgsConstructor
public class WriteUserService extends CrudService<AppUser> {

    private final AppUserWriteRepository repository;

    private final UserValidator userValidator;

    private final KeycloakService keycloakService;

    private final PhoneInteractionService phoneInteractionService;

    private final AppUserWriteRepository appUserWriteRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Queue<ServiceTask> serviceTaskQueue = new PriorityQueue<>((a, b) -> Math.toIntExact(a.getCreatedAt() - b.getCreatedAt()));

    private final Gson gson = new GsonBuilder().create();


    @Async
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkTasks() {
        for (ServiceTask serviceTask : serviceTaskQueue) {
            if (serviceTask instanceof UserActionServiceTask actionServiceTask) {
                taskDelete(actionServiceTask);
            }
        }
    }

    public List<ValidationContext> validate(RegisterRequest validateRegisterRequest) {
        return userValidator.validateRequest(validateRegisterRequest);
    }

    @Transactional
    public AppUser tryCreateUser(RegisterRequest registerRequest) {
        List<ValidationContext> validationContext = userValidator.validateRequest(registerRequest);
        if (!validationContext.isEmpty()) {
            throw new ValidationException(validationContext);
        }
        AppUser storedUser = store(mapUser(registerRequest));
        if (storedUser == null) {
            throw new UserCreationException("Failed to store user in service database.");
        }

        try {
            String keycloakId = keycloakService.registerUser(storedUser, registerRequest.getPassword());
            storedUser.setKeycloakId(keycloakId);

            storedUser = updateUser(storedUser.getId(), storedUser);
            if (storedUser != null) {
                kafkaTemplate.send(KafkaTopic.RECIPIENT_CREATION, gson.toJson(RecipientCreation.from(storedUser)));
            }
            return storedUser;
        } catch (Exception e) {
            deleteUser(storedUser.getId());
            throw new RuntimeException("Timeout while creating Keycloak user.", e);
        }
    }

    @Transactional
    public HttpStatus enableUser(AppUser appUser) {
        appUser.setEnabled(true);
        store(appUser);

        try {
            keycloakService.changeUserAvailability(appUser.getUsername(), true);
        } catch (Exception e) {
            log.error("Cannot update user after code being accepted.", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        phoneInteractionService.removeAllForUser(appUser);
        return HttpStatus.OK;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AppUser updateUser(Long userId, AppUser updatedData) {
        AppUser existingUser = appUserWriteRepository.findById(userId)
                .orElseThrow(() -> new UserPersistenceException("User not found with ID: " + userId));

        if (updatedData.getUsername() != null) {
            existingUser.setUsername(updatedData.getUsername());
        }
        if (updatedData.getLastName() != null) {
            existingUser.setLastName(updatedData.getLastName());
        }
        if (updatedData.getFirstName() != null) {
            existingUser.setFirstName(updatedData.getFirstName());
        }
        if (updatedData.getMiddleName() != null) {
            existingUser.setMiddleName(updatedData.getMiddleName());
        }
        if (updatedData.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedData.getPhoneNumber());
        }
        if (updatedData.getEmail() != null) {
            existingUser.setEmail(updatedData.getEmail());
        }
        if (updatedData.getKeycloakId() != null) {
            existingUser.setKeycloakId(updatedData.getKeycloakId());
        }
        existingUser.setEnabled(updatedData.isEnabled());
        existingUser.setLocked(updatedData.isLocked());

        if (updatedData.getFavourite() != null) {
            existingUser.setFavourite(updatedData.getFavourite());
        }
        if (updatedData.getBlocked() != null) {
            existingUser.setBlocked(updatedData.getBlocked());
        }
        if (updatedData.getAppUserOptions() != null) {
            existingUser.setAppUserOptions(updatedData.getAppUserOptions());
        }
        if (updatedData.getUserRole() != null) {
            existingUser.setUserRole(updatedData.getUserRole());
        }

        existingUser.setUpdatedOnServerUtc(System.currentTimeMillis());

        return appUserWriteRepository.save(existingUser);
    }

    public AppUser mapUser(RegisterRequest registerRequest) {
        Assert.notNull(registerRequest, "Register request cannot be null");

        AppUser appUser = new AppUser(registerRequest.getUsername(), registerRequest.getPhoneNumber());
        appUser.setEmail(registerRequest.getEmail());
        appUser.setFirstName(registerRequest.getFirstName());
        appUser.setLastName(registerRequest.getLastName());
        appUser.setUserRole(UserRole.valueOf(registerRequest.getUserRole()));

        AppUserOptions appUserOptions = new AppUserOptions();
        appUserOptions.setUserRating(UserRating.defaultRating());
        appUserOptions.setResidence(CountryCode.getCountryCode(registerRequest.getResidence()));
        appUserOptions.setAddress(new AppUserOptions.Address(registerRequest.getStreet(),
                registerRequest.getCity(),
                registerRequest.getRegion(),
                registerRequest.getPostalCode(),
                registerRequest.getApartmentHouse(),
                registerRequest.getCountry()));
        appUser.setAppUserOptions(appUserOptions);
        appUser.setDob(registerRequest.getDay());
        appUser.setMob(registerRequest.getMonth());
        appUser.setYob(registerRequest.getYear());
        {
            LocalDate birthDate = LocalDate.of(registerRequest.getYear(), registerRequest.getMonth(), registerRequest.getDay());
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(birthDate, currentDate);
            appUser.setAge(period.getYears());
        }

        SystemInformation systemInformation = new SystemInformation();
        systemInformation.setIp(registerRequest.getIp());
        appUser.setSystemInformation(systemInformation);

        return appUser;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(Long userId) {
        try {
            keycloakService.deleteUser(userId);
            appUserWriteRepository.deleteById(userId);
        } catch (Exception e) {
            log.warn("Cannot delete user {}", userId);
            serviceTaskQueue.add(UserActionServiceTask.builder()
                    .userActionTaskStatus(UserActionTaskStatus.DELETE_USER)
                    .dto(AppUserDTO.builder().id(userId).build()).build());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(String username, String email) {
        try {
            if (keycloakService.deleteUser(email, username)) {
                appUserWriteRepository.deleteAllByEmailAndUsername(email, username);
            }
            log.info("Successfully deleted user {}", username);
        } catch (Exception e) {
            log.warn("Cannot delete user {}", username);
            serviceTaskQueue.add(UserActionServiceTask.builder()
                    .userActionTaskStatus(UserActionTaskStatus.DELETE_USER)
                    .dto(AppUserDTO.builder()
                            .email(email)
                            .username(username).build()).build());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AppUser saveUser(AppUser appUser) {
        return this.repository.save(appUser);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void taskDelete(UserActionServiceTask actionServiceTask) {
        if (Objects.requireNonNull(actionServiceTask.getUserActionTaskStatus()) == UserActionTaskStatus.DELETE_USER) {
            if (actionServiceTask.getDto().getId() != null) {
                deleteUser(actionServiceTask.getDto().getId());
            } else {
                deleteUser(actionServiceTask.getDto().getUsername(), actionServiceTask.getDto().getEmail());
            }
        }
    }

    @Override
    public JpaRepository<AppUser, Long> getRepository() {
        return repository;
    }
}
