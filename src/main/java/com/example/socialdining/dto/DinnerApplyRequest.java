package com.example.socialdining.dto;

import lombok.Data;
import java.util.List;

@Data
public class DinnerApplyRequest {

    private String ownerEmail;           // 擁有者 email
    private String title;                // 餐會名稱
    private List<String> availableDates; // ["2025-05-01", "2025-05-02"]
    private String cuisine;              // 料理類型
    private Integer capacity;            // 人數上限
    private String description;          // 簡介

    // 以下三個欄位用於地址自動完成
    private String country;              // 國家
    private String city;                 // 縣市
    private String district;             // 區
}
