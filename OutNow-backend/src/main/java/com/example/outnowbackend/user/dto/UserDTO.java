package com.example.outnowbackend.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

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
    private Set<Integer> followedBusinessAccountIds;
    private Boolean showDob;
    private Boolean showLocation;
    private Boolean showGender;
    private Boolean showInterests;
    private Double latitude;
    private Double longitude;

    @JsonProperty("showDob")
    public Boolean getShowDob() {
        return showDob;
    }

    @JsonProperty("showLocation")
    public Boolean getShowLocation() {
        return showLocation;
    }

    @JsonProperty("showGender")
    public Boolean getShowGender() {
        return showGender;
    }

    @JsonProperty("showInterests")
    public Boolean getShowInterests() {
        return showInterests;
    }

}