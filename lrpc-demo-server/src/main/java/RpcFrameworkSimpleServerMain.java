import com.cccll.HelloService;
import com.cccll.entity.RpcServiceProperties;
import com.cccll.remoting.transport.socket.SocketRpcServer;
import com.cccll.serviceimpl.HelloServiceImpl;

public class RpcFrameworkSimpleServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        socketRpcServer.registerService(helloService, rpcServiceProperties);
        socketRpcServer.start();
    }
}
