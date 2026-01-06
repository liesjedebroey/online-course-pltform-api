package com.courseplatform.dto;

import com.courseplatform.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String userName;
    private String email;
    private String password;
    private Role role; //The user can choose their role during registration

}
