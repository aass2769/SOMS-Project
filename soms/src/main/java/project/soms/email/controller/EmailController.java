package project.soms.email.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import project.soms.email.dto.EmailDto;
import project.soms.email.service.EmailService;
import project.soms.employee.dto.EmployeeDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("email")
public class EmailController {

  /*
  이메일 서비스 인터페이스 생성
   */
  private final EmailService emailService;

  /*
  이메일 작성 화면 응답
   */
  @GetMapping("sendForm")
  public String readmail(Model model) {
    model.addAttribute("emailDto", new EmailDto());
    return "email/emailForm/sendMailRe";
  }

  /*
  답장 보낼 수신자 값을 담아서 화면 응답
   */
  @GetMapping("reply")
  public String reply(Long emailNo, Model model) {
    //화면에 응답할 emailDto 객체 생성
    EmailDto emailDto = new EmailDto();
    //파라미터로 전달받은 이메일번호로 이메일을 조회 후 이메일의 발신자 값을 수신자 값으로 객체에 값 할당
    List<String> emailList = new ArrayList<>();
    emailList.add(emailService.emailDetail(emailNo).getEmailFrom());
    emailDto.setEmailRecipient(emailList);

    //모델에 저장
    model.addAttribute("emailDto", emailDto);

    //이메일 작성 화면 응답
    return "email/emailForm/sendMailRe";
  }

  /*
  전달 보낼 이메일의 내용 전체 담아서 화면 응답
   */
  @GetMapping("forward")
  public String forward(Long emailNo, Model model) {
    //파라미터로 전달받은 이메일번호로 이메일을 조회
    EmailDto emailDetail = emailService.emailDetail(emailNo);

    //내용에 전달에 대한 정보 추가
    String addForwardValue =
        "<small><p>---------- Forwarded message ---------<br>" +
            "보낸사람 : " + emailDetail.getEmailFrom() + "<br>" +
            "Date : " + emailDetail.getEmailSentDate() + "<br>" +
            "Subject : " + emailDetail.getEmailSubject() + "<br>" +
            "To : " + emailDetail.getEmailFrom() + "</p></small><br><br><br>" +
            emailDetail.getEmailContent();

    //수신자 배열 초기화, 내용 추가
    emailDetail.setEmailRecipient(new ArrayList<>());
    emailDetail.setEmailContent(addForwardValue);

    model.addAttribute("emailDto", emailDetail);
    return "email/emailForm/sendMailRe";
  }

  /*
  임시보관한 메일 다시 쓰기
   */
  @GetMapping("retry")
  public String retry(Long emailNo, Model model) {
    //파라미터로 전달받은 이메일번호로 이메일을 조회
    EmailDto emailDetail = emailService.emailDetail(emailNo);

    model.addAttribute("emailDto", emailDetail);
    return "email/emailForm/sendMailRe";
  }

  @GetMapping("emailList")
  public String emailList(HttpServletRequest request, Model model) {
    List<EmailDto> emailList = emailService.emailList(request);
    model.addAttribute("emailList", emailList);

    String folderName = request.getParameter("folderName");
    model.addAttribute("folderName", folderName);

    return "email/mailFolder/getMailRepositoryRe";
  }

  @GetMapping("emailDetail")
  public String emailDetail(Long emailNo, Model model, HttpServletRequest request) {
    EmailDto emailDetail = emailService.emailDetail(emailNo);
    emailService.emailUpdateSeen(request, emailNo);
    model.addAttribute("folderName", request.getParameter("folderName"));
    model.addAttribute("emailDetail", emailDetail);
    return "email/emailForm/readMailRe";
  }

  @PostMapping("moveToTrashOrJunk")
  public String moveToTrashOrJunk(HttpServletRequest request, @RequestParam List<Long> emailNoList) {
    emailNoList.removeIf(emailNo -> emailNo.equals(0L));

    if (request.getParameter("moveFolder").equals("justSeen")) {
      emailService.emailUpdateSeenMul(request, emailNoList);
      return "redirect:/email/emailList?folderName=" + request.getParameter("folderName");
    }

    if (emailNoList.size() <= 0) {
      return "redirect:/email/emailList?folderName=" + request.getParameter("folderName");
    }
    if (request.getParameter("folderName").equals("Trash") || request.getParameter("folderName").equals("Junk E-mail")) {
      emailService.deleteMessage(request, emailNoList);
    } else {
      emailService.moveToTrashOrJunk(request, emailNoList);
    }
    return "redirect:/email/emailList?folderName=" + request.getParameter("folderName");
  }

  @PostMapping("emailSend")
  public ResponseEntity<String> emailSend(HttpServletRequest request, EmailDto emailDto, @RequestParam("recipients") List<String> recipients,
                          @RequestParam(value = "fileName", required = false) List<MultipartFile> fileName,
                          @RequestParam(value = "addedFileName", required = false) List<String> addedFileName,
                          @RequestParam(value = "addedFile", required = false) List<String> addedFile) {

    emailDto.setEmailRecipient(recipients);
    List<String> fileNames = new ArrayList<>();
    List<String> filePaths = new ArrayList<>();
    try {
      Path fileAddress = Paths.get("src/main/resources/static/files");
      List<MultipartFile> fileList = fileName;
      if (fileList != null) {
        for (int i = 0; i < fileList.size(); i++) {
          String fileRealName = fileList.get(i).getOriginalFilename();
          String extension = FilenameUtils.getExtension(fileRealName);
          String randomParseFileName = UUID.randomUUID() + "." + extension;
          Files.createDirectories(fileAddress);
          Path targetPath = fileAddress.resolve(randomParseFileName).normalize();
          fileList.get(i).transferTo(targetPath);

          fileNames.add(fileRealName);
          filePaths.add("src/main/resources/static/files/" + randomParseFileName);
        }
      }

      for (int i = 0; i < addedFileName.size(); i++) {
        fileNames.add(addedFile.get(i));
        filePaths.add(addedFileName.get(i));
      }

    } catch (NullPointerException | IOException e) {
      log.info("추가할 파일 없음");
    }
    emailDto.setEmailAttachment(fileNames);
    emailDto.setEmailAttachmentFileName(filePaths);

    try {
      emailService.emailSend(emailDto, getEmployee(request));
    } catch (MailSendException e) {
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Resource not found");
    } catch (FileNotFoundException e) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("An Unexpected error occurred.");
    }

    return ResponseEntity.ok("Success");
  }

  @GetMapping("fileError")
  public String locationFileError() {
    return "email/emailForm/errorMail_2";
  }

  @GetMapping("sendError")
  public String locationSendError() {
    return "email/emailForm/errorMail_1";
  }
  @GetMapping("downloadAttachment")
  public ResponseEntity<ByteArrayResource> downloadAttachment(@RequestParam("fileName") String fileName) {
    return emailService.downloadAttachment(fileName);
  }

  private EmployeeDto getEmployee(HttpServletRequest request) {
    HttpSession session = request.getSession();
    return (EmployeeDto) session.getAttribute("LOGIN_EMPLOYEE");
  }

}
