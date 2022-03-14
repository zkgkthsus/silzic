package com.ssafy.deathnotelive.service;

import com.ssafy.deathnotelive.dto.NoticeDto;
import com.ssafy.deathnotelive.entity.Notice;
import com.ssafy.deathnotelive.entity.User;
import com.ssafy.deathnotelive.error.exception.NoticeNotFoundException;
import com.ssafy.deathnotelive.repository.NoticeRepository;
import com.ssafy.deathnotelive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //공지사항 전체 검색
    public List<NoticeDto.Notices> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        List<NoticeDto.Notices> list = notices
                .stream()
                .map(notice -> modelMapper.map(notice, NoticeDto.Notices.class))
                .collect(Collectors.toList());
        return list;
    }

    //조회수 증가
    public void hitUp(Long noticeNo){
        Notice notice = noticeRepository.findById(noticeNo).orElseThrow(() -> new NoticeNotFoundException("EORROR"));
        int hit = notice.getHit();
        notice.setHit(hit+1);
        noticeRepository.save(notice);
    }

    //공지사항 상세조회
    public NoticeDto.NoticeDetail findNotice(Long noticeNo) {
        hitUp(noticeNo);
        Notice notice = noticeRepository.findById(noticeNo).orElseThrow(() -> new NoticeNotFoundException("EORROR"));
        return NoticeDto.NoticeDetail.builder()
                .noticeNo(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .hit(notice.getHit())
                .userId(notice.getUser().getUserId())
                .createdAt(notice.getCreatedAt())
                .modifiedAt(notice.getModifiedAt())
                .build();
    }

    //공지사항 삭제
    public void deleteNotice(Long noticeNo) {
        noticeRepository.findById(noticeNo).ifPresent(noticeRepository::delete);
    }

    //공지사항 수정
    public void modifyNotice(NoticeDto.NoticeModify noticeModify) {
        Long noticeNo = noticeModify.getNoticeNo();
        Notice notice = noticeRepository.findById(noticeNo).orElseThrow(() -> new NoticeNotFoundException("EORROR"));
        notice.setContent(noticeModify.getContent());
        notice.setTitle(noticeModify.getTitle());
        noticeRepository.save(notice);
    }

    public void registNotice(NoticeDto.NoticeRegist noticeRegist) {
        User user = userRepository.getByUserId(noticeRegist.getUserId());
        noticeRepository.save(Notice.builder()
                .title(noticeRegist.getTitle())
                .content(noticeRegist.getContent())
                .user(user)
                .hit(0)
                .build());
    }
}
