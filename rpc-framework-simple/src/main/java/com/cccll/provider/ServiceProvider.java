package com.cccll.provider;

import com.cccll.entity.RpcServiceProperties;


/**
 * 保存和提供服务实例对象。服务端使用。
 *
 * @author cccll
 * @createTime 2020年06月31日 16:52:00
 */
public interface ServiceProvider {

    /**
     *
     * 保存服务实例对象和服务实例对象实现的接口类的对应关系
     *
     * @param service              服务实例对象（service object）
     * @param serviceClass         服务实例对象实现的接口类
     * @param rpcServiceProperties 服务相关的属性
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    /**
     *
     * 获取服务实例对象
     *
     * @param rpcServiceProperties 服务相关的属性
     * @return 服务实例对象
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     * @param service              服务实例对象
     * @param rpcServiceProperties 服务相关的属性
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

    /**
     * @param service service object
     */
    void publishService(Object service);
}
