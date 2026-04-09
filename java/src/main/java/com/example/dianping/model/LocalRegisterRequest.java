package com.example.dianping.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocalRegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 32, message = "用户名长度需要在 3 到 32 位之间")
        String username,
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 64, message = "密码长度需要在 6 到 64 位之间")
        String password,
        @Size(max = 50, message = "显示名称不能超过 50 个字符")
        String displayName,
        @Email(message = "邮箱格式不正确")
        @Size(max = 150, message = "邮箱长度不能超过 150 个字符")
        String email
) {
}
