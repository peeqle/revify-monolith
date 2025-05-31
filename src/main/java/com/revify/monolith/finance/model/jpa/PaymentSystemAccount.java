package com.revify.monolith.finance.model.jpa;

import com.revify.monolith.finance.model.jpa.payment.Payment;
import com.revify.monolith.finance.model.jpa.payment.PaymentToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_system_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("BASE")
public class PaymentSystemAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long systemUserId;

    @NotBlank(message = "Account number cannot be blank")
    @Size(max = 255, message = "Account number cannot exceed 20 characters")
    private String accountId;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PaymentToken> paymentToken;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "Year of birth cannot be null")
    @Min(value = 1900, message = "Year of birth cannot be before 1900")
    @Max(value = 2100, message = "Year of birth cannot be in the future")
    private Long dobYear;

    @NotNull(message = "Month of birth cannot be null")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Long dobMonth;

    @NotNull(message = "Day of birth cannot be null")
    @Min(value = 1, message = "Day must be between 1 and 31")
    @Max(value = 31, message = "Day must be between 1 and 31")
    private Long dobDay;

    @NotNull(message = "Address cannot be null")
    private Address address;

    @NotNull(message = "Creation time cannot be null")
    private Long createdAt;

    private Boolean isDeleted;
    private Boolean isActive;

    private Boolean isReceiver = false;
    private Boolean isCustomer = false;

    @Data
    @Embeddable
    public static class Address {
        String city;
        //ISO 3166-1
        String country;
        String addressLine;
        String postalCode;
        /**
         * State, county, province, or
         */
        String state;
    }
}
