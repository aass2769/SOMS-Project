package project.soms.common.dto;

import lombok.Data;

@Data
public class CommonDto {

	private String employeeTeam;
	private String manage;
	private String employeeName;
	private long employeeNo;
	private Integer employeeAttendance;
	
	public CommonDto() {}
	
	
	public CommonDto(String employeeTeam, String manage, String employeeName) {
		this.employeeTeam = employeeTeam;
		this.manage = manage;
		this.employeeName = employeeName;
	}
}
