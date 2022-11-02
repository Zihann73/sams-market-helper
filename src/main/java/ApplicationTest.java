import api.Api;

/**
 * ApplicationTest.
 */
public class ApplicationTest {
    public static void main(String[] args) {
        if (Api.context.containsKey("end")) {
            return;
        }
        Api.getRecommendStoreByLocation();
        Api.getUserCart();
        // 获取可配送时间
        Api.getCapacityData(1000);
        // 下单
        Api.commitPay();
    }

}
