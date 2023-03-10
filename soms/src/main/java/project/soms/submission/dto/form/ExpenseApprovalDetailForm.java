package project.soms.submission.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//지출 서식을 조회할 때 사용되는 data클래스
public class ExpenseApprovalDetailForm {

  /**
   * 기본적인 서식 관련 데이터와
   * 지출 상세 내용을 담는 데이터를 선언하거나 저장함
   */
  private Long submissionNo;
  private String submissionPri;
  private String submissionSection;
  /**
   * 기본적인 서식 관련 데이터와
   * 연차 상세 내용을 담는 데이터를 선언하거나 저장함
   */
  private String submissionStatus;
  /**
   * 결재가 가능한지 아닌지를 구분하기 위한 변수
   * !!!데이터베이스 컬럼에는 없고, 쿼리에서 생성한 컬럼!!!
   */
  private String approvalAble;
  private Long proposerEmployeeNo;
  private Long approverEmployeeNo;
  private Long expenseNo;
  private String expenseSection;
  private String expensePjt;
  private String expenseDate;
  private Integer expenseCost;
  private String expenseContent;
  /**
   * 결재 반려를 시킬 때 기안자에게 확인 요청을 주기위한 기안자의 값이 담긴 PK값
   * 결재 반려외에 결재 완료에서 1회 사용됨
   * 해당 결재건을 미열람으로 변경하여 기안자로 접근시 해당 건을 구분함
   */
  private Long rejectValue;

  public ExpenseApprovalDetailForm(Long submissionNo, String submissionPri, String approvalAble, Long proposerEmployeeNo, Long approverEmployeeNo, Long expenseNo, String expenseSection, String expensePjt, String expenseDate, Integer expenseCost, String expenseContent) {
    this.submissionNo = submissionNo;
    this.submissionPri = submissionPri;
    this.approvalAble = approvalAble;
    this.proposerEmployeeNo = proposerEmployeeNo;
    this.approverEmployeeNo = approverEmployeeNo;
    this.expenseNo = expenseNo;
    this.expenseSection = expenseSection;
    this.expensePjt = expensePjt;
    this.expenseDate = expenseDate;
    this.expenseCost = expenseCost;
    this.expenseContent = expenseContent;
  }
}
