package filesystem;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class specifies the executor of operations. Note that each task thread are handling their
 * own message transmission and ack, no other operation required.
 */
public class FileSystemThreadPool {
    private ThreadPoolExecutor threadPoolExecutor;

    public FileSystemThreadPool(int corePoolSize, long keepAliveTime) {
        // the first two parameters are core pool size and max pool size. Since we used an linked queue
        // the pool size won't change, and we can submit "infinite" number of tasks to it.
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, corePoolSize,
                keepAliveTime, TimeUnit.SECONDS, taskQueue);
    }

    public void addTask(Runnable task) {
        threadPoolExecutor.execute(task);
    }
}

