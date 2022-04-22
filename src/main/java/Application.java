import api.Api;
import cn.hutool.core.util.RandomUtil;

public class Application {
    public static void main(String[] args) {
        int coreThreadSize = 2;
        int sleepMillisMin = 200;
        int sleepMillisMax = 500;
        boolean isCommitPay = true; // 是否下单

        for (int i = 0; i < coreThreadSize; i++) {
            new Thread(() -> {
                while (!Api.context.containsKey("end") && !Api.context.containsKey("storeList")) {
                    sleep(RandomUtil.randomInt(sleepMillisMin, sleepMillisMax));
                    Api.getRecommendStoreByLocation();
                }
            }).start();
        }

        for (int i = 0; i < coreThreadSize; i++) {
            new Thread(() -> {
                while (!Api.context.containsKey("end") && !Api.context.containsKey("storeId")) {
                    sleep(RandomUtil.randomInt(sleepMillisMin, sleepMillisMax));
                    Api.getUserCart();
                }
            }).start();
        }

        // 获取可配送时间
        for (int i = 0; i < coreThreadSize; i++) {
            new Thread(() -> {
                while (!Api.context.containsKey("end") && !Api.context.containsKey("expectArrivalTime")) {
                    sleep(RandomUtil.randomInt(sleepMillisMin, sleepMillisMax));
                    Api.getCapacityData(100);
                }
            }).start();
        }

        // 下单
        if (!isCommitPay) {
            return;
        }

        for (int i = 0; i < coreThreadSize; i++) {
            new Thread(() -> {
                while (!Api.context.containsKey("end") && !Api.context.containsKey("end")) {
                    sleep(RandomUtil.randomInt(sleepMillisMin, sleepMillisMax));
                    while (!Api.context.containsKey("end") && !Api.context.containsKey("expectArrivalTime")) {
                        // 可配送时间还没取到 自旋
                    }
                    Api.commitPay();
                }
            }).start();
        }
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }
}
