**记录一下开发过程中的想法以及遇到的问题**

数据路径：
    1.本地landasat8原始数据 lc8/raw/ + /path/row/filename
    2.解压后数据路径 lc8/unzipped/
    3.大气校正后数据路径 lc8/sr/
    4.计算ndvi后数据路径 lc8/ndvi/


后端传递前端哪些数据。先考虑直接返回dataGranule，可能还有部分数据是用不上的，后期再进行修改。
数据的解压应该提前处理好，不该交由用户请求后再执行。

doLasrc(Integer orderId)
根据orderIds,查询得到多个dataGranule对象，再取出里面的path，循环执行doLasrc



Lasrc步骤
由OrderDataGranule进行查询，得到用户订单对应的所有dataGranule，然后
    1.判断source是否为本地(目前只考虑本地数据情况)。
    2.判断其formatCode，是否需要解压，如果是tgz就需要调用tgz的方法进行解压缩到指定路径(根据读取路径进行调整),
      解压后的路径根据原路径进行更改。
      `由于解压信息没有体现在ID中,解压后修改内容包括：
      dataGranuleId,productCode,formatCode,dataPath,四部分
      
      `
      在将这些解压后的数据存储到本地，同时在DataGranule表中写入数据信息。
    
    
    3.读取数据路径，执行Lasrc，