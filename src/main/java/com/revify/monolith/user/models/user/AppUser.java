package com.revify.monolith.user.models.user;

import com.revify.monolith.commons.ElasticContext;
import com.revify.monolith.commons.models.user.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

@Data
@Entity
@Table(name = "sys_app_user", schema = "system", indexes = {
        @Index(name = "USER_USERNAME_INDEX", columnList = "username", unique = true),
        @Index(name = "USER_PHONE_NUMBER_INDEX", columnList = "user_phone_number", unique = true),
        @Index(name = "USER_EMAIL_INDEX", columnList = "user_email", unique = true),
})
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "username")
    private String username;
    @Column(unique = true, name = "phone_number")
    private String phoneNumber;
    @Column(unique = true, name = "email")
    private String email;

    private String firstName;
    private String lastName;
    private String middleName;
    //todo make submodule for passport data, migrate fields, dont forget ab rpc models

    @Column(unique = true, name = "keycloak_id")
    private String keycloakId;

    private boolean enabled = false;
    private boolean locked = false;

    private UserRole clientUserRole = UserRole.CLIENT;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<AppUser> friends = new HashSet<>();
    //consider migration to neo4j
    @OneToMany
    private Set<AppUser> favourite = new HashSet<>();
    @OneToMany
    private Set<AppUser> blocked = new HashSet<>();

    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private AppUserOptions appUserOptions = new AppUserOptions();
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private FinancialInformation financialInformation = new FinancialInformation();
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private SystemInformation systemInformation = new SystemInformation();

    private Long createdOnServerUtc = System.currentTimeMillis();
    private Long updatedOnServerUtc = System.currentTimeMillis();

    public AppUser(String username, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.username = username;
    }

    public AppUser(Long id) {
        this.id = id;
    }

    protected AppUser() {
    }

    public Map<String, String> getCommonUserInfo() {
        Map<String, String> userMap = new HashMap<>();

        userMap.put(ElasticContext.USERNAME, this.username);
        userMap.put(ElasticContext.COMMON_INFORMATION, this.username);
        userMap.put(ElasticContext.ROLE, this.clientUserRole.name());

        return userMap;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AppUser appUser = (AppUser) o;
        return getId() != null && Objects.equals(getId(), appUser.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public String getCommonUserName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    public void addFriend(AppUser friend) {
        this.friends.add(friend);
        friend.getFriends().add(this);
    }

    public void removeFriend(AppUser friend) {
        this.friends.remove(friend);
        friend.getFriends().remove(this);
    }
}

