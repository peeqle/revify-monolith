package com.revify.monolith.user.models.user.modification;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "app_user_email_modification")
public class AppUserEmailModification extends AppUserModification {
    private String email;
    private String emailAcceptanceKey;

    private Boolean accepted = false;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AppUserEmailModification that = (AppUserEmailModification) o;
        if (getEmail() != null && Objects.equals(getEmail(), that.getEmail())) {
            return true;
        }
        if (getEmailAcceptanceKey() != null && Objects.equals(getEmailAcceptanceKey(), that.getEmailAcceptanceKey())) {
            return true;
        }
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
