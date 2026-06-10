package com.api.ecomtracker.repository;

import com.api.ecomtracker.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByActiveTrue(Pageable pageable);
}
