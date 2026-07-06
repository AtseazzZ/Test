package com.atsea.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {

    private Integer id;
    private String username;
    private String password;
    private String name;
    private Integer age;

    @Override  // 注解表示重写父类（Object）的方法，必须加
    public String toString() {
        // 自定义输出格式，把所有字段的值拼进去
        return "User{" +
                "id=" + id +                    // 拼接id字段
                ", username='" + username + '\'' +  // 拼接username（字符串加单引号更易读）
                ", password='" + password + '\'' +  // 拼接password
                ", name='" + name + '\'' +          // 拼接name
                ", age=" + age +                  // 拼接age
                '}';                              // 结尾闭合
    }

    public User(Integer id, String username, String password, String name, Integer age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
    }
}
