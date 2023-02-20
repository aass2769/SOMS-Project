package project.soms.mypage.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.soms.employee.dto.EmployeeDto;
import project.soms.mypage.dto.AttendanceCheckDto;
import project.soms.mypage.dto.WorkDto;
import project.soms.mypage.repository.AttendanceRepository;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService{

	private final AttendanceRepository attendanceRepository ;
	
	@Override
	public void workcheck(HttpServletRequest req) {
		
		EmployeeDto employee = (EmployeeDto) req.getSession().getAttribute("employee");
		long employeeNo = employee.getEmployeeNo();
		
		Optional<String> bool = Optional.ofNullable(attendanceRepository.goToWorkCheck(employeeNo));
	
		Date now = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = sdf.format(now);
		 
		sdf = new SimpleDateFormat("HH");
		Integer nowHour = Integer.parseInt(sdf.format(now));
		
		sdf = new SimpleDateFormat("mm");
		int nowMinute = Integer.parseInt(sdf.format(now));
		
		if(nowMinute > 30) {
			nowHour+=1;
		}
		
		if(bool.isPresent()) {
			long attendanceNo = getAttendanceNum(employeeNo);
			WorkDto leaveToWorkDto = new WorkDto(employeeNo, nowHour, attendanceNo);
			leaveToWork(leaveToWorkDto);
		}else {
			WorkDto goToWorkDto = new WorkDto(employeeNo, nowDate, nowHour);
			goToWork(goToWorkDto);
		}
		
	}

	@Override
	public void goToWork(WorkDto goToWorkDto) {
		attendanceRepository.goToWork(goToWorkDto);
	}

	@Override
	public long getAttendanceNum(long employeeNo) {
		return attendanceRepository.getAttendanceNum(employeeNo);
	}
	
	@Override
	public void leaveToWork(WorkDto leaveToWorkDto) {
		attendanceRepository.leaveToWork(leaveToWorkDto);
	}
	
	@Override
	public List<AttendanceCheckDto> attendanceCheck(long employeeNo,String ym) {
		return attendanceRepository.attendanceCheck(employeeNo, ym);
	}
	
	@Override
	public String getAttendanceDate(String attendanceDate) {
				
		if(attendanceDate == null) {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			return sdf.format(now);
		}else {
			return attendanceDate;
		}
		
	}
	
	@Override
	public List<String> getAttendanceAtSixMonths(){
		
		LocalDate localNow;
		String localNow_Text;
		List<String> attendanceAtSixMonths = new ArrayList<>();
		
		for(int i = 0; i < 6; i++) {
			localNow = LocalDate.now().minusMonths(i);
			localNow_Text = localNow.toString().substring(0,7);
			attendanceAtSixMonths.add(localNow_Text);
		}
		
		Collections.reverse(attendanceAtSixMonths);
		
		return attendanceAtSixMonths;
		
	}
	
	@Override
	public AttendanceCheckDto getWorkTime(long employeeNo, String workDate) {
		return attendanceRepository.getWorkTime( employeeNo, workDate);
	}
	
	@Override
	public String getWeekWorkTime(long employeeNo) {
		
		Integer DayWorkingHours = 0;
		
		Integer weekWorkingHours = 0;
		
		Integer GoToTime = 0;
		
		Integer LeaveToTime = 0;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		
		for(int i = 0; i<7; i++) {;
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + i);
			String Today = format.format(cal.getTime());
			
			AttendanceCheckDto times = getWorkTime(employeeNo, Today);
			
			if(times != null) {				
				
				
				if (times.getAttendanceLeavetotime() == null) {
					GoToTime = 0;
					LeaveToTime = 0;
					
				}else {
					GoToTime = times.getAttendanceGototime();
					LeaveToTime = times.getAttendanceLeavetotime();
				}
				DayWorkingHours = LeaveToTime - GoToTime;
				weekWorkingHours += DayWorkingHours;
			}
		}
		
		return weekWorkingHours.toString();
	}
	
	
	

}