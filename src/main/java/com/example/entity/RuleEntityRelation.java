package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RuleEntityRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String uuid;//全局唯一key
    private Integer parentId;//parent_id
    private String key;//规则key
    private String value;//规则参数
    private String operation;//操作符
    private String des;//规则描述
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer valid;
}
