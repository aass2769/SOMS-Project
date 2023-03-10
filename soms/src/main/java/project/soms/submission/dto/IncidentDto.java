package project.soms.submission.dto;

import lombok.Data;

@Data
//시말서 필드값
public class IncidentDto {

  private Long incidentNo;
  private String incidentSection;
  private String incidentPjt;
  private String incidentContent;

  public IncidentDto(String incidentSection, String incidentPjt, String incidentContent) {
    this.incidentSection = incidentSection;
    this.incidentPjt = incidentPjt;
    this.incidentContent = incidentContent;
  }

  public IncidentDto() {
  }
}
