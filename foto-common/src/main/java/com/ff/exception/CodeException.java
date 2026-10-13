package com.ff.exception;

public class CodeException extends RuntimeException {
    /** 通用错误 */
    public static final int GENERAL = 0;
    /** 参数错误 */
    public static final int PARAM_ERROR = 1;
    /** 未授权 */
    public static final int UNAUTHORIZED = 2;
    /** 禁止访问 */
    public static final int FORBIDDEN = 3;
    /** 资源不存在 */
    public static final int NOT_FOUND = 4;
    /** 业务冲突（如已注册） */
    public static final int CONFLICT = 5;
    /**没开始或未进行**/
    public static final int NOT_STARTED = 6;
    /** 已完成 */
    public static final int COMPLETED = 7;
    /**状态异常**/
    public static final int STATUS_ERROR = 8;
    /**未完成**/
    public static final int NOT_COMPLETED = 9;
}
