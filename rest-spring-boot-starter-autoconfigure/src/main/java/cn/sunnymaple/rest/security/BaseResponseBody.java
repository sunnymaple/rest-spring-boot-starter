package cn.sunnymaple.rest.security;

import lombok.Data;

/**
 * 响应体
 * @author wangzb
 * @date 2019/6/17 9:41
 */
@Data
public class BaseResponseBody {

    /**
     * AES密钥
     */
    private String key;

    /**
     * 响应结果的json字符串
     */
    private String data;

    public BaseResponseBody() {
    }

    public BaseResponseBody(String key, String data) {
        this.key = key;
        this.data = data;
    }
}
