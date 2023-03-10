package project.soms.submission.dto;

import lombok.Data;

@Data
//연장근로신청서 필드값
public class OvertimeDto {

  private Long overtimeNo;
  private String overtimeSection;
  private String overtimePjt;
  private String overtimeStartDate;
  private Integer overtimeStartTime;
  private String overtimeEndDate;
  private Integer overtimeEndTime;
  private String overtimeContent;

  public OvertimeDto(String overtimeSection, String overtimePjt, String overtimeStartDate, Integer overtimeStartTime, String overtimeEndDate, Integer overtimeEndTime, String overtimeContent) {
    this.overtimeSection = overtimeSection;
    this.overtimePjt = overtimePjt;
    this.overtimeStartDate = overtimeStartDate;
    this.overtimeStartTime = overtimeStartTime;
    this.overtimeEndDate = overtimeEndDate;
    this.overtimeEndTime = overtimeEndTime;
    this.overtimeContent = overtimeContent;
  }

  public OvertimeDto() {
  }
}
