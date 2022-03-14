package com.ssafy.deathnotelive.controller;

import com.ssafy.deathnotelive.config.jwt.JwtProperties;
import com.ssafy.deathnotelive.dto.LoginDto;
import com.ssafy.deathnotelive.dto.RoomDto;
import com.ssafy.deathnotelive.entity.Room;
import com.ssafy.deathnotelive.service.RoomService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("room")
@Api(tags = {"Room Controller"})
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /**
     * 게스트/로그인 유저가 룸에 참여하고 싶을 때
     * 방의 룸 코드(세션아이디)를 보내서 활성화 되어있는지 조회함.
     * 방이 활성화 중이라면 true 아니라면 false를 반납한다.
     */
    @GetMapping("join/{userId}")
    @ApiOperation(value = "방 참여", notes = "해당 유저의 방이 열려있는지 확인한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 403, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<String> joinRoom(
            @ApiParam(value = "방장 Id", required = true) @PathVariable String userId) {

        Room room = roomService.getRoomByUserId(userId);
        if (room.getIsOpen()) {
            return new ResponseEntity(room.getRoomCode(), HttpStatus.OK);
        }
        return new ResponseEntity("room is closed", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("create/{userId}")
    @ApiOperation(value = "방 룸생성", notes = "해당 유저가 호스트인 방을 생성후 룸코드를 반납해줌.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 403, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<String> createRoom(
            @ApiParam(value = "방장 Id", required = true) @PathVariable String userId) {

        String roomCode = roomService.createRoom(userId);

        return new ResponseEntity(roomCode, HttpStatus.OK);
    }


    @PostMapping("nickName")
    @ApiOperation(value = "중복된 닉네임을 사용중인지 체크", notes = "해당 방의 중복된 닉네임이 있는지 확인한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 403, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })

    public ResponseEntity<String> validateName(
            @ApiParam(value = "Room code, NickName", required = true) @RequestBody RoomDto.validateName validateName) {
        String roomCode = validateName.getRoomCode();
        String nickName = validateName.getNickName();
        System.out.println(roomCode);
        System.out.println(nickName);
        boolean nameIsValid = roomService.validateName(roomCode, nickName);
        if (nameIsValid) {
            return new ResponseEntity("Your nickName can use", HttpStatus.OK);
        }
        return new ResponseEntity("Your nickName is duplicated", HttpStatus.FORBIDDEN);
    }

    @PutMapping("finish/{roomCode}")
    @ApiOperation(value = "룸 종료", notes = "게임룸을 비활성화 시킨다.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 403, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    private ResponseEntity<String> finishRoom(
            @ApiParam(value = "종료시킬 룸 코드", required = true) @PathVariable String roomCode) {
        //룸 활성화 끄기 + 해당 룸의 참여자 닉네임 초기화
        roomService.finishRoom(roomCode);

        return new ResponseEntity("Success", HttpStatus.OK);
    }

    @DeleteMapping("delete/{sessionId}")
    @ApiOperation(value = "", notes = "해당 세션의 해당 닉네임을 삭제한다.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 400, message = "잘못된 요청"),
            @ApiResponse(code = 403, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    private ResponseEntity<String> deleteParticipant(
            @ApiParam(value = "종료시킬 룸 코드", required = true) @PathVariable String sessionId,
            @ApiParam(value = "종료시킬 룸 코드", required = true) @RequestParam String nickName) {

        //룸 활성화 끄기 + 해당 룸의 참여자 닉네임 초기화
        roomService.deleteParticipant(sessionId, nickName);

        return new ResponseEntity("Success", HttpStatus.OK);
    }
}



