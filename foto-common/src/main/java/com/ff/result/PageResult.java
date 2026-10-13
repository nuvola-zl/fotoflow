//package com.ff.result;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * 封装分页查询结果
// */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class PageResult implements Serializable {
//
//    private long total; //总记录数
//
//    private List records; //当前页数据集合
//
//}
package com.ff.result;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 * 兼容旧版，支持泛型
 */
@Data
public class PageResult<T> implements Serializable {

    private long total;      // 总记录数

    private List<T> records; // 当前页数据集合

    // 手动写无参构造器（替代 @NoArgsConstructor）
    public PageResult() {
    }

    // 手动写全参构造器（替代 @AllArgsConstructor）
    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}