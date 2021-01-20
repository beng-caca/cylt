package com.cylt.security;

import com.cylt.common.DESUtil;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义的密码加密方法，实现了PasswordEncoder接口
 *
 */
@Component
public class SysPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence) {
        //加密密码
        return DESUtil.encrypt(charSequence.toString(),DESUtil.KEY);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return encode(charSequence).equals(s);
    }
}
