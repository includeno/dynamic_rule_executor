package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("rule_entity")
public class RuleEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String uuid;//全局唯一key
    private Integer parentId;//parent_id
    private Integer rootId;//根节点id
    private String key;//规则key
    private String value;//规则参数
    private String operation;//操作符
    private String des;//规则描述
    private BigDecimal sortIndex;//数据库中的sortIndex
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer valid;
}
