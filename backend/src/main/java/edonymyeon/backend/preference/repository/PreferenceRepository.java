package edonymyeon.backend.preference.repository;

import edonymyeon.backend.preference.domain.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {

}
