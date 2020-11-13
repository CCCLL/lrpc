import com.cccll.HelloService;
import com.cccll.annotation.RpcScan;
import com.cccll.entity.RpcServiceProperties;
import com.cccll.remoting.transport.netty.server.NettyServer;
import com.cccll.serviceimpl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Server: 通过 @RpcService 注解自动注册服务
 *
 */
@RpcScan(basePackage = {"com.cccll"})
public class NettyServerMain {
    public static void main(String[] args) {
        // 通过注解注册服务
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyServer nettyServer = (NettyServer) annotationConfigApplicationContext.getBean("nettyServer");
        // 手动注册服务
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        nettyServer.registerService(helloService2, rpcServiceProperties);
        nettyServer.start();
    }
}
