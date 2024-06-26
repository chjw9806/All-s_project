package bit.naver.controller;

import bit.naver.entity.StudyGroup;
import bit.naver.entity.StudyMembers;
import bit.naver.entity.Users;
import bit.naver.mapper.StudyGroupMapper;
import bit.naver.mapper.StudyRecruitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/studyRecruit")
public class StudyRecruitController {

    @Autowired
    private StudyRecruitMapper studyMapper;

    // 모집글 리스트
    @RequestMapping("/recruitList")
    public String getAllStudies(Model model) {
        List<StudyGroup> studies = studyMapper.getAllStudies();
        model.addAttribute("studies", studies);

        List<StudyGroup> study_18 = studyMapper.getAllStudy_9();
        model.addAttribute("study_18", study_18);


        return "studyRecruit/recruitList"; // recruitList.jsp로 이동
    }

    // 신청가입리스트?
    @GetMapping("/recruitReadForm")
    public String getStudyDetail(@RequestParam("studyIdx") Long studyIdx, Model model) {
        // 스터디 상세 정보 조회
        StudyGroup study = studyMapper.getStudyById(studyIdx);
        List<StudyMembers> members = studyMapper.getStudyMembersByStudyId(studyIdx);
        model.addAttribute("study", study);
        model.addAttribute("members", members);

        return "studyRecruit/recruitReadForm";
    }


    // 스터디 가입 신청서 제출
    @PostMapping("/apply")
    public String applyForStudy(@RequestParam("studyIdx") Long studyIdx,
                                @RequestParam("joinReason") String joinReason,
                                HttpSession session, Principal principal) {
        Users user = (Users) session.getAttribute("userVo");
        Long userIdx = user.getUserIdx(); // 사용자 ID 가져오기

        StudyMembers studyMember = new StudyMembers();
        studyMember.setStudyIdx(studyIdx);
        studyMember.setUserIdx(userIdx);
        studyMember.setRole("MEMBER");
        studyMember.setStatus("PENDING");
        studyMember.setJoinReason(joinReason.isEmpty() ? "신청내용이 없습니다" : joinReason);
        studyMember.setCreatedAt(LocalDateTime.now());
        studyMember.setUpdatedAt(LocalDateTime.now());

        studyMapper.insertStudyMember(studyMember);

        return "redirect:/studyRecruit/recruitList";
    }

    // 스터디 멤버 상태 업데이트
    @PostMapping("/updateMemberStatus")
    public String updateMemberStatus(@RequestParam("studyIdx") Long studyIdx, @RequestParam("userIdx") Long userIdx, @RequestParam("status") String status) {
        studyMapper.updateStudyMemberStatus(studyIdx, userIdx, status);
        return "redirect:/studyRecruit/recruitReadForm?studyIdx=" + studyIdx;
    }

    // 멤버 목록 가져오기
    @GetMapping("/studyGroupManagerMember")
    public String getStudyMembers(@RequestParam("studyIdx") Long studyIdx, Model model) {
        List<StudyMembers> members = studyMapper.getMembersByStudyIdx(studyIdx);
        for (StudyMembers member : members) {
            System.out.println(member);
        }
        model.addAttribute("members", members);
        return "studyGroup/studyGroupManagerMember";
    }

}
