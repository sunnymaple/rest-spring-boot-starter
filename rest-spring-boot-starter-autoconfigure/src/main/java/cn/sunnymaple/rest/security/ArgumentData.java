package cn.sunnymaple.rest.security;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 解密后的数据
 * @auther: wangzb
 * @date: 2019/6/17 00:14
 */
@Data
public class ArgumentData {
    /**
     * 客户端唯一识别id
     */
    private String appKey;
    /**
     * 对应接口的参数，key参数名称，value：参数值
     */
    private JSONObject params;

    public ArgumentData() {
    }

    public ArgumentData(String appKey, JSONObject params) {
        this.appKey = appKey;
        this.params = params;
    }
}
