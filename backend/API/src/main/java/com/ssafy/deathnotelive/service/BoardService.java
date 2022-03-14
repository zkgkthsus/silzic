package com.ssafy.deathnotelive.service;

import com.ssafy.deathnotelive.dto.BoardDto;
import com.ssafy.deathnotelive.entity.Board;
import com.ssafy.deathnotelive.entity.User;
import com.ssafy.deathnotelive.error.exception.BoardNotFoundException;
import com.ssafy.deathnotelive.repository.BoardRepository;
import com.ssafy.deathnotelive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //공지사항 전체 검색
    public List<BoardDto.Boards> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        List<BoardDto.Boards> list = boards
                .stream()
                .map(Board -> modelMapper.map(Board, BoardDto.Boards.class))
                .collect(Collectors.toList());
        return list;
    }

    //조회수 증가
    public void hitUp(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElseThrow(() -> new BoardNotFoundException("EORROR"));
        int hit = board.getHit();
        board.setHit(hit + 1);
        boardRepository.save(board);
    }

    //공지사항 상세조회
    public BoardDto.BoardDetail findBoard(Long boardNo) {
        hitUp(boardNo);
        Board board = boardRepository.findById(boardNo).orElseThrow(() -> new BoardNotFoundException("EORROR"));
        return BoardDto.BoardDetail.builder()
                .boardNo(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .userId(board.getUser().getUserId())
                .createdAt(board.getCreatedAt())
                .modifiedAt(board.getModifiedAt())
                .build();
    }

    //공지사항 삭제
    public void deleteBoard(Long boardNo) {
        boardRepository.findById(boardNo).ifPresent(boardRepository::delete);
    }

    //공지사항 수정
    public void modifyBoard(BoardDto.BoardModify boardModify) {
        Long boardNo = boardModify.getBoardNo();
        Board board = boardRepository.findById(boardNo).orElseThrow(() -> new BoardNotFoundException("EORROR"));
        board.setContent(boardModify.getContent());
        board.setTitle(boardModify.getTitle());
        boardRepository.save(board);
    }

    public void registBoard(BoardDto.BoardRegist boardRegist) {
        User user = userRepository.getByUserId(boardRegist.getUserId());
        boardRepository.save(Board.builder()
                .title(boardRegist.getTitle())
                .content(boardRegist.getContent())
                .user(user)
                .hit(0)
                .build());
    }
}

