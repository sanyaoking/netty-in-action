package nia.chapter2.sanyaoking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
/**
 * 
 * @ClassName:  SykBusHandler   
 * @Description:此类用于编写业务处理逻辑代码  
 * @author: sanyaoking
 * @date:   2018年2月7日 上午10:10:59   
 *     
 * @Copyright: 2018 www.mengchao.top Inc. All rights reserved.
 */
public class SykBusHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		 ByteBuf in = (ByteBuf) msg;
		// TODO Auto-generated method stub
		System.out.println("msg====="+in.toString(CharsetUtil.UTF_8));
		ctx.write("this is back msg!");
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
        .addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
}
