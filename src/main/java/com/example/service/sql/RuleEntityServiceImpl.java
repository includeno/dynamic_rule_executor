package com.example.service.sql;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.RuleEntity;
import com.example.mapper.RuleEntityMapper;
import org.springframework.stereotype.Service;

@Service
public class RuleEntityServiceImpl extends ServiceImpl<RuleEntityMapper, RuleEntity> implements RuleEntityServiceService {
}
