package com.tt.vo;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private Set<String> permissions;
    private List<String> roles;
}
