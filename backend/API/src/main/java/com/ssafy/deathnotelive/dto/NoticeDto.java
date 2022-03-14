package com.ssafy.deathnotelive.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel("Notice")
public class NoticeDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Notices {
        @ApiModelProperty(name = "공지사항 번호")
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
    public static class NoticeDetail {
        @ApiModelProperty(name = "공지사항 번호")
        private Long noticeNo;
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
    public static class NoticeModify{
        @ApiModelProperty(name = "공지사항 번호")
        private Long noticeNo;
        @ApiModelProperty(name = "글 제목")
        private String title;
        @ApiModelProperty(name = "글 내용")
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeRegist {
        @ApiModelProperty(name = "작성자")
        private String userId;
        @ApiModelProperty(name = "글 제목")
        private String title;
        @ApiModelProperty(name = "글 내용")
        private String content;
    }
}