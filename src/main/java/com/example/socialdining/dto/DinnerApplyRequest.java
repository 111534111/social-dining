package com.example.socialdining.dto;

import lombok.Data;
import java.util.List;

@Data
public class DinnerApplyRequest {
    private String ownerEmail;           // 擁有者 email
    private String title;                // 餐會名稱
    private List<String> availableDates; // 可舉辦日期

    // 地址改成兩段
    private String publicAddress;        // 只到區
    private String fullAddress;          // 完整地址
    private String phone;
    private String cuisine;              // 料理類型
    private Integer capacity;            // 人數上限
    private String description;          // 簡介
}