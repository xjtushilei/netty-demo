package echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author scriptshi
 * 2018/5/30
 * <p>
 * 1.@Sharable 标识这类的实例之间可以在 channel 里面共享
 * <p>
 * 2.日志消息输出到控制台
 * <p>
 * 3.将所接收的消息返回给发送者。注意，这还没有冲刷数据
 * <p>
 * 4.冲刷所有待审消息到远程节点。关闭通道后，操作完成
 * <p>
 * 5.打印异常堆栈跟踪
 * <p>
 * 6.关闭通道
 * <p>
 * <p>
 * 在 main() 方法，引导了服务器
 * <p>
 * 创建 ServerBootstrap 实例来引导服务器并随后绑定
 * 创建并分配一个 NioEventLoopGroup 实例来处理事件的处理，如接受新的连接和读/写数据。
 * 指定本地 InetSocketAddress 给服务器绑定
 * 通过 EchoServerHandler 实例给每一个新的 Channel 初始化
 * 最后调用 ServerBootstrap.bind() 绑定服务器
 * <p>
 * 这样服务器初始化完成，可以被使用了。
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {

        int port = 5000;        //1
        new EchoServer(port).start();                //2
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(); //3
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)                                //4
                    .channel(NioServerSocketChannel.class)        //5
                    .localAddress(new InetSocketAddress(port))    //6
                    .childHandler(new ChannelInitializer<SocketChannel>() { //7
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new EchoServerHandler());
                        }
                    });

            ChannelFuture f = b.bind().sync();            //8
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();            //9
        } finally {
            group.shutdownGracefully().sync();            //10
        }
    }

}