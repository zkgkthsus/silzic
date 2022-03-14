package com.ssafy.deathnotelive.controller;

import com.ssafy.deathnotelive.dto.NoticeDto;
import com.ssafy.deathnotelive.service.NoticeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("notice")
@Api(tags = {"Notice Controller"})
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;


    @PostMapping("regist")
    @ApiOperation(value = "공지사항 등록", notes = "공지사항을 등록한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<String> registNotice(
            @RequestBody @ApiParam(value = "공지사항 등록", required = true) NoticeDto.NoticeRegist noticeRegist
    ) {
        noticeService.registNotice(noticeRegist);
        return new ResponseEntity("정상적으로 등록되었습니다.", HttpStatus.OK);
    }


    @GetMapping()
    @ApiOperation(value = "공지사항 전체 조회", notes = "공지사항 전체 목록을 불러온다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<List<NoticeDto.Notices>> getAllNotices() {
        List<NoticeDto.Notices> list = noticeService.getAllNotices();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("{noticeNo}")
    @ApiOperation(value = "공지사항 상세조회", notes = "공지사항 번호로 공지사항 상세내용을 확인한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<NoticeDto.NoticeDetail> getNoticeDetail(
            @PathVariable @ApiParam(value = "정보를 조회할 공지사항 번호", required = true) Long noticeNo
    ) {
        NoticeDto.NoticeDetail noticeDetail = noticeService.findNotice(noticeNo);
        return new ResponseEntity(noticeDetail, HttpStatus.OK);
    }

    @PutMapping("modify")
    @ApiOperation(value = "공지사항 수정", notes = "공지사항을 수정한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<String> modifyNotice(
            @RequestBody @ApiParam(value = "공지사항 수정", required = true) NoticeDto.NoticeModify noticeModify
    ) {
        noticeService.modifyNotice(noticeModify);
        return new ResponseEntity("정상적으로 수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("{noticeNo}")
    @ApiOperation(value = "공지사항 삭제", notes = "공지사항을 삭제한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<List<NoticeDto.Notices>> deleteNotice(
            @PathVariable @ApiParam(value = "삭제할 공지사항 번호", required = true) Long noticeNo
    ) {
        noticeService.deleteNotice(noticeNo);
        return new ResponseEntity("정상적으로 삭제되었습니다.", HttpStatus.OK);
    }
}
