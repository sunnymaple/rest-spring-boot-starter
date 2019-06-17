package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.Utils;
import com.alibaba.fastjson.JSONObject;

/**
 * 定义单例的ThreadLocal对象，运用存放当前线程的参数数据（ArgumentData）
 * 每个ThreadLocal内维护了一个ThreadLocalMap
 * key：当前线程
 * value:被AES解密后的文明请求参数，即ThreadLocal<ArgumentData>泛型ArgumentData对象
 * @auther: wangzb
 * @date: 2019/6/16 15:06
 */
public class ArgumentsThreadLocal extends ThreadLocal<ArgumentData> {
    /**
     * 定义一个懒汉式的对象
     */
    private static ArgumentsThreadLocal argumentsThreadLocal = new ArgumentsThreadLocal();

    /**
     * 定义私有的构造方法
     */
    private ArgumentsThreadLocal() {
    }

    /**
     * 获取实例对象
     * @return
     */
    public static ArgumentsThreadLocal getInstance(){
        return argumentsThreadLocal;
    }

    /**
     * 获取请求参数对应的值
     * @param paramName 参数名称
     * @return
     */
    public Object getParamByKey(String paramName){
        ArgumentData argumentData = get();
        if (!Utils.isEmpty(argumentData)){
            JSONObject params = argumentData.getParams();
            return params.get(paramName);
        }
        return null;
    }
}
