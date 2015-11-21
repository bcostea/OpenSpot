package ro.devhacks.terra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.devhacks.terra.model.Lot;
import ro.devhacks.terra.model.User;

public interface LotRepository extends JpaRepository<Lot, Long> {
    public Lot findByUser(User user);
}
