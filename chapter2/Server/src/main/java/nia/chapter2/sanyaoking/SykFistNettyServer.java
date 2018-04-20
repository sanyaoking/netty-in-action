package nia.chapter2.sanyaoking;

import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
/**
 * 
 * @ClassName:  SykFistNettyServer   
 * @Description:服务器测试
 * @author: sanyaoking
 * @date:   2018年2月7日 上午10:11:39   
 *     
 * @Copyright: 2018 www.mengchao.top Inc. All rights reserved.
 */
public class SykFistNettyServer {
	private ChannelFuture cf;
	/**
	 * 
	 * @Title:  SykFistNettyServer   
	 * @Description:    创建Netty服务器并启动
	 * @param:  @param port  服务器端口
	 * @throws
	 */
	public SykFistNettyServer(int port) {
		//创建启动辅助对象
		ServerBootstrap bs = new ServerBootstrap();
		//创建事件循环
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        SocketAddress soc = new InetSocketAddress("127.0.0.1",8080);
        final SykBusHandler sbh = new SykBusHandler();
        try {
        	
			cf = bs.group(workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addFirst(sbh);
//					ch.writeAndFlush(msg)
				}
			}).bind(soc).sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//关闭EventLoopGroup，释放所有的资源
			try {
				workerGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @Title: end   
	 * @Description: 结束Netty的子线程，促使主线程继续执行，实现服务器的关闭
	 * @param:       
	 * @return: void      
	 * @throws
	 */
	public void end() {
		cf.channel().close().awaitUninterruptibly();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SykFistNettyServer sns = new SykFistNettyServer(8080);
	}

}
