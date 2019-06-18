package cn.sunnymaple.rest.response;

/**
 * 默认的RestResult工厂类
 * @author wangzb
 * @date 2019/6/18 10:29
 */
public class DefaultRestResultFactory implements IRestResultFactory<DefaultRestResult>{
    /**
     * 响应成功时创建结果集
     *
     * @param result                   响应结果对象
     * @param beforeBodyWriteParameter beforeBodyWrite方法对应的参数
     * @throws Exception
     * @return
     */
    @Override
    public DefaultRestResult success(Object result, BeforeBodyWriteParameter beforeBodyWriteParameter) {
        //对于响应对象本身就是DefaultRestResult对象，则不再次封装，直接返回
        if (result instanceof DefaultRestResult){
            return (DefaultRestResult) result;
        }
        return new DefaultRestResult(result);
    }
    /**
     * 响应失败时创建结果集
     *
     * @param message                  异常信息
     * @param status                   状态码
     * @param beforeBodyWriteParameter beforeBodyWrite方法对应的参数
     * @return
     */
    @Override
    public DefaultRestResult failure(String message, Integer status, BeforeBodyWriteParameter beforeBodyWriteParameter) {
        return new DefaultRestResult(status,message);
    }
}
