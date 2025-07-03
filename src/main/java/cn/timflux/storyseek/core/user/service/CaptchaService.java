package cn.timflux.storyseek.core.user.service;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Random;

/**
 * ClassName: CaptchaService
 * Package: cn.timflux.storyseek.core.user.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午10:34
 * @Version 1.0
 */

@Service
public class CaptchaService {
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    // TODO: 待接入短信推送服务
    private static final String PUSH_URL = " ";

    public void sendCaptcha(String phone) {
        String code = generateRandomCode();
        cache.put(phone, code);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "【时流】");
        params.add("code", code);
        System.out.println("code:" + code);
        params.add("targets", phone);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(PUSH_URL, request, String.class);
            System.out.println("验证码已发送到 " + phone + "，响应：" + response.getBody());
        } catch (Exception e) {
            System.err.println("发送验证码失败: " + e.getMessage());
            // 也可以考虑清除缓存中的验证码
        }
    }

    public boolean verifyCaptcha(String phone, String code) {
        String cached = cache.get(phone);
        return cached != null && cached.equals(code);
    }

    public void removeCaptcha(String phone) {
        cache.remove(phone);
    }

    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6位数字验证码
        return String.valueOf(code);
    }
}

