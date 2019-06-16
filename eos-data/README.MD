### 模型更新策略
`本文档主要说明对现有代码进行扩展，实现新模型的整合`

1.前端页面传递表单需要与后端相互对应。有两个Enum用于匹配前端传递的表单。表单数据从前端index.html传递过来，将前端信息封装为
OrderInputDTO，可以调用`OrderInputDTOConvert.doForward(OrderDataGranule)`返回一个Order的实体对象，这个实体对象便可以直接存储
到数据库中。

步骤一，在index.html遥感模型selector中添加遥感模型信息。
步骤二，在LandsatEnum中添加新模型匹配的productCode。
步骤三，数据返还给前端时候不需要进行信息的修改。
步骤四，

再使用到OrderInputDTO进行封装再存入数据库中，封装操作就是将对应字段进行转换，需要使用
