# dynamic_rule_executor
配置化规则和规则执行器



# 基础类



## RuleElement

定义：规则的嵌套式结构，是系统内规则的通用表现形式。

其数组构成了多种规则，每一个对象构成一个tree，tree的列表构成了完整的规则。



## RuleEntity

定义：规则的扁平式数据存储结构 用于存储在数据库

每一个对象和其parent建立关联，无parent的对象是tree的根节点。



## 方法

### RuleElement与RuleEntity相互转化



从tree列表提取所有的
