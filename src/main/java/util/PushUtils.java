package util;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.junit.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * push app message using Bark {@url https://github.com/Finb/Bark}
 *
 */
public class PushUtils {

    // bark 域名
    private final static String HOST = "https://api.day.app/";
    // 推送铃声
    private final static String defaultSound = "gotosleep";
    // 推送icon
    private final static String defaultIcon = "";
    // 推送点击跳转链接
    private final static String defaultUrl = "";
    // 推送用户token 替换成自己的
    private static String[] defaultTokens = {};
    // encode & decode 默认字符集
    private final static String CHARSET_UTF8 = "UTF-8";

    /**
     * 推送默认用户
     * @param title 推送标题
     * @param content 推送主体内容
     */
    public static void doPush(String title, String content) {
        doPush(title, content, defaultSound, defaultTokens);
    }

    /**
     * 推送指定用户
     * @param title 推送标题
     * @param content 推送主体内容
     * @param tokens 推送用户token列表
     */
    public static void doPush(String title, String content, String ...tokens) {
        doPush(title, content, defaultSound, tokens);
    }

    /**
     * 异步发推送
     *
     * @param title 推送标题
     * @param content 推送主体内容
     * @param sound 推送提醒铃声
     * @param tokens 推送用户token列表
     */
    public static void doPush(String title, String content, String sound, String ...tokens) {
        new Thread(() -> {
            try {
                for (String token : tokens) {
                    String url = HOST + token + "/" + title + "/" + content + "?sound" + sound;
                    System.out.println(url);
                    HttpRequest httpRequest = HttpUtil.createGet(url);
                    HttpResponse execute = httpRequest.execute();
                    System.out.println(execute.body());
                }
            } catch (Exception e) {
                System.out.println("bark 推送失败");
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 带完整参数的推送
     * @param param 参数
     * @param tokens 推送用户token列表
     */
    public static void doPush(Map<String, String> param, String ...tokens) {
        new Thread(() -> {
            String urlParam = buildPushUrl(param);
            for (String t : tokens) {
                String url = HOST + t + urlParam;
                HttpRequest httpRequest = HttpUtil.createGet(url);
                HttpResponse execute = httpRequest.execute();
            }
        }).start();
    }

    /**
     * 参数拼装
     * @param param 参数 map
     * @return 拼成的链接paramStr
     */
    private static String buildPushUrl(Map<String, String> param) {
        String title = param.get("title");
        Assert.assertNotNull(title);
        String content = param.get("content");
        Assert.assertNotNull(content);
        String icon = param.get("icon");
        String sound = param.get("sound");
        String url = param.get("url");
        String group = param.get("group");
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(title).append("/").append(content);
        try {
            sb.append("?sound=").append(sound)
                    .append("&icon=").append(URLEncoder.encode(icon, CHARSET_UTF8))
                    .append("&url=").append(URLEncoder.encode(url, CHARSET_UTF8))
                    .append("&group=").append(group);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Push param encode failure");
        }
        return sb.toString();
    }
}
