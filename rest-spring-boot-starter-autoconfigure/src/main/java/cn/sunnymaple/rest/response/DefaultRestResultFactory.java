package cn.sunnymaple.rest.response;

/**
 * 默认的RestResult工厂类
 * @author wangzb
 * @date 2019/6/18 10:29
 */
public class DefaultRestResultFactory implements IRestResultFactory<RestResult>{

    /**
     * 响应成功时创建结果集
     *
     * @param result 响应结果对象
     * @return
     */
    @Override
    public RestResult success(Object result) {
        return new RestResult(result);
    }

    /**
     * 响应失败时创建结果集
     *
     * @param message 异常信息
     * @param status  状态码
     * @return
     */
    @Override
    public RestResult failure(String message, String status) {
        return new RestResult(message,status);
    }
}
