package com.cccll.remoting.transport;

import com.cccll.remoting.dto.RpcRequest;

/**
 * send RpcRequest
 *
 */
public interface ClientTransport {
    /**
     * 发送rpc请求到服务器并获得结果
     *
     * @param rpcRequest message body
     * @return 来自服务器的返回值
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
