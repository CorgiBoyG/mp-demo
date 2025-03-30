package com.itheima.mp.service;

import com.itheima.mp.domain.po.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class IAddressServiceTest {

    @Autowired
    private IAddressService addressService;

    @Test
    void testQuery() {
        List<Address> list = addressService.list();

        list.forEach(System.out::println);
    }

    @Test
    void testLogicDelete() {
        // 1. 删除 UPDATE address SET deleted=1 WHERE id=? AND deleted=0
        addressService.removeById(59);

        // 2.查询 SELECT * FROM address WHERE id=? AND deleted=0
        Address address = addressService.getById(59);
        System.out.println("address: " + address);
    }
}