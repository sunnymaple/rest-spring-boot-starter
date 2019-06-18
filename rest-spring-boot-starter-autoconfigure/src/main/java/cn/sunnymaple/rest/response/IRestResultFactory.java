package cn.sunnymaple.rest.response;

/**
 * RestResult工厂
 * @author wangzb
 * @date 2019/6/18 10:01
 */
public interface IRestResultFactory<T extends IRestResult> {
    /**
     * 响应成功时创建结果集
     * @param result 响应结果对象
     * @param beforeBodyWriteParameter beforeBodyWrite方法对应的参数
     * @return
     */
    T success(Object result,BeforeBodyWriteParameter beforeBodyWriteParameter);

    /**
     * 响应失败时创建结果集
     * @param message 异常信息
     * @param status 状态码
     * @param beforeBodyWriteParameter beforeBodyWrite方法对应的参数
     * @return
     */
    T failure(String message,Integer status,BeforeBodyWriteParameter beforeBodyWriteParameter);
}
