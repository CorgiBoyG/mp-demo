package com.itheima.mp.domain.dto;

import com.itheima.mp.domain.po.Shop;
import lombok.Data;

import java.util.List;

@Data
public class ShopDTO {
    private Long id;

    /**
     * 商铺名称
     */
    private String name;

    /**
     * 商铺图片，多个图片以','隔开
     */
    private List<String> images;

    /**
     * 商圈，例如陆家嘴
     */
    private String area;

    /**
     * 地址
     */
    private String address;

    /**
     * 均价，取整数
     */
    private Long avgPrice;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    private String openHours;

    public ShopDTO() {
    }

    public ShopDTO(Shop s) {
        this.id = s.getId();
        this.name = s.getName();
        this.images = List.of(s.getImages().split(","));
        this.area = s.getArea();
        this.address = s.getAddress();
        this.avgPrice = s.getAvgPrice();
        this.comments = s.getComments();
        this.score = s.getScore();
        this.openHours = s.getOpenHours();
    }
}
