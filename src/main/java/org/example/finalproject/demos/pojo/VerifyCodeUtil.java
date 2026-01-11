package org.example.finalproject.demos.pojo;

import javax.servlet.http.HttpSession;
import java.util.Random;

//验证码验证工具
public class VerifyCodeUtil {
    // 生成6位数字验证码
    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 存储验证码到Session（5分钟过期）
    public static void storeCode(HttpSession session, String email, String code) {
        session.setAttribute("VERIFY_CODE:" + email, code);
        session.setMaxInactiveInterval(60 * 5);
    }

    // 验证验证码
    public static boolean validateCode(HttpSession session, String email, String inputCode) {
        String storedCode = (String) session.getAttribute("VERIFY_CODE:" + email);
        return storedCode != null && storedCode.equals(inputCode);
    }
}
