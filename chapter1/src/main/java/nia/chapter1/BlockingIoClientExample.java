package nia.chapter1;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
//磁珠使用的java阻塞io,在java 1.4时，就已经提供了非阻塞IO及nio包
public class BlockingIoClientExample {
	Socket socket;   
    BufferedReader in;   
    PrintWriter out;   
    public BlockingIoClientExample(){   
        try {  
            System.out.println("尝试连接服务器ip端口 127.0.0.1:8080");   
            socket = new Socket("127.0.0.1",8080);   
            System.out.println("客户端已经成功连接至服务器");   
            //请求信息拼接  
            String ipRequest = "{123456789012345678}{127.0.0.1}{1234}";  
            String requestLength = String.format("%04d", ipRequest.length());//获取长度，不足的补0，如长度56，则输出0056  
            ipRequest = requestLength+ipRequest;  
            System.out.println("发送给服务端的信息是:"+ipRequest);   
            InputStream in_withcode = new ByteArrayInputStream(ipRequest.getBytes());  
            BufferedReader line = new BufferedReader(new InputStreamReader(in_withcode));  
            //创建客户端输出流（流出客户端，流入服务端）
            out = new PrintWriter(socket.getOutputStream(),true);   
            out.println(line.readLine());  
            //获取服务端返回信息  （流入客户端，流出服务端）
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));   
            System.out.println(in.readLine());  
            out.close();   
            in.close();   
            socket.close();   
        } catch (UnknownHostException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
       
    public static void main(String[] args){   
    	
		try {
			BlockingIoExample bie = new BlockingIoExample();
			Thread t = new Thread( new MyTarget(bie));
			t.start();
			 new BlockingIoClientExample();
			 new BlockingIoClientExample();
			 bie.closeService();
			 t.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }   
}
class MyTarget implements Runnable {
	private BlockingIoExample bie ;
	public MyTarget(BlockingIoExample bie ) {
		this.bie=bie;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			bie.serve(8080);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}