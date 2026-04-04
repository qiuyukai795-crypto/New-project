package com.example.dianping.mapper;

import com.example.dianping.entity.ShopTagEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

public interface ShopTagMapper {

    @Select("""
            <script>
            SELECT shop_id, tag_name, sort_order
            FROM shop_tags
            WHERE shop_id IN
            <foreach collection="shopIds" item="shopId" open="(" separator="," close=")">
                #{shopId}
            </foreach>
            ORDER BY shop_id, sort_order
            </script>
            """)
    List<ShopTagEntity> selectByShopIds(@Param("shopIds") Collection<Long> shopIds);

    @Delete("DELETE FROM shop_tags WHERE shop_id = #{shopId}")
    int deleteByShopId(@Param("shopId") Long shopId);

    @Insert("""
            INSERT INTO shop_tags (shop_id, tag_name, sort_order)
            VALUES (#{shopId}, #{tagName}, #{sortOrder})
            """)
    int insertTag(ShopTagEntity entity);
}
