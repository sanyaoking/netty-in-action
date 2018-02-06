package nia.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

/**
 * Created by kerr.
 *
 * Listing 1.1 Blocking I/O example
 */
public class BlockingIoExample {
	private ServerSocket serverSocket;
	
    /**
     * Listing 1.1 Blocking I/O example
     * */
    public void serve(int portNumber) throws IOException {
    	//启动服务并绑定本级端口
        serverSocket = new ServerSocket(portNumber);
        while(true) {
        	//为了提高效率，每次为一个连接开一个线程，但是依然会有瓶颈当超过10000个连接是会产生问题
        	startDeal(serverSocket);
        	System.out.println("再次等待连接");
        }
    }
    
    public void startDeal( ServerSocket serverSocket) throws IOException {
        //执行下面的这一条语句后，阻塞式的网络编程将会一直阻塞下去，知道接受到一个网络连接之后才会继续执行下面的语句
    	 Socket clientSocket = serverSocket.accept();
    	 MyTash my = new MyTash(clientSocket,()->{return  "Processed";});
    	 Thread myth = new Thread(my);
    	 myth.start();
    }
    
    public void closeService() {
    	try {
    		//关闭socket服务
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private String processRequest(String request){
        return "Processed";
    }
}
class MyTash implements Runnable{
	private Socket clientSocket;
	private final Supplier<String> sup;
	public MyTash(Socket clientSocket,Supplier sup) {
		this.clientSocket=clientSocket;
		this.sup = sup;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader in;
		try {
			//accept接受客户端链接后，开始读取数据，获取输入流(流向服务端)
			in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			//获取输出流(流向客户端)
			  PrintWriter out =
		                new PrintWriter(clientSocket.getOutputStream(), true);
		        String request, response;
		        while ((request = in.readLine()) != null) {
		            if ("Done".equals(request)) {
		                break;
		            }
		            response = sup.get();
		            out.println(response);
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
	}
	
}
