package online.nonamekill.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    // 获取手机CPU内核储量
    private static final int NCPU = Math.max(2, Runtime.getRuntime().availableProcessors());

    // 通用线程
    private static ExecutorService threadPool = null;

    public static void init() {
        threadPool = Executors.newFixedThreadPool(3);
    }

    public static ExecutorService getThreadPool(){
        return threadPool;
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static Future<?> submit(Runnable runnable){
        return threadPool.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> task){
        return threadPool.submit(task);
    }

    /**
     * 获取最佳的线程数量，阻塞因子越大线程数量越多 阻塞因子需要配合场景和服务器来计算， android上我就不弄了
     *
     * @param blockingCoefficient 阻塞系数，阻塞因子介于0~1之间的数，阻塞因子越大，线程池中的线程数越多。
     * @return 线程数量
     */
    public static int getPoolSizeByBlockingCoefficient(float blockingCoefficient) {
        if (blockingCoefficient >= 1 || blockingCoefficient < 0) {
            throw new IllegalArgumentException("[blockingCoefficient] must between 0 and 1, or equals 0.");
        }
        // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
        return (int) (NCPU / (1 - blockingCoefficient));
    }


    /**
     * 创建固定数量的线程池
     *
     * @param blockingCoefficient
     * @return
     */
    public static ExecutorService newFixedThreadPoolByBlockingCoefficient(float blockingCoefficient) {
        int poolSizeCore = getPoolSizeByBlockingCoefficient(blockingCoefficient);
        return new ThreadPoolExecutor(poolSizeCore, poolSizeCore, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
