package com.api.EcomTracker.domain.categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long>{
    Page<Categories> findAllByActiveTrue(Pageable pagination);
}
