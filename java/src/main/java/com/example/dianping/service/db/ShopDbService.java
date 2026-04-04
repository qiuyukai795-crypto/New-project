package com.example.dianping.service.db;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dianping.entity.ShopEntity;

import java.util.List;

public interface ShopDbService extends IService<ShopEntity> {

    List<ShopEntity> listByKeyword(String keyword);

    IPage<ShopEntity> pageByKeyword(String keyword, long page, long pageSize);
}
