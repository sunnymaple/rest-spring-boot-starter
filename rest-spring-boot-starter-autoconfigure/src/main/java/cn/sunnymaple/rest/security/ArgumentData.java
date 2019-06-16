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
     * 可以使用的次数,初始为参数的个数，userSize为0时，则该对象从ArgumentsHashTable中移除
     */
    private Integer useSize;
    /**
     * 对应接口的参数，key参数名称，value：参数值
     */
    private JSONObject params;

    public ArgumentData() {
    }

    public ArgumentData(JSONObject params) {
        this.params = params;
    }
}
