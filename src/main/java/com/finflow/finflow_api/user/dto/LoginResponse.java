package com.finflow.finflow_api.user.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;
    private String tokenType;


}
