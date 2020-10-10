package com.cccll.remoting.transport.socket;

import com.cccll.factory.SingletonFactory;
import com.cccll.remoting.dto.RpcRequest;
import com.cccll.remoting.dto.RpcResponse;
import com.cccll.remoting.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author cccll
 * @createTime 2020年06月13日 07:20:00
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {
    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;


    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            // 读取出 rpcRequest
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 交由给 rpcRequest 处理类去处理
            Object result = rpcRequestHandler.handle(rpcRequest);
            // 返回RpcResponse给客户端
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }

}