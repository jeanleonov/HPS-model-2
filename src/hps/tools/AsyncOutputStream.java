package hps.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncOutputStream {
	
	private final static WriterThread writerThreadAction;
	private final static Thread writerThread;
	private static boolean hasToFinish = false;
	static {
		writerThreadAction = new WriterThread();
		writerThread = new Thread(writerThreadAction);
		writerThread.start();
	}
	private static class GlobalQueueItem {
		OutputStream out;
		boolean noMore = false;
		BlockingQueue<byte[]> buffers = new LinkedBlockingQueue<>();
		public GlobalQueueItem(OutputStream out) {
			this.out = out;
		}
	}
	private static final BlockingQueue<GlobalQueueItem> outputsQueue = new LinkedBlockingQueue<>();
	private static final List<GlobalQueueItem> outputsToClose = new LinkedList<>();
	
	private final GlobalQueueItem item;
	
	public AsyncOutputStream(OutputStream out) {
		item = new GlobalQueueItem(out);
		outputsQueue.add(item);
	}
	
	public void write(byte[] bytesToWrite) throws IOException {
		item.buffers.add(bytesToWrite);
	}
	
	public void close() throws InterruptedException {
		item.noMore = true;
	}
	
	private static class WriterThread implements Runnable {
		@Override
		public void run() {
			while (!hasToFinish || outputsQueue.size()!=0) {
				if (outputsQueue.size()==0)
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						Logger.error("Error while writting in parallel");
						Logger.error(e1);
						e1.printStackTrace();
					}
				else {
					GlobalQueueItem item = outputsQueue.remove();
					while (!item.noMore || item.buffers.size() != 0) {
						if (item.buffers.size() == 0)
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								Logger.error("Error while writting in parallel");
								Logger.error(e1);
								e1.printStackTrace();
							}
						else
							try {
								item.out.write(item.buffers.remove());
							} catch (IOException e) {
								Logger.error("Error while writting in parallel");
								Logger.error(e);
								e.printStackTrace();
							}
					}
					outputsToClose.add(item);
				}
			}
		}
	}
	
	public static void finish() throws InterruptedException, IOException {
		hasToFinish = true;
		while (writerThread.isAlive())
			Thread.sleep(100);
		for (GlobalQueueItem item : outputsToClose) {
			item.out.flush();
			item.out.close();
		}
	}

}
