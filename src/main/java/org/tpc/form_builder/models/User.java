package org.tpc.form_builder.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    @Builder.Default
    private String publicId = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String roleId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Builder.Default
    private boolean isActive = true;

}
