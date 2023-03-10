package project.soms.board.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import project.soms.board.dto.BoardDto;
import project.soms.board.dto.CommentDto;
import project.soms.board.service.BoardService;
import project.soms.employee.dto.EmployeeDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {

	private final BoardService boardService;
		
	//전사 게시판,부서 게시판에 게시글 리스트 SELECT하는 메서드
	//검색 하는 메서드
	@GetMapping("boardList") // board/main
	public String selectBoard(Model model, BoardDto boardDto,
										   HttpServletRequest request) {
		
		//카테고리에서 원하는 부서 클릭 시 url로 boardSection 파라미터를 보내줌.
		String boardSection = request.getParameter("boardSection");
		
		//전체 게시물 수
		int total = boardService.selectBoardTotal(boardSection);
		
		//  전체 게시물 수에 따른 페이지의 개수  (double) 12/10 -> ceil(1.2) ->int(2.0) -> 2
		int totalPage = (int) Math.ceil((double)total/boardDto.getPageLimit());
		
		// pageOffset = 페이지당 제외하고 출력해야하는 글 개수, viewPage = 페이지 개수 중 페이지
		boardDto.setPageOffset((boardDto.getViewPage() - 1) * boardDto.getPageLimit());
		
		//전체 게시글 수가 45개이고 10개씩 출력한다고 가정하면
		//1page -> 45 ~ 36 2page -> 35 ~ 26 ....
		// 1page의 startRowNo(시작 행 넘버) = total(총게시물 수) - (viewPage(현재페이지) - 1) * 10(출력할 개시글 수) 
		int startRowNo = total - (boardDto.getViewPage() - 1) * boardDto.getPageLimit();
		
		//공지 없는 리스트
		List<BoardDto> boardList = new ArrayList<>();
		boardList =	boardService.selectBoard(boardSection, boardDto);
		
		//공지 있는 리스트
		List<BoardDto> noticeBoardList = new ArrayList<>();
		noticeBoardList = boardService.selectNoticeBoard(boardSection, boardDto);
		
		// total(전체 게시물 수)와 totalPage(게시물 수에 따른 페이지의 개수)를 검색 조건일 떄도 적용하기 위해 재할당
		// 만약 검색 결과가 없을 시 전체 게시물 수를 0으로 할당.
		if(boardList.size() == 0) {
			total = 0;
		} else {
			total = boardList.get(0).getTotal();
		}
		
		//전체 게시물 수에 따른 페이지의 개수  (double) 12/10 -> ceil(1.2) ->int(2.0) -> 2
		totalPage = (int) Math.ceil((double)total/boardDto.getPageLimit());
		
		model.addAttribute("total", total);
		model.addAttribute("viewPage", boardDto.getViewPage());
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("startRowNo", startRowNo);
		model.addAttribute("boardList" , boardList);
		model.addAttribute("boardSection", request.getParameter("boardSection"));
		model.addAttribute("noticeBoardList", noticeBoardList);
		model.addAttribute("pageLimit", boardDto.getPageLimit());
		return "board/board";
	}

	//게시판 작성 페이지 불러오는 메서드
	@GetMapping("boardWrite")
	public String writeBoard(Model model, BoardDto boardDto, HttpServletRequest request) {
		
		BoardDto readBoardDto = new BoardDto();
		String boardSection = boardDto.getBoardSection();
		Integer pageLimit = boardDto.getPageLimit();
		if(request.getParameter("boardNo") != null) {
			Integer boardNo = Integer.parseInt(request.getParameter("boardNo"));
			//게시글 상세내용 불러오는 메서드
			readBoardDto = boardService.readBoard(boardNo);
			
		}
		model.addAttribute("pageLimit", pageLimit);
		model.addAttribute("readBoardDto", readBoardDto);
		model.addAttribute("employee", loginEmployee(request));
		model.addAttribute("boardSection", boardSection);
		
		return "board/writeBoard";
	}
	
	
	//게시판 작성하는 post 메서드
	@PostMapping("boardWrite")
	public String writeBoard(Model model, String boardSection, BoardDto boardDto, HttpServletRequest request, RedirectAttributes redirectAttributes, BoardDto readBoardDto) {

		int pageLimit = boardDto.getPageLimit();
		if(boardDto.getBoardAnnouncement() == null) {
			boardDto.setBoardAnnouncement("공지없음");
		}
		//INSERT 메서드 실행
		boardService.insertBoard(boardDto);
		
		//boardList 메서드로 redirect 할 때 boardSection값을 보내줌.
		redirectAttributes.addAttribute("boardSection", boardSection);
		redirectAttributes.addAttribute("pageLimit", pageLimit);
		return "redirect:/board/boardList";
	}
	
	//응답이 틀리면 http 메서드 오류가 발생할 수 있음 (클라이언트오류 405ERROR)
	//게시판 수정 메서드
	@PostMapping("boardUpdate")
	public String updateBoard(Model model, HttpServletRequest request , BoardDto readBoardDto, RedirectAttributes redirect) {
		
		String page_limit = request.getParameter("pageLimit");
		int pageLimit = 10;
		if(page_limit != null) {
			pageLimit = Integer.parseInt(page_limit);
		}
		
		String boardSection = request.getParameter("boardSection");
		if(readBoardDto.getBoardAnnouncement() == null) {
			readBoardDto.setBoardAnnouncement("공지없음");
		}

		boardService.updateBoard(readBoardDto);

		redirect.addAttribute("pageLimit",pageLimit);
		redirect.addAttribute("boardNo",readBoardDto.getBoardNo());
		redirect.addAttribute("boardSection", "boardSection");
		
		return "redirect:/board/boardRead";
	}
	
	
	//세션 가져오는 메서드
	public EmployeeDto loginEmployee(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(session == null) {
			System.out.println("세션이 없습니다.");
		}
		
		EmployeeDto employeeDto = (EmployeeDto) session.getAttribute("LOGIN_EMPLOYEE");
		return employeeDto;
	}
	
	//게시물 상세내용과 댓글 가져오는 메서드
	@GetMapping("boardRead")
	public String readBoard(Model model, Integer boardNo, HttpServletRequest request) {
		//given
		boardService.updateViews(boardNo);		//조회 수 update
		BoardDto readBoardDto = new BoardDto();	
		try {
			readBoardDto = boardService.readBoard(boardNo);	//게시글 상세내용
		} catch (NullPointerException e) {
			
			return "redirect:/board/errorBoardPage";
		}
		List<CommentDto> commentList = boardService.selectComment(boardNo);	//댓글
		Integer pageLimit = Integer.parseInt(request.getParameter("pageLimit")); //페이지당 게시글 출력
		
		String page_No = request.getParameter("pageNo");
		
		int pageNo = 0;
		
		if(page_No != null) {
			pageNo = Integer.parseInt(page_No);
		}
		
		model.addAttribute("pageNo", pageNo);
		//add model
		model.addAttribute("pageLimit", pageLimit);
		model.addAttribute("employee", loginEmployee(request)); // 세션
		model.addAttribute("readBoardDto", readBoardDto);
		model.addAttribute("commentList", commentList);
		
		//return
		return "board/readBoard";
	}
	
	//댓글 작성하는 메서드
	@PostMapping("commentWrite")
	public String writeComment(HttpServletRequest request, String commentContent, Integer boardNo, RedirectAttributes redirectAttributes) {
		
		Integer pageLimit = Integer.parseInt(request.getParameter("pageLimit"));
		EmployeeDto employeeDto = loginEmployee(request);
		boardService.writeComment(commentContent, employeeDto, boardNo);
		redirectAttributes.addAttribute("pageLimit", pageLimit);
		redirectAttributes.addAttribute("boardNo", boardNo);
		
		return "redirect:/board/boardRead";
	}
	
	//게시글 삭제 메서드
	@PostMapping("boardDelete")
	public String deleteBoard(Integer boardNo, int pageLimit, String boardSection, RedirectAttributes redirectAttributes) {

		boardService.deleteBoard(boardNo);
		
		redirectAttributes.addAttribute("pageLimit", pageLimit);
		redirectAttributes.addAttribute("boardSection", boardSection);
		return "redirect:/board/boardList";
	}
	
	//댓글 삭제 메서드
	@PostMapping("commentDelete")
	public String deleteComment(CommentDto commentDto, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		boardService.deleteComment(commentDto);
		Integer pageLimit = Integer.parseInt(request.getParameter("pageLimit"));
		Integer boardNo = commentDto.getBoardNo();

		redirectAttributes.addAttribute("pageLimit", pageLimit);
		redirectAttributes.addAttribute("boardNo", boardNo);
		return "redirect:/board/boardRead";
	}
	
	//게시판 다음글 메서드
	@GetMapping("moveBoardRead")
	public String readBoardMove(String boardSection, Integer boardNo ,HttpServletRequest request, RedirectAttributes redirectAttributes) {
		
		BoardDto boardPage = boardService.readBoardMove(boardSection, boardNo);
		Integer pageLimit = Integer.parseInt(request.getParameter("pageLimit"));
		
		String moveButton = request.getParameter("moveButton");

		
		/* 만약 이전글이고 이전글No가 0이아니면 redirect
		 * 이전 글이고 이전글No가 0이면 게시글 No와 pageNo라는 값을 redirect. (pageNo는 0으로 dto에서 초기화되있음.)
		 * 만약 다음글이고 다음글No가 0아아니면 redirect
		 * 다음 글이고 다음글No가 0이면 게시글 No와 pageNo라는 값을 redirect*/
		
		int pageNo = 0;
		
		if("before".equals(moveButton) && boardPage.getBeforeBoardNo() != 0) {
			redirectAttributes.addAttribute("boardNo", boardPage.getBeforeBoardNo());
		} else if("before".equals(moveButton) && boardPage.getBeforeBoardNo() == 0){
			redirectAttributes.addAttribute("boardNo", boardNo);
			pageNo = 1;
		} else if("after".equals(moveButton) && boardPage.getAfterBoardNo() != 0) {
			redirectAttributes.addAttribute("boardNo", boardPage.getAfterBoardNo());
		} else if("after".equals(moveButton) && boardPage.getAfterBoardNo() == 0) {
			redirectAttributes.addAttribute("boardNo", boardNo);
			pageNo = 2;
			

		}
		
		
		redirectAttributes.addAttribute("pageNo", pageNo);
		redirectAttributes.addAttribute("pageLimit", pageLimit);
		
		return "redirect:/board/boardRead";
	}
	
	@GetMapping("errorBoardPage")
	public String errorBoardPage() {
		return "board/errorBoardPage";
	}
	
}
