package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.Utils;
import com.alibaba.fastjson.JSONObject;

import java.util.Hashtable;

/**
 * 参数存储的Map对象
 * key：当前线程
 * value:被AES解密后的文明请求参数
 * @auther: wangzb
 * @date: 2019/6/16 15:06
 */
public class ArgumentsHashTable extends Hashtable<Thread,ArgumentData> {

    /**
     * 定义一个懒汉式的对象
     */
    private static ArgumentsHashTable argumentsHashTable = new ArgumentsHashTable();

    /**
     * 定义私有的构造方法
     */
    private ArgumentsHashTable() {
    }

    /**
     * 获取实例对象
     * @return
     */
    public static ArgumentsHashTable getInstance(){
        return argumentsHashTable;
    }

    /**
     * 获取请求参数对应的值
     * @param paramName 参数名称
     * @return
     */
    public Object getParamByKey(String paramName){
        ArgumentData argumentData = get(Thread.currentThread());
        if (!Utils.isEmpty(argumentData)){
            argumentData.setUseSize(argumentData.getUseSize() - 1);
            JSONObject params = argumentData.getParams();
            Object value = params.get(paramName);
            Integer useSize = argumentData.getUseSize();
            if (useSize<=0){
                //移除
                this.remove(Thread.currentThread());
            }
            return value;
        }
        return null;
    }
}
