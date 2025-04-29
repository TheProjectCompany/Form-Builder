package org.tpc.form_builder.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpc.form_builder.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    Optional<User> findByPublicIdAndIsActiveTrue(String publicId);
}
