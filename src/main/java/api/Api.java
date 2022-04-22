package api;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import config.ApiConfig;
import okhttp3.*;
import util.DateUtils;
import util.PushUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Api
 */
public class Api {

    // 全局上下文
    public static final Map<String, Object> context = new ConcurrentHashMap<>();

    // 【全球购】商品的模版店铺ID 不买全球购 所以指出跳过
    public static final String ALL_WORLD_SHOPPING = "1147161263885953814";
    
    /**
     * 根据当前地址获取店铺信息
     *
     * 需要抓包该请求 获取请求的经纬度，或者自己定位一个地址
     */
    public static void getRecommendStoreByLocation() {
        try {
            Map<String, Object> map = new HashMap<>();
            // 经纬度会决定你可以购买并配送的店，不能错
            map.put("longitude",""); // 抓包获取
            context.put("longitude", "");
            map.put("latitude",""); // 抓包获取
            context.put("latitude", "");
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse(" application/json");
            RequestBody body = RequestBody.create(mediaType, ApiConfig.getBody(map));
            Request request = new Request.Builder()
                    .url("https://api-sams.walmartmobile.cn/api/v1/sams/merchant/storeApi/getRecommendStoreListByLocation")
                    .method("POST", body)
                    .headers(ApiConfig.getHeaders())
                    .build();
            Response response = client.newCall(request).execute();
            String responseBodyStr = response.body().string();
            System.out.println(response);
            JSONObject object = JSONUtil.parseObj(responseBodyStr);
            JSONArray storeList = object.getJSONObject("data").getJSONArray("storeList");
            System.out.println(storeList);
            context.put("storeList",assembleStoreList(storeList));
        } catch (UnknownHostException e) {
            System.out.println("api || getRecommendStoreByLocation || 网络连接异常");
        } catch (IOException e) {
            System.out.println("api || getRecommendStoreByLocation || 网络IO异常");
        }
    }

    /**
     * 获取用户购物车数据
     */
    public static void getUserCart() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("uid",""); // 随便写，以header里的 auth-token 为准
            param.put("deliveryType","0");
            param.put("deviceType","ios");
            while (!Api.context.containsKey("end") && !context.containsKey("storeList")) {
                // 自旋
            }
            param.put("storeList",Api.context.get("storeList"));
            param.put("parentDeliveryType", 1);
            param.put("homePagelongitude", context.get("longitude")); // 首页经度 必须要有
            param.put("homePagelatitude", context.get("latitude")); //  首页纬度 必须要有 否则无法获取 data.deliveryAddress.addressId
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse(" application/json");
            RequestBody body = RequestBody.create(mediaType, ApiConfig.getBody(param));
            Request request = new Request.Builder()
                    .url("https://api-sams.walmartmobile.cn/api/v1/sams/trade/cart/getUserCart")
                    .method("POST", body)
                    .headers(ApiConfig.getHeaders())
                    .build();
            Response response = client.newCall(request).execute();
            String responseBodyStr = response.body().string();
            System.out.println("getUserCart: " + responseBodyStr);
            JSONObject data = JSONUtil.parseObj(responseBodyStr).getJSONObject("data");
            JSONArray floorInfoList = data.getJSONArray("floorInfoList");
            if (floorInfoList.isEmpty()) {
                System.out.println("api || getUserCart || 购物车为空");
                context.put("end", 0);
                return;
            }
            JSONObject floorInfoList_0 = floorInfoList.getJSONObject(0);
            JSONObject storeInfo = floorInfoList_0.getJSONObject("storeInfo");
            context.put("storeType", storeInfo.getInt("storeType"));
            context.put("storeId", storeInfo.getStr("storeId"));
            context.put("areaBlockId", storeInfo.getStr("areaBlockId"));
            context.put("storeDeliveryTemplateId", storeInfo.getStr("storeDeliveryTemplateId"));
            context.put("deliveryModeId", storeInfo.getStr("deliveryModeId"));

