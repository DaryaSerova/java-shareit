package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    @Query(
            value = "FROM Item it " +
                    "WHERE LOWER (it.name) LIKE LOWER (concat('%',:text,'%')) " +
                    "OR LOWER (it.description) LIKE LOWER (concat('%',:text,'%')) " +
                    "AND it.available = TRUE")
    List<Item> findByNameOrDescription(@Param("text") String text);

}

