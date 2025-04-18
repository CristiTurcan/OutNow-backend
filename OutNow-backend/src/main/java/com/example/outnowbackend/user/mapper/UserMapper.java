package com.example.outnowbackend.user.mapper;

import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity
     * @return a UserDTO with the mapped fields, or null if the input is null
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setUserid(user.getUserid());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setUserPhoto(user.getUserPhoto());
        dto.setBio(user.getBio());
        dto.setGender(user.getGender());
        dto.setLocation(user.getLocation());
        dto.setInterestList(user.getInterestList());
        if (user.getDateOfBirth() != null) {
            dto.setDateOfBirth(user.getDateOfBirth().format(dateFormatter));
        }
        // NEW: map followed business-account IDs
        if (user.getFollowedAccounts() != null) {
            dto.setFollowedBusinessAccountIds(
                    user.getFollowedAccounts()
                            .stream()
                            .map(ba -> ba.getId())
                            .collect(Collectors.toSet())
            );
        }
        return dto;
    }

    /**
     * Converts a UserDTO to a User entity.
     * Note: This method only maps the basic fields and does not handle collections.
     *
     * @param dto the UserDTO
     * @return a User entity with the mapped fields, or null if the input is null
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUserid(dto.getUserid());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setUserPhoto(dto.getUserPhoto());
        user.setBio(dto.getBio());
        user.setGender(dto.getGender());
        user.setLocation(dto.getLocation());
        user.setInterestList(dto.getInterestList());

        if (dto.getDateOfBirth() != null && !dto.getDateOfBirth().isEmpty()) {
            user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth(), dateFormatter));
        }
        return user;
    }
}
