package com.ssafy.deathnotelive.dto;

import com.ssafy.deathnotelive.model.BaseResponseBody;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("Login")
public class LoginDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String userId;
        private String password;
    }

//    나중에 httponly로 바꾸게 되면 사용할 DTO
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class LoginInfo {
//        private String userId;
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response extends BaseResponseBody {
        private String accessToken;

        public static Response of(Integer statusCode, String message, String accessToken) {
            Response res = new Response();
            res.setStatusCode(statusCode);
            res.setMessage(message);
            res.setAccessToken(accessToken);
            return res;
        }
    }
}
