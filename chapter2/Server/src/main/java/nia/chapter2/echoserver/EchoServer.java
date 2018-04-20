package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;
    private static ChannelFuture f ;
    public static ChannelFuture getF() {
		return f;
	}
	public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    
    
    public void start() throws Exception {
    	//创建一个实现业务逻辑的ChannelHandler抽象实现类
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建事件组用于注册绑定channel，通知事件
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            //注册group
            b.group(group)
            	//选择所使用的的socketchannel
                .channel(NioServerSocketChannel.class)
                //绑定地址端口
                .localAddress(new InetSocketAddress(port))
                //绑定处理逻辑
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                    	//通过Channel的pipeline绑定处理逻辑的Hanndler
                        ch.pipeline().addLast(serverHandler);
                    }
                });
            //绑定服务器，并等待绑定完成，(sync的作用是产生阻塞知道绑定完成)
            f = b.bind().sync();
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            //获取channel的CloseFuture，并阻塞当前线程等在子线程退出，以保证服务器正常执行
            f.channel().closeFuture().sync();
        } finally {
        	//关闭EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
    
    public void end() {
    	//调用下面的方法可以关闭服务器，并且下面的语句执行后，才可以而技术start主线程的阻塞
    	EchoServer.getF().channel().close().awaitUninterruptibly();
    }
}
