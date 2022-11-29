package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ITEM_T")
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Введите название предмета.")
    private String name;
    @NotBlank(message = "Введите описание предмета.")
    private String description;
    private Long ownerId;
    private Boolean available;
    private Long requestId;

    public Item(Long id, String name, String description,
                Long ownerId, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.available = available;
    }

}
