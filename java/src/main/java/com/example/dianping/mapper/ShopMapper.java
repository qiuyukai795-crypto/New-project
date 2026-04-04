package com.example.dianping.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.dianping.entity.ShopEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ShopMapper extends BaseMapper<ShopEntity> {

    @Select("""
            <script>
            SELECT DISTINCT s.id, s.name, s.category, s.rating, s.avg_price, s.district, s.address, s.description, s.signature_dish
            FROM shops s
            LEFT JOIN shop_tags st ON s.id = st.shop_id
            WHERE LOWER(s.name) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(s.category) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(s.district) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(s.description) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(st.tag_name) LIKE CONCAT('%', LOWER(#{keyword}), '%')
            ORDER BY s.id
            </script>
            """)
    List<ShopEntity> search(@Param("keyword") String keyword);

    @Select("""
            <script>
            SELECT DISTINCT s.id, s.name, s.category, s.rating, s.avg_price, s.district, s.address, s.description, s.signature_dish
            FROM shops s
            LEFT JOIN shop_tags st ON s.id = st.shop_id
            WHERE LOWER(s.name) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(s.category) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(s.district) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(s.description) LIKE CONCAT('%', LOWER(#{keyword}), '%')
               OR LOWER(st.tag_name) LIKE CONCAT('%', LOWER(#{keyword}), '%')
            ORDER BY s.id
            </script>
            """)
    IPage<ShopEntity> searchPage(IPage<ShopEntity> page, @Param("keyword") String keyword);
}
