package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, Integer> {

    App getAppByName(String name);

    Boolean existsByName(String name);
}
