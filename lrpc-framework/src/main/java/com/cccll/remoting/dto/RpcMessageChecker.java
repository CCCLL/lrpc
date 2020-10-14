package com.cccll.remoting.dto;

import com.cccll.enumeration.RpcErrorMessage;
import com.cccll.enumeration.RpcResponseCode;
import com.cccll.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

/**
 * 校验 RpcRequest 和 RpcRequest
 *
 * @author cccll
 * @createTime 2020年06月28日 19:05:00
 */
@Slf4j
public final class RpcMessageChecker {
    private static final String INTERFACE_NAME = "interfaceName";

    private RpcMessageChecker() {
    }

    public static void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessage.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessage.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessage.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
