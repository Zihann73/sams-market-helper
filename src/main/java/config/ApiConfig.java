package config;

import api.Api;
import cn.hutool.json.JSONObject;
import okhttp3.Headers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求header和body的拼装配置类，header参数建议抓包获取后写死
 */
public class ApiConfig {

    /**
     * 拼装统一 header
     *
     * 随便抓个包获取一下
     *
     * @return
     */
    public static Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        return builder.add("Host", " api-sams.walmartmobile.cn")
                .add("Content-Type", " application/json")
                .add("Accept", " */*")
                .add("auth-token", "") // todo
                .add("app-version", "") // todo
                .add("longitude", "") // todo
                .add("device-id", "") // todo
                .add("latitude", "")// todo
                .add("device-type", "") // todo
                .add("Accept-Language", " en-CN;q=1, zh-Hans-CN;q=0.9, zh-Hant-CN;q=0.8, de-CN;q=0.7, el-CN;q=0.6, hi-Latn-CN;q=0.5")
                // .add("Accept-Encoding", " gzip, deflate, br") // 显示会乱码
                .add("apptype", "") // todo
                .add("device-name", "") // todo
                .add("device-os-version", "") // todo
                .add("User-Agent", "") // todo
                .add("Content-Length", " 155")
                .add("system-language", " CN")
                .add("Connection", " keep-alive")
                .build();
    }

    /**
     * request body 序列化
     * @param param map
     * @return str
     */
    public static String getBody(Map<String, Object> param) {
        JSONObject object = new JSONObject();
        object.putAll(param);
        return object.toString();
    }

    /**
     * commitPay 提交订单支付接口 请求参数拼装
     * @return 序列化后的请求参数
     */
    public static String buildCommitPayReqParam() {
        Map<String, Object> param = new HashMap<>();
        List<Map<String, Object>> goodsList = new ArrayList<>();
        param.put("goodsList",Api.context.get("goodsList"));
        //param.put("goodsList","");

        //param.put("invoiceInfo", new Object());
        param.put("cartDeliveryType", 2);
        param.put("floorId", 1);
        param.put("amount", "0"); //总价格无所谓 随便写
        param.put("purchaserName", "");
        param.put("tradeType", "APP");
        param.put("purchaserId", "");
        param.put("payType", 0);
        param.put("currency", "CNY");
        // 支付方式 随便写 一个string就行
        param.put("channel", "alipay");  // wechat/alipay/china_unionpay/sam_coupon
        param.put("shortageId", 1);
        param.put("isSelfPickup", 0);
        param.put("orderType", 0);
        param.put("remark", "");
        param.put("uid", ""); // 随便写
        param.put("addressId", Api.context.get("addressId")); // 收货地址必须是对的，否则请求失败
        // 配送时间
        Map<String, Object> settleDeliveryInfo = new HashMap<>();
        settleDeliveryInfo.put("expectArrivalTime", Api.context.get("expectArrivalTime"));
        settleDeliveryInfo.put("expectArrivalEndTime", Api.context.get("expectArrivalEndTime"));
        settleDeliveryInfo.put("deliveryType", 0);
        param.put("settleDeliveryInfo", settleDeliveryInfo);
        // 配送方式
        Map<String, Object> deliveryInfoVO = new HashMap<>();
        deliveryInfoVO.put("storeDeliveryTemplateId", Api.context.get("storeDeliveryTemplateId"));
        deliveryInfoVO.put("deliveryModeId", Api.context.get("deliveryModeId"));
        deliveryInfoVO.put("storeType", Api.context.get("storeType"));
        param.put("deliveryInfoVO", deliveryInfoVO);
        // 配送店铺
        Map<String, Object> storeInfo = new HashMap<>();
        storeInfo.put("storeId", Api.context.get("storeId"));
        storeInfo.put("areaBlockId", Api.context.get("areaBlockId"));
        storeInfo.put("storeType", Api.context.get("storeType"));
        param.put("storeInfo", storeInfo);
        param.put("shortageDesc", "其他商品继续配送（缺货商品直接退款）");
        param.put("payMethodId", "2019030763460479");
        // System.out.println("api || commitPay || " + param);
        return getBody(param);
    }
}
