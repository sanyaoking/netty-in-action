package nia.chapter2.sanyaoking;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SykFirstNettyClient {

	public void start(int port) {
		EventLoopGroup elg = new NioEventLoopGroup();
		Bootstrap bs = new Bootstrap();
		SykFirstClientHandler sfch = new SykFirstClientHandler();
		try {
			ChannelFuture cf = bs.channel(NioSocketChannel.class).group(elg).handler(sfch).remoteAddress("127.0.0.1",port).connect().sync();
			cf.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				elg.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SykFirstNettyClient sfnc = new SykFirstNettyClient();
		sfnc.start(8080);
	}



}
