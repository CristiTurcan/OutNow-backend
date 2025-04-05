package com.example.outnowbackend.user.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {
    private Integer userid;
    private String email;
    private String username;
    private String userPhoto;
    private String bio;
    private String gender;
    private String dateOfBirth;
    private String location;
    private String interestList;
}