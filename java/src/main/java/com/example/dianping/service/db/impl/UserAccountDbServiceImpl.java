package com.example.dianping.service.db.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dianping.entity.UserAccountEntity;
import com.example.dianping.mapper.UserAccountMapper;
import com.example.dianping.service.db.UserAccountDbService;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDbServiceImpl extends ServiceImpl<UserAccountMapper, UserAccountEntity> implements UserAccountDbService {
}