            JSONArray normalGoodsList = floorInfoList_0.getJSONArray("normalGoodsList");
            List<Map<String, Object>> goodsList = new ArrayList<>();
            for (int i = 0; i < normalGoodsList.size(); i++) {
                JSONObject goodObj = normalGoodsList.getJSONObject(i);
                Map<String, Object> good = new HashMap<>();
                good.put("isSelected", goodObj.getStr("isSelected")); // 是否选中
                good.put("quantity", goodObj.getInt("quantity")); // 数量
                good.put("spuId", goodObj.getStr("spuId")); // 商品编号
                good.put("storeId", goodObj.getStr("storeId")); // 店铺id
                goodsList.add(good);
            }
            context.put("goodsList", goodsList);
            context.put("addressId", data.getJSONObject("deliveryAddress").getStr("addressId"));
        } catch (UnknownHostException e) {
            System.out.println("api || getUserCart || 网络连接异常");
        } catch (IOException e) {
            System.out.println("api || getUserCart || 网络IO异常");
        }
    }

    /**
     * 获取运力信息（可配送时间）
     * @param millisTime 循环请求sleep时间，毫秒
     */
    public static void getCapacityData(int millisTime) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        // 拼装请求参数 param
        Map<String, Object> param = new HashMap<>();
        List<String> next7Days = DateUtils.getNext7Days();
        param.put("perDateList", next7Days);
        while (!Api.context.containsKey("end") && !context.containsKey("storeDeliveryTemplateId")) {
            // 自旋
        }
        if (ALL_WORLD_SHOPPING.equals(context.get("storeDeliveryTemplateId"))) {
            System.out.println("api || getCapacityData || 全球购 不买 直接返回");
            context.put("end", 0); // 结束标记：购物车只有全球购商品，停止抢购行动
            return;
        }
        param.put("storeDeliveryTemplateId", context.get("storeDeliveryTemplateId"));
        MediaType mediaType = MediaType.parse(" application/json");
        RequestBody body = RequestBody.create(mediaType, ApiConfig.getBody(param));
        Request request = new Request.Builder()
                .url("https://api-sams.walmartmobile.cn/api/v1/sams/delivery/portal/getCapacityData")
                .method("POST", body)
                .headers(ApiConfig.getHeaders())
                .build();

        int count = 0;
        try {
            while (count < 1000) {
                sleep(millisTime);
                Response response;
                response = client.newCall(request).execute();
                if (!response.isSuccessful()) { // 请求非200 可能因为繁忙 自旋
                    System.out.println("api || getCapacityData || code not in 200..299 ");
                    continue;
                }
                String responseBody = response.body().string();
                JSONObject rspJson = JSONUtil.parseObj(responseBody);
                JSONObject data = rspJson.getJSONObject("data");
                if (data == null) { // 限流报错 返回体无 data 自旋
                    System.out.println("api || getCapacityData || busy...");
                    continue;
                }
                JSONArray capcityResponseList = data.getJSONArray("capcityResponseList");
                if (null == capcityResponseList || capcityResponseList.isEmpty()) {
                    System.out.println("api || getCapacityData || 可配送时间列表为空");
                    context.put("end", 0);
                    return;
                }
                int n = capcityResponseList.size();
                JSONObject capcityDateLast = capcityResponseList.getJSONObject(n-1);
                if (count >= 2) {
                    continue;
                }
                // 如果只是想推送可以抢的信息，使用 dateISFull 判断即可
//            if (dateISFull != null && dateISFull.equals("false")) {
//                PushUtils.doPush("Sam's", "山姆现在可以抢！");
//                count++;
//                continue;
//            }
                // 获取可用配送时间需要继续执行
                JSONArray capcityDateLastTimeList = capcityDateLast.getJSONArray("list");
                String strDate = capcityDateLast.getStr("strDate");
                int times = capcityDateLastTimeList.size();
                JSONObject lastTime = capcityDateLastTimeList.getJSONObject(times - 1);
                String timeISFull = lastTime.getStr("timeISFull");
                if (timeISFull != null && timeISFull.equals("false")) {
                    String startRealTime = lastTime.getStr("startRealTime");
                    String endRealTime = lastTime.getStr("endRealTime");
                    System.out.println("api || getCapacityData || startRealTime: " + startRealTime + ", endRealTime: " + endRealTime);
                    context.put("expectArrivalTime", startRealTime);
                    context.put("expectArrivalEndTime", endRealTime);
                    // 异步发推送
                    PushUtils.doPush("Sam'S",
                            "山姆现在可以抢！可配送时间[" +
                                    DateUtils.dateTranfer(strDate) + " " +
                                    lastTime.getStr("startTime") + "-" +
                                    lastTime.getStr("endTime") + "]");
                    count++;
                    return;
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("api || getCapacityData || 网络连接异常");
        } catch (IOException e) {
            System.out.println("api || getCapacityData || 网络IO异常");
        }
    }

    /**
     * 下单支付
     */
    public static void commitPay()  {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse(" application/json");
        RequestBody body = RequestBody.create(mediaType, ApiConfig.buildCommitPayReqParam());
        Request request = new Request.Builder()
                .url("https://api-sams.walmartmobile.cn/api/v1/sams/trade/settlement/commitPay")
                .method("POST", body)
                .headers(ApiConfig.getHeaders())
                .build();
        // 自旋请求 创建订单提交支付 直到成功
        try {
            while (!Api.context.containsKey("end")) {
                Response response = client.newCall(request).execute();
                String responseBodyStr = response.body().string();
                String success = "";
                try {
                    JSONObject rsp = JSONUtil.parseObj(responseBodyStr);
                    success = rsp.getStr("success");
                    if (success.equals("true")) {
                        Api.context.put("end", 0); // 结束请求
                        String totalAmt = rsp.getJSONObject("data").getJSONObject("payInfo").getStr("TotalAmt");
                        PushUtils.doPush("山姆", "下单成功！还未支付");
                        System.out.println("api || commitPay: " + response.code() + " || " + responseBodyStr);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("api || commitPay: responseBody parse failed");
                    e.printStackTrace();
                }
                System.out.println("api || commitPay: " + response.code() + " || " + responseBodyStr);
            }
        } catch (UnknownHostException e) {
            System.out.println("api || getCapacityData || 网络连接异常");
        } catch (IOException e) {
            System.out.println("api || getCapacityData || 网络IO异常");
        }

    }

    public static void main(String[] args) {
        getRecommendStoreByLocation();
        getUserCart();
    }

    /**
     * 店铺列表转化
     * 从 getRecommendStoreByLocation 获取店铺信息
     * 转换成 getUserCart 的请求参数 storeList
     *
     * @param data 店铺信息 list
     * @return storeList
     */
    private static JSONArray assembleStoreList(JSONArray data) {
        int storeNum = data.size();
        JSONArray storeListVO = new JSONArray();
        for (int i = 0; i < storeNum; i++) {
            JSONObject store = data.getJSONObject(i);
            Map<String, Object> storeVO = new HashMap<>();
            storeVO.put("storeType", store.getStr("storeType"));
            storeVO.put("storeId", store.getStr("storeId"));
            storeVO.put("areaBlockId", store.getJSONObject("storeAreaBlockVerifyData").getStr("areaBlockId"));
            String storeDeliveryTemplateId = store.getJSONObject("storeRecmdDeliveryTemplateData").getStr("storeDeliveryTemplateId");
            storeVO.put("storeDeliveryTemplateId", storeDeliveryTemplateId);
            storeVO.put("deliveryModeId",
                    store.getJSONObject("storeDeliveryModeVerifyData").getStr("deliveryModeId"));
            storeListVO.put(new JSONObject(storeVO));
        }
        return storeListVO;
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }
}