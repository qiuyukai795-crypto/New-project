package com.example.dianping.service.db.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dianping.entity.ShopEntity;
import com.example.dianping.mapper.ShopMapper;
import com.example.dianping.service.db.ShopDbService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopDbServiceImpl extends ServiceImpl<ShopMapper, ShopEntity> implements ShopDbService {

    @Override
    public List<ShopEntity> listByKeyword(String keyword) {
        return baseMapper.search(keyword);
    }

    @Override
    public IPage<ShopEntity> pageByKeyword(String keyword, long page, long pageSize) {
        return baseMapper.searchPage(new Page<>(page, pageSize), keyword);
    }
}
