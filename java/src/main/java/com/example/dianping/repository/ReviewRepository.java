package com.example.dianping.repository;

import com.example.dianping.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByShopIdOrderByCreatedAtDescIdDesc(Long shopId);

    long countByShopId(Long shopId);
}
