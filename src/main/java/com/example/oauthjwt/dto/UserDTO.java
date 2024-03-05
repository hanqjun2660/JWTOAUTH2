package com.example.oauthjwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String role;
    private String name;
    private String userName;
    private String email;
    private String profileImage;
}
