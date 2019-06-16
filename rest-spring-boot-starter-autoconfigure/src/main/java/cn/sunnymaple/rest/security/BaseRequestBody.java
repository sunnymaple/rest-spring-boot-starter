package cn.sunnymaple.rest.security;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 请求提参数
 * @auther: wangzb
 * @date: 2019/6/16 11:25
 */
@Data
public class BaseRequestBody {
    /**
     * AES密钥
     */
    @NotNull
    private String key;

    /**
     * 唯一确定客户端的appKey,
     */
    @NotNull
    private String appKey;

    /**
     * 请求参数的json字符串
     * 无请求参数时为空
     */
    private String data;

    /**
     * 数字签名，可以为空
     */
    private String sign;

    /**
     * 获取签名参数对象
     * @return
     */
    public String getSignParam(){
        JSONObject sign = new JSONObject();
        sign.put("key",key);
        sign.put("appKey",appKey);
        sign.put("data",data);
        return sign.toJSONString();
    }
}
