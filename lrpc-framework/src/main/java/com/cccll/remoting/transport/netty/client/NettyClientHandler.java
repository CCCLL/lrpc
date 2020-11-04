package com.cccll.remoting.transport.netty.client;

import com.cccll.enumeration.RpcMessageType;
import com.cccll.factory.SingletonFactory;
import com.cccll.remoting.dto.RpcRequest;
import com.cccll.remoting.dto.RpcResponse;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 自定义客户端ChannelHandler以处理服务器发送的数据
 *
 * <p>
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ，{@link SimpleChannelInboundHandler} 内部的
 * channelRead 方法会替你释放 ByteBuf ，避免可能导致的内存泄露问题。详见《Netty进阶之路 跟着案例学 Netty》
 *
 * @author cccll
 * @createTime 2020年07月01日 22:18:00
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * 利用Netty提供的高水位机制，对客户端做流控，避免netty发送队列积压发生OOM，
     * 当发送队列发送的字节数组到达高水位时，对应channel变为不可写状态，注意在此状态下调用write方法仍可将消息
     * 加入待发送队列，所以须在代码中对channel状态做判断
     *
     */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) {
//        //此值须根据业务的QPS规划、客户端处理性能、网络带宽、链路数、消息平均码流大小等综合因素计算
//        ctx.channel().config().setWriteBufferHighWaterMark(10*1024*1024);
//    }

    /**
     * 读取从服务端返回的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcResponse) {
                RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg;
                unprocessedRequests.complete(rpcResponse);
            }
        } finally {
            //msg为netty申请的请求ByteBuf，需业务主动释放，否则会造成内存泄漏
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     *  Netty 心跳机制，当发生空闲超时时，会触发此方法，给服务器发送一个心跳请求，保证客户端和服务端的连接不被断掉，避免重连。
     *  Netty通过IdleStateHandler实现的此种心跳机制不是一种双向心跳的PING-PONG模式，而是客户端发送心跳数据包，
     *  服务端接收心跳但不回复，因为一般应用rpc框架的服务端可能同时有上千个连接，心跳的回复需要消耗大量网络资源；
     *  如果服务端一段时间内内有收到客户端的心跳数据包则认为客户端已经下线，将通道关闭避免资源的浪费；
     *  在这种心跳模式下服务端可以感知客户端的存活情况，无论是宕机的正常下线还是网络问题的非正常下线，服务端都能感知到，
     *  而客户端不能感知到服务端的非正常下线。
     *  要想实现客户端感知服务端的存活情况，需要进行双向的心跳；Netty中的channelInactive()方法是通过Socket连接关闭时挥手数据包触发的，
     *  因此可以通过channelInactive()方法感知正常的下线情况，但是因为网络异常等非正常下线则无法感知。
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = channelProvider.get((InetSocketAddress) ctx.channel().remoteAddress());
                RpcRequest rpcRequest = RpcRequest.builder().rpcMessageType(RpcMessageType.HEART_BEAT).build();
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 处理客户端消息发生异常的时候被调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
