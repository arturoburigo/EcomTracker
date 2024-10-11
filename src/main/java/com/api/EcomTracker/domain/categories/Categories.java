package com.api.EcomTracker.domain.categories;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "categories")
@Entity(name = "Categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Categories {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean active;

    public Categories(CategoryRegisterData data) {
        this.active = true;
        this.name = data.getName();
    }

    public void inactivate() {
        this.active = false;
    }

}
