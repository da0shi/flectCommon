package jp.co.flect.net;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jp.co.flect.log.Logger;
import jp.co.flect.log.LoggerFactory;

public abstract class SocketWorker {
	
	private static final Logger log = LoggerFactory.getLogger(SocketWorker.class);
	
	private ThreadPoolExecutor executor;
	private AccepterThread accepter;
	private boolean systemExit = false;
	
	public SocketWorker(ServerSocket socket, int corePoolSize, int maxPoolSize, int keepAliveTime, int queueSize) {
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(queueSize);
		RejectedExecutionHandler handler = createRejectedExecutionHandler();
		if (handler == null) {
			handler = new DefaultRejectedExecutionHandler();
		}
		this.executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue, handler);
		this.executor.allowCoreThreadTimeOut(true);
		
		this.accepter = new AccepterThread(socket);
	}
	
	protected abstract RejectedExecutionHandler createRejectedExecutionHandler();
	protected abstract SocketRunnable createRunnable(Socket socket);
	
	public void setExitOnShutdown(boolean b) { this.systemExit = b;}
	public boolean isExitOnShutdown() { return this.systemExit;}
	
	public void start() {
		this.accepter.start();
	}
	
	public void shutdown() {
		this.accepter.exit();
		try {
			this.accepter.getSocket().close();
		} catch (IOException e) {
			//ignore
		}
		this.executor.shutdown();
		if (this.systemExit) {
			System.exit(0);
		}
	}
	
	public void shutdownLater(final int waitTime) {
		this.accepter.exit();
		try {
			this.accepter.getSocket().close();
		} catch (IOException e) {
			//ignore
		}
		new Thread() {
			public void run() {
				int cnt = waitTime;
				do {
					if (executor.getActiveCount() == 0 && executor.getQueue().size() == 0) {
						executor.shutdown();
						break;
					}
					if (cnt > 0) {
						cnt--;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				} while (cnt > 0);
				if (!executor.isShutdown() && !executor.isTerminating() && !executor.isTerminated()) {
					executor.shutdown();
				}
				if (systemExit) {
					System.exit(0);
				}
			}
		}.start();
	}
	
	private static class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
		
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			log.warn("SocketWorker - rejected");
			SocketRunnable sr = (SocketRunnable)r;
			try {
				sr.getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class AccepterThread extends Thread {
		
		private ServerSocket socket;
		private volatile boolean running;
		
		public AccepterThread(ServerSocket socket) {
			this.socket = socket;
		}
		
		public ServerSocket getSocket() { return this.socket;}
		
		public void run() {
			this.running = true;
			while (running) {
				try {
					Socket s = this.socket.accept();
					if (s != null) {
						executor.execute(createRunnable(s));
					}
				} catch (IOException e) {
					if (this.running) {
						log.error(e);
					}
				}
			}
		}
		
		public void exit() {
			this.running = false;
		}
	}
}
