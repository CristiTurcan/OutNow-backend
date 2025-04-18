package com.example.outnowbackend.businessaccount.dto;

import lombok.Data;

@Data
public class BusinessAccountDTO {
    private Integer id;
    private String email;
    private String username;
    private String userPhoto;
    private String bio;
    private String location;
    private String interestList;
    private Long followerCount;

}
