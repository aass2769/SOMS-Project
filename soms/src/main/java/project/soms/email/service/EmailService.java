package project.soms.email.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import project.soms.email.dto.EmailDto;
import project.soms.employee.dto.EmployeeDto;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.List;

public interface EmailService {

  List<EmailDto> emailList(HttpServletRequest request);

  EmailDto emailDetail(Long emailNo);

  void emailUpdateSeen(HttpServletRequest request, Long emailNo);

  void emailUpdateSeenMul(HttpServletRequest request, List<Long> emailNoList);

  void moveToTrashOrJunk(HttpServletRequest request, List<Long> emailNoList);

  void emailSend(EmailDto emailDto, EmployeeDto employee) throws FileNotFoundException, MailSendException;

  void deleteMessage(HttpServletRequest request, List<Long> emailNoList);

  ResponseEntity<ByteArrayResource> downloadAttachment(String emailFileName);
}
