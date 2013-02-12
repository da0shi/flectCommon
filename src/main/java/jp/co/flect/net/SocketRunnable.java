package jp.co.flect.net;

import java.net.Socket;

public interface SocketRunnable extends Runnable {
	
	public Socket getSocket();
	
}
