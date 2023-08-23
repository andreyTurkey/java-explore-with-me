package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppRepository extends JpaRepository<App, Integer> {

    App getAppByName (String name);

    Boolean existsByName(String name);
}
