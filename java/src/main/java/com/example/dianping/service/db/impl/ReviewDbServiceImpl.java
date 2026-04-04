package com.example.dianping.service.db.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dianping.entity.ReviewEntity;
import com.example.dianping.mapper.ReviewMapper;
import com.example.dianping.service.db.ReviewDbService;
import org.springframework.stereotype.Service;

@Service
public class ReviewDbServiceImpl extends ServiceImpl<ReviewMapper, ReviewEntity> implements ReviewDbService {
}
