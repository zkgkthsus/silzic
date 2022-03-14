package com.ssafy.deathnotelive.controller;

import com.ssafy.deathnotelive.dto.BoardDto;
import com.ssafy.deathnotelive.service.BoardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("board")
@Api(tags = {"Board Controller"})
@Slf4j
public class BoardController {

    private final BoardService boardService;


    @PostMapping("regist")
    @ApiOperation(value = "건의사항 등록", notes = "건의사항을 등록한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<String> registBoard(
            @RequestBody @ApiParam(value = "건의사항 등록", required = true) BoardDto.BoardRegist boardRegist
    ) {
        boardService.registBoard(boardRegist);
        return new ResponseEntity("정상적으로 등록되었습니다.", HttpStatus.OK);
    }


    @GetMapping()
    @ApiOperation(value = "건의사항 전체 조회", notes = "건의사항 전체 목록을 불러온다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<List<BoardDto.Boards>> getAllBoards() {
        List<BoardDto.Boards> list = boardService.getAllBoards();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("{boardNo}")
    @ApiOperation(value = "건의사항 상세조회", notes = "건의사항 번호로 건의사항 상세내용을 확인한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<BoardDto.BoardDetail> getBoardDetail(
            @PathVariable @ApiParam(value = "정보를 조회할 건의사항 번호", required = true) Long boardNo
    ) {
        BoardDto.BoardDetail boardDetail = boardService.findBoard(boardNo);
        return new ResponseEntity(boardDetail, HttpStatus.OK);
    }

    @PutMapping("modify")
    @ApiOperation(value = "건의사항 수정", notes = "건의사항을 수정한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<String> modifyBoard(
            @RequestBody @ApiParam(value = "건의사항 수정", required = true) BoardDto.BoardModify boardModify
    ) {
        boardService.modifyBoard(boardModify);
        return new ResponseEntity("정상적으로 수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("{boardNo}")
    @ApiOperation(value = "건의사항 삭제", notes = "건의사항을 삭제한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<List<BoardDto.Boards>> deleteBoard(
            @PathVariable @ApiParam(value = "삭제할 건의사항 번호", required = true) Long boardNo
    ) {
        boardService.deleteBoard(boardNo);
        return new ResponseEntity("정상적으로 삭제되었습니다.", HttpStatus.OK);
    }
}
