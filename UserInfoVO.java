package com.tt.vo;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class UserInfoVO {
    private Long userId;
    private String username;
    private String realName;
    private String primaryDeptName;
    private Set<String> permissions;
    private List<String> roles;
}
