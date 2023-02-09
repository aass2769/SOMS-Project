package project.soms.submission.dto;

import lombok.Data;

@Data
public class AnnualLeaveDto {

  private Long annualLeaveNo;
  private String annualLeaveSection;
  private String annualLeavePjt;
  private String annualLeaveStart;
  private String annualLeaveEnd;
  private Integer annualLeaveTime;
  private String annualLeaveContent;

  public AnnualLeaveDto(String annualLeaveSection, String annualLeavePjt, String annualLeaveStart, String annualLeaveEnd, Integer annualLeaveTime, String annualLeaveContent) {
    this.annualLeaveSection = annualLeaveSection;
    this.annualLeavePjt = annualLeavePjt;
    this.annualLeaveStart = annualLeaveStart;
    this.annualLeaveEnd = annualLeaveEnd;
    this.annualLeaveTime = annualLeaveTime;
    this.annualLeaveContent = annualLeaveContent;
  }
}
