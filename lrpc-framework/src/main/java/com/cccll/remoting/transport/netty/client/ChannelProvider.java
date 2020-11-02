package com.cccll.remoting.transport.netty.client;

import com.cccll.factory.SingletonFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于存取 Channel 实例
 *
 * @author cccll
 */
@Slf4j
public final class ChannelProvider {
    /**
     * netty中，channel的实现一定是线程安全的，基于此，我们可以存储一个channel的引用，
     * 并且在需要向远端发送数据时，通过这个引用来写数据，即便当时有很多线程在使用它也不会出现线程安全的问题，
     * 而且消息一定是按照顺序发送的。
     *
     * channel之所以在netty中是线程安全的是因为：
     *      1.一个EventLoop在其生命周期内只和唯一的一个Thread线程绑定（即一个io线程）
     *      2.所有由EventLoop处理的各种io事件都将在其所关联的io线程上执行，因为是单线程保证了线程安全
     *      3.一个Channel在其生命周期内只会注册在一个EventLoop（selector）上
     *      4.运行期间，一个EventLoop会被分配给一个或多个channel
     *
     *  netty在代码中具体的实现就是，在执行channelHandler时会判断当前线程是否是EventLoop中所绑定的那个唯一的io线程，
     *  如果是，则直接执行相应的channelHandler，处理该io事件。若不是，则将需要执行的channelHandler封装成一个任务交给EventLoop中io线程去执行，
     *  EventLoop中具体保存任务的是一个FIFO队列，而且又是单线程执行，所以在保证线程安全的同时也保证了任务的有序性。
     * （这里同时要注意的是，在channelHandler中要避免执行耗时或阻塞的处理逻辑，
     * 这样会导致后续channelHandler或其他绑定在该EventLoop上的channel的事件响应阻塞，从而影响性能。
     * 解决方式：耗时任务或阻塞的任务在自定义的channelHandler中可以交给业务线程池去处理，从而避免阻塞）
     */
    private final Map<String, Channel> channelMap;
    private final NettyClient nettyClient;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
        nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }


    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // 判断是否有对应地址的连接
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // 如果有的话，判断连接是否可用，可用的话就直接获取
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        // 否则，重新连接获取 Channel
        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channelMap.put(key, channel);
        return channel;
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map size :[{}]", channelMap.size());
    }
}

