package com.cccll.remoting.transport.netty.server;

import com.cccll.enumeration.RpcMessageType;
import com.cccll.enumeration.RpcResponseCode;
import com.cccll.factory.SingletonFactory;
import com.cccll.remoting.dto.RpcRequest;
import com.cccll.remoting.dto.RpcResponse;
import com.cccll.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义服务端的 ChannelHandler 来处理客户端发过来的数据。
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。详见《Netty进阶之路 跟着案例学 Netty》
 *
 * @author cccll
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    /**
     * 读取从客户端发送的消息，然后调用目标服务的目标方法并返回给客户端。
     *
     * TODO: 因考虑到RPC框架一般用于拆分服务，所以考虑到服务之间的调用会较为频繁，因此框架服务端与客户端使用的是长连接。
     *  长连接的弊端就是如果较多客户端连接服务端，则服务端就要维持大量与客户端的连接，这对服务端是一种负担，如果遇到
     *  某些客户端正常通信后长时间不再与服务端正常通信，只是发心跳请求维持链接，而服务端还要维持着链接则无疑是对服务端
     *  内存和机器性能的负担，所以可以考虑加个心跳计数器，每个Channel对应一个心跳数，每次有心跳信息过来就给对应Channel
     *  加1，非心跳请求过来就把对应Channel计数器置0，当心跳数达到值则把对应Channel关掉。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("server receive msg: [{}] ", msg);
            RpcRequest rpcRequest = (RpcRequest) msg;
            //如果是心跳请求则忽略
            if (rpcRequest.getRpcMessageType() == RpcMessageType.HEART_BEAT) {
                log.info("receive heat beat msg from client");
                return;
            }
            // 执行目标方法（客户端需要执行的方法）并返回方法结果
            Object result = rpcRequestHandler.handle(rpcRequest);
            log.info(String.format("server get result: %s", result.toString()));
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                //返回方法执行结果给客户端
                RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                //增加ChannelFutureListener.CLOSE_ON_FAILURE监听器，如果传送未成功，将会关闭此链接（Channel）
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCode.FAIL);
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                log.error("not writable now, message dropped");
            }
        } finally {
            //确保ByteBuf被释放，否则可能会出现内存泄漏
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}

