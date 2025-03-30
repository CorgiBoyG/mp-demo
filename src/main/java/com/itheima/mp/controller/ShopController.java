package com.itheima.mp.controller;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.mp.domain.dto.ShopDTO;
import com.itheima.mp.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-12-26
 */
@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final IShopService shopService;

    @GetMapping
    public List<ShopDTO> findAll(){
        return shopService.list()
                .stream()
                .map(ShopDTO::new)
                .peek(this::downloadImage)
                .collect(Collectors.toList());
    }

    private void downloadImage(ShopDTO shopDTO) {
        for (String image : shopDTO.getImages()) {
            try {
                // 获取图片地址
                URLConnection connection = new URL(image).openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                String name = RandomUtil.randomString(20);

                String rawName = StrUtil.subAfter(image, '/', true);
                System.out.println("rawName = " + rawName + "， newName = " + name);

                Files.copy(inputStream, new File("C:\\Users\\amy\\Desktop\\" + name + ".jpg").toPath());

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
