package nia.chapter1;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;

public class NoBlockingClientExample {
	 // 通道管理器  
    private Selector selector;  
    
	public NoBlockingClientExample() {
		SocketChannel channel;
		try {
			channel = SocketChannel.open();
			 channel.configureBlocking(false); // 获得一个通道管理器  
		        this.selector = Selector.open(); // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen()方法中调  
		        // 用channel.finishConnect();才能完成连接  
		        channel.connect(new InetSocketAddress("127.0.0.1", 8080));  
		        // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。  
		        channel.register(selector, SelectionKey.OP_CONNECT);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 设置通道为非阻塞  
       
//        try {
//            SocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
//            SocketChannel client = SocketChannel.open(address);
//            // 注意client的通道是阻塞模式，修改为非阻塞模式，则通过
//            client.configureBlocking(false);
//            ByteBuffer buffer = ByteBuffer.allocate(74);
//            InputStream is = System.in;
//            byte[] byes = new byte[10];
//            is.read(byes, 0, 10);
//            for(int i=0;i<byes.length;i++) {
//            	buffer.put(byes[i]);
//            }
//            client.write(buffer);
//            // 要重用现有缓冲区，再次读取之前需要清空缓冲区；该方法不会改变缓冲区数据
//            // 只是简单重置索引
//            buffer.clear();
//            client.read(buffer);
////            System.out.println(buffer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	}
	/** 
     * * // 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理 * @throws // IOException 
     * @throws Exception  
     */  
    @SuppressWarnings("unchecked")  
    public void listen() throws Exception { // 轮询访问selector  
        while (true) {  
            // 选择一组可以进行I/O操作的事件，放在selector中,客户端的该方法不会阻塞，  
            // 这里和服务端的方法不一样，查看api注释可以知道，当至少一个通道被选中时，  
            // selector的wakeup方法被调用，方法返回，而对于客户端来说，通道一直是被选中的  
            selector.select(); // 获得selector中选中的项的迭代器  
            Iterator ite = this.selector.selectedKeys().iterator();  
            while (ite.hasNext()) {  
                SelectionKey key = (SelectionKey) ite.next(); // 删除已选的key,以防重复处理  
                ite.remove(); // 连接事件发生  
                if (key.isConnectable()) {  
                    SocketChannel channel = (SocketChannel) key.channel(); // 如果正在连接，则完成连接  
                    if (channel.isConnectionPending()) {  
                        channel.finishConnect();  
                    } // 设置成非阻塞  
                    channel.configureBlocking(false);  
                    // 在这里可以给服务端发送信息哦  
                    channel.write(ByteBuffer.wrap(new String("hello server!").getBytes()));  
                    // 在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。  
                    channel.register(this.selector, SelectionKey.OP_READ); // 获得了可读的事件  
                } else if (key.isReadable()) {  
                    read(key);  
                }  
            }  
        }  
    }  
  
    private void read(SelectionKey key) throws Exception {  
        SocketChannel channel = (SocketChannel) key.channel();  
        // 穿件读取的缓冲区  
        ByteBuffer buffer = ByteBuffer.allocate(10);  
        channel.read(buffer);  
        byte[] data = buffer.array();  
        String msg = new String(data).trim();  
        System.out.println("client receive msg from server:" + msg);  
        if(msg.contains("!")) {
        	key.cancel();
        	channel.close();
        }
//        channel.register(this.selector, SelectionKey.); // 获得了可读的事件  
        
    }  
	public static void  main(String arg[]) {
		final NoBlockingIoExample nbs = new NoBlockingIoExample();
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					nbs.serve(8080);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		th.start();
		NoBlockingClientExample nbce = new NoBlockingClientExample();
		try {
			nbce.listen();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
