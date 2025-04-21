package cn.fuguang.order.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    public static ThreadPoolExecutor commonThreadPool = new ThreadPoolExecutor(
            5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.AbortPolicy());


    public static ThreadPoolExecutor jifenThreadPool = new ThreadPoolExecutor(
            5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.AbortPolicy());

}
