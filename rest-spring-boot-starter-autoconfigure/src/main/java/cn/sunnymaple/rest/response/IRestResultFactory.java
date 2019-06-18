package cn.sunnymaple.rest.response;

/**
 * RestResult工厂
 * @author wangzb
 * @date 2019/6/18 10:01
 */
public interface IRestResultFactory<T extends RestResult> {
    /**
     * 响应成功时创建结果集
     * @param result 响应结果对象
     * @return
     */
    T success(Object result);

    /**
     * 响应失败时创建结果集
     * @param message 异常信息
     * @param status 状态码
     * @return
     */
    T failure(String message,String status);
}
