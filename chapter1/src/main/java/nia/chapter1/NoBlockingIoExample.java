package nia.chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

public class NoBlockingIoExample {
	ServerSocketChannel serverChannel;

	public void close() {
		try {
			serverChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void serve(int port) throws IOException {
		
	        System.out.println("listening for connections on port: " + port);

	        // 可打印ASCII共95个，由于每打印一行需要左移一个字符，这里生成92*2个只是为了打印方便
	        byte[] rotation = new byte[95 * 2];
	        for (byte i = ' '; i <= '~'; i++) {
	            rotation[i - ' '] = i;
	            rotation[i + 95 - ' '] = i;
	        }

	        
	        // Selector只接受非阻塞通道
	        Selector selector;
	        try {
	            serverChannel = ServerSocketChannel.open();
	            // 要绑定端口，需要先用socket方法获取ServerSocket的对等端peer
	            // 然后使用bind绑定端口
	            ServerSocket ss = serverChannel.socket();
	            InetSocketAddress address = new InetSocketAddress(port);
	            ss.bind(address);
	            // 配置serverChannel为非阻塞模式，如果没有连接则accpet方法立即返回null， 如果不设置，accept方法将一直阻塞直到有连接进入
	            serverChannel.configureBlocking(false);
	            // 使用选择器迭代处理准备好的连接
	            selector = Selector.open();
	            // 向选择器注册对此通道的监视，对于chargen，只关心服务器Socket是否准备好接收一个新连接
	            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return;
	        }

	        while (true) {
	            try {
	                // 检查是否有可操作的通道准备好接受IO操作
	                selector.select();
	               
	            } catch (IOException e) {
	            	 System.out.println("========================");
	                e.printStackTrace();
	                break;
	            }

	            // 获取就绪通道的的SelectionKey的集合
	            Set<SelectionKey> readyKey = selector.selectedKeys();
	            // 迭代处理所有的key
	            Iterator<SelectionKey> iterator = readyKey.iterator();
	            while (iterator.hasNext()) {
	                SelectionKey key = iterator.next();
	                // 已经处理的key需要从集合中删除，标记该键已经处理
	                // 若下次该key还再次准备好，还将继续出现在Set中
	                iterator.remove();

	                try {
	                    // 测试该key是否已经准备好接收一个新的socket连接，即此时就绪的是服务器通道，接收一个新的Socket通道，将其添加到选择器
	                    if (key.isAcceptable()) {
	                    	 //有accept可以返回  
	                        //取得可以操作的channel  
	                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
	                        //调用accept完成三次握手，返回与客户端可以通信的channel 
	                        SocketChannel client = server.accept();
	                        System.out.println("accepted from " + client);
	                        // 配置client的通道为非阻塞模式
	                        client.configureBlocking(false);
	                        //注册进selector，当可读或可写时将得到通知，select返回  
	                        client.register(selector, SelectionKey.OP_READ);
	                    }else if(key.isReadable()) {
	                    	 //有channel可读,取出可读的channel  
	                        SocketChannel channel = (SocketChannel) key.channel(); 
	                        //创建读取缓冲区,一次读取1024字节  
	                        ByteBuffer buffer = ByteBuffer.allocate(1024);  
	                        channel.read(buffer);
	                        byte[] data = buffer.array();  
	                        String msg = new String(data).trim(); 
	                        System.out.println("OP_READ===="+msg);
	                        //锁住缓冲区，缓冲区使用的大小将固定  
	                        buffer.flip(); 
	                        //附加上buffer，供写出使用  
	                        key.attach(buffer);  
	                        key.interestOps(SelectionKey.OP_WRITE);  
	                    }else if (key.isWritable()) {// 测试该key是否准备好写操作，即此时就绪的是Socket通道，向缓冲区写入数据
	                    	//有channel可写,取出可写的channel  
	                        SocketChannel channel = (SocketChannel) key.channel(); 
	                        //取出可读时设置的缓冲区  
	                        ByteBuffer buffer = (ByteBuffer) key.attachment();  
	                        //将缓冲区指针移动到缓冲区开始位置  
	                        buffer.rewind();  
	                        //读取为String  
	                        String recv = new String(buffer.array());  
	                        //清空缓冲区  
	                        buffer.clear();  
	                        buffer.flip();  
	                        System.out.println("OP_WRITE===="+recv);
	                        //写回数据  
	                        byte[] sendBytes = recv.toUpperCase().getBytes();  
	                        channel.write(ByteBuffer.wrap(sendBytes));  
	                        key.interestOps(SelectionKey.OP_READ);  
	                    }
	                    // 没有就绪通道，选择器等待就绪通道
	                } catch (IOException e) {
	                    key.cancel();
	                    try {
	                        key.channel().close();
	                    } catch (IOException ex) {

	                    }
	                }
	            }
	        }
    }

	public static String getString(ByteBuffer buffer) {

		Charset charset = null;

		CharsetDecoder decoder = null;

		CharBuffer charBuffer = null;

		try {

			charset = Charset.forName("UTF-8");

			decoder = charset.newDecoder();

			// 用这个的话，只能输出来一次结果，第二次显示为空

			// charBuffer = decoder.decode(buffer);

			charBuffer = decoder.decode(buffer.asReadOnlyBuffer());

			return charBuffer.toString();

		} catch (Exception ex) {

			ex.printStackTrace();

			return "error";

		}

	}
}
