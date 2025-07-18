package cn.timflux.storyseek.core.user.service;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
/**
 * ClassName: CaptchaService
 * Package: cn.timflux.storyseek.core.user.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午10:34
 * @Version 1.0
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public void sendCaptcha(String identifier) {
        String code = generateRandomCode();
        cache.put(identifier, code);

        if (identifier.contains("@")) {
            sendEmailCaptcha(identifier, code);
        } else {
            // TODO: 短信验证码服务待接入
            log.info("模拟短信验证码发送：{} -> {}", identifier, code);
        }
    }

    public boolean verifyCaptcha(String identifier, String code) {
        return code != null && code.equals(cache.get(identifier));
    }

    public void removeCaptcha(String identifier) {
        cache.remove(identifier);
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private void sendEmailCaptcha(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("验证码");
            message.setText("欢迎访问TimeFlux AI，您的验证码是：" + code + "，5分钟内有效。请勿向他人泄露。");
            message.setFrom(String.valueOf(new InternetAddress(from, "TimeFlux", "UTF-8")));
            mailSender.send(message);
            log.info("验证码邮件已发送到 {}", to);
        } catch (UnsupportedEncodingException e) {
            log.error("发件编码失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("验证码邮件发送失败: {}", e.getMessage(), e);
        }
    }

}
