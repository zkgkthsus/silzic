package com.ssafy.deathnotelive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("Room")
public class RoomDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class validateName {
        @ApiModelProperty(name = "roomCode")
        private String roomCode;
        @ApiModelProperty(name = "nickName")
        private String nickName;
    }

}
