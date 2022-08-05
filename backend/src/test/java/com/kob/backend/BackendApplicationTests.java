package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //3次加密密码都不一样
        /*$2a$10$ZbBnkO6J40zgJ33xR1G1dOtHRQqGqeBtS3Vb/YvnwuMDgu3V6g87a
        $2a$10$lmuvXr7PmGr/XB2uVF9UROo9rOTlg9Gqai6GZT1r5PSGy3JaaZo4u
        $2a$10$39NBYV0sTTQwXxhr.VK6KOiucwRDJfqYju3BKGMAwzoJFNqtFA5q.*/
        System.out.println(passwordEncoder.encode("pj"));
        System.out.println(passwordEncoder.encode("pb"));
        System.out.println(passwordEncoder.encode("pc"));
        System.out.println(passwordEncoder.encode("pd"));
    }

}
