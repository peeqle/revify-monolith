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
@Table(name = "app_user_phone_modification")
public class AppUserPhoneModification extends AppUserModification {
    private String phone;
    private String phoneAcceptanceKey;

    private Boolean accepted = false;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AppUserPhoneModification that = (AppUserPhoneModification) o;
        if (getPhone() != null && Objects.equals(getPhone(), that.getPhone())) {
            return true;
        }
        if (getPhoneAcceptanceKey() != null && Objects.equals(getPhoneAcceptanceKey(), that.getPhoneAcceptanceKey())) {
            return true;
        }
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
