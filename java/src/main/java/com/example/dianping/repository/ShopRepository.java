package com.example.dianping.repository;

import com.example.dianping.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {

    @Query("""
            select distinct s
            from ShopEntity s
            left join s.tags tag
            where lower(s.name) like lower(concat('%', :keyword, '%'))
               or lower(s.category) like lower(concat('%', :keyword, '%'))
               or lower(s.district) like lower(concat('%', :keyword, '%'))
               or lower(s.description) like lower(concat('%', :keyword, '%'))
               or lower(tag) like lower(concat('%', :keyword, '%'))
            order by s.id
            """)
    List<ShopEntity> search(@Param("keyword") String keyword);

    List<ShopEntity> findByIdNot(Long id);
}
