package com.cccll.remoting.dto;

import com.cccll.entity.RpcServiceProperties;
import com.cccll.enumeration.RpcMessageType;
import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private RpcMessageType rpcMessageType;
    //服务版本，主要是为后续不兼容升级提供可能
    private String version;
    //主要用于处理一个接口有多个类实现的情况
    private String group;

    public RpcServiceProperties toRpcProperties() {
        return RpcServiceProperties.builder().serviceName(this.getInterfaceName())
                .version(this.getVersion())
                .group(this.getGroup()).build();
    }
}
