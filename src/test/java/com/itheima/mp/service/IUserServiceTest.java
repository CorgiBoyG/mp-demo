package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class IUserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    void testSaveUser() {
        User user = new User();
        // user.setId(5L);
        user.setUsername("LiLei");
        user.setPassword("123");
        user.setPhone("18688990013");
        user.setBalance(200);
        user.setInfo(UserInfo.of(24, "英文老师", "female"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userService.save(user);
    }

    @Test
    void testQuery() {
        List<User> users = userService.listByIds(List.of(1L, 2L, 4L));
        users.forEach(System.out::println);
    }

    private User buildUser(int i) {
        User user = new User();
        user.setUsername("user_" + i);
        user.setPassword("123");
        user.setPhone("" + (18688190000L + i));
        user.setBalance(2000);
        user.setInfo(UserInfo.of(24, "英文老师", "female"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(user.getCreateTime());
        return user;
    }

    @Test
    void testSaveOneByOne() {
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            userService.save(buildUser(i)); //每次都要请求，单独的sql
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时(ms)：" + (e - b));
    }

    @Test
    void testSaveBatch() {
        // 我们每次批量插入1000条件，插入100次即10万条数据

        // 1.准备一个容量为1000的集合
        List<User> list = new ArrayList<>(1000);
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            // 2.添加一个user
            list.add(buildUser(i));
            // 3.每1000条批量插入一次
            if (i % 1000 == 0) {
                userService.saveBatch(list); //预编译方案 一次请求
                // 4.清空集合，准备下一批数据
                list.clear();
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }

    @Test
    void testPageQuery() {
        int pageNo = 1, pageSize = 2;
        // 1.准备分页条件
        // 1.1.分页条件
        Page<User> page = Page.of(pageNo, pageSize);
        // 1.2.排序条件
        page.addOrder(new OrderItem("balance", true));
        page.addOrder(new OrderItem("id", true));

        // 2.分页查询
        Page<User> p = userService.page(page);

        // 3.解析
        // 总条数
        long total = p.getTotal();
        System.out.println("total = " + total);
        // 2.总页数
        long pages = p.getPages();
        System.out.println("pages = " + pages);
        // 分页数据
        List<User> users = p.getRecords();
        users.forEach(System.out::println);
    }
}