package project.soms.submission.dto;

import lombok.Data;

@Data
public class BusinessTripDto {

  private Long businessTripNo;
  private String businessTripSection;
  private String businessTripStart;
  private String businessTripEnd;
  private Integer businessTripTime;
  private String businessTripDestination;
  private String businessTripContent;

  public BusinessTripDto(String businessTripSection, String businessTripStart, String businessTripEnd, Integer businessTripTime, String businessTripDestination, String businessTripContent) {
    this.businessTripSection = businessTripSection;
    this.businessTripStart = businessTripStart;
    this.businessTripEnd = businessTripEnd;
    this.businessTripTime = businessTripTime;
    this.businessTripDestination = businessTripDestination;
    this.businessTripContent = businessTripContent;
  }
}