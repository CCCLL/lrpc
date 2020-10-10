import com.cccll.HelloService;
import com.cccll.entity.RpcServiceProperties;
import com.cccll.remoting.transport.netty.server.NettyServer;
import com.cccll.serviceimpl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Server: 通过 @RpcService 注解自动注册服务
 *
 */
@RpcScan(basePackage = {"com.cccll.serviceimpl"})
public class NettyServerMain {
    public static void main(String[] args) {
        // 通过注解注册服务
        new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = new NettyServer();
        // 手动注册服务
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        nettyServer.registerService(helloService2, rpcServiceProperties);
        nettyServer.start();
    }
}
