package com.ssafy.deathnotelive.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BoardDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Boards {
        @ApiModelProperty(name = "건의사항 번호")
        private Long id;
        @ApiModelProperty(name = "작성자") //필요한지 나중에 체크
        private String userId;
        @ApiModelProperty(name = "글 제목")
        private String title;
        @ApiModelProperty(name = "조회수")
        private Integer hit;
        @ApiModelProperty(name = "수정일")
        private LocalDateTime modifiedAt;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardDetail {
        @ApiModelProperty(name = "건의사항 번호")
        private Long boardNo;
        @ApiModelProperty(name = "작성자")
        private String userId;
        @ApiModelProperty(name = "글 제목")
        private String title;
        @ApiModelProperty(name = "글 내용")
        private String content;
        @ApiModelProperty(name = "조회수")
        private Integer hit;
        @ApiModelProperty(name = "작성일")
        private LocalDateTime createdAt;
        @ApiModelProperty(name = "수정일")
        private LocalDateTime modifiedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardModify {
        @ApiModelProperty(name = "건의사항 번호")
        private Long boardNo;
        @ApiModelProperty(name = "글 제목")
        private String title;
        @ApiModelProperty(name = "글 내용")
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoardRegist {
        @ApiModelProperty(name = "작성자")
        private String userId;
        @ApiModelProperty(name = "글 제목")
        private String title;
        @ApiModelProperty(name = "글 내용")
        private String content;
    }
}