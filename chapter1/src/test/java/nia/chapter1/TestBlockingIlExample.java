package nia.chapter1;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.Test;

public class TestBlockingIlExample {
	@Test
	public void createserve() {
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
	@Test
	public void test() {
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
//		try {
//			BlockingIoExample bie = new BlockingIoExample();
//			Thread t = new Thread( new MyTarget(bie));
//			t.start();
//			ConnectExample.connect();
//			 bie.closeService();
//			 t.stop();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	/*
	 * 
	 */
	@Test
	public void testConnectHandler() {
		NoBlockingIoExample nbs = new NoBlockingIoExample();
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
			nbs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
}	
