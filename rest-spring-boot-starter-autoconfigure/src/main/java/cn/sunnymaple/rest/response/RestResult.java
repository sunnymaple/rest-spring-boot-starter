package cn.sunnymaple.rest.response;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import lombok.Data;

/**
 * 同一格式的响应参数
 * @author wangzb
 * @date 2019/6/5 11:46
 */
@Data
public class RestResult {
    /**
     * 响应结果的状态码,默认为200,200表示请求成功，其他为异常
     */
    private Integer status = HttpStatusEnum.OK.getStatus();
    /**
     * 响应结果信息
     */
    private String message = HttpStatusEnum.OK.getMessage();
    /**
     * 响应结果对象，所有接口返回的对象都存放在result对象中
     */
    private Object result;
    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public RestResult() {
    }

    public RestResult(Integer status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public RestResult(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public RestResult(String message, Object result) {
        this.message = message;
        this.result = result;
    }

    public RestResult(Object result) {
        this.result = result;
    }

    /**
     * result为null
     * @return
     */
    public static RestResult factory(){
        return new RestResult(HttpStatusEnum.OK.getMessage(),null);
    }

    /**
     * result为null
     * @return
     */
    public static RestResult factory(Object result){
        return new RestResult(HttpStatusEnum.OK.getMessage(),result);
    }
}
