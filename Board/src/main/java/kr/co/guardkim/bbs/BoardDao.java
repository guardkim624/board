package kr.co.guardkim.bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BoardDao {

	private Connection conn;
	private ResultSet rs; // executeQuery(String sql)을 통해 쿼리 실행하면 ResultSet타입으로 반환을 해주어 결과값을 저장할 수 있다.
	
	//기본 생성자
	public BoardDao() {
		try {
			String dbURL = "jdbc:oracle:thin:@localhost:1521:xe";
			String dbID = "board";
			String dbPassword = "java";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	//작성일자 메소드
	public String getDate() {
//		String sql = "SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM DUAL";
//		String sql = "SELECT TO_DATE(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM DUAL";
//		String sql = "SELECT SYSDATE FROM DUAL";
		String sql = "SELECT NOW()";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ""; //데이터베이스 오류
	}
	
	//게시글 번호 부여 메소드
	public int getNext() {
		//현재 게시글을 내림차순으로 조회하여 가장 마지막 글의 번호를 구한다
		String sql = "select boardId from bbs order by boardId desc";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1; //첫 번째 게시물인 경우
		}catch (Exception e) {
			e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	
	//글쓰기 메소드
	public int write(String bbsTitle, String userId, String bbsContent) {
		String sql = "insert into bbs values(?, ?, ?, TO_DATE(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'), ?, ?)";
//		String sql = "insert into bbs values(?, ?, ?, ?, ?, ?)";
//		String sql = "insert into bbs values(?, ?, ?, SYSDATE, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, getNext());
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userId);
//			pstmt.setString(4, getDate()); //////// 주석 처리
			pstmt.setString(4, bbsContent);
			pstmt.setInt(5, 1); //글의 유효번호
			return pstmt.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return -1; //데이터베이스 오류
	}
	
	//게시글 리스트 메소드
		public ArrayList<BoardVo> getList(int pageNumber){
//			String sql = "select * from bbs where boardId < ? and boardAvailable = 1 order by boardId desc limit 10";
			String sql = "select * from bbs where boardAvailable=1 order by boardId";
//			String sql = "select * from bbs order by boardId";
			ArrayList<BoardVo> list = new ArrayList<BoardVo>();
			try {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					BoardVo bbs = new BoardVo();
					bbs.setBoardId(rs.getInt(1));
					bbs.setBoardTitle(rs.getString(2));
					bbs.setUserId(rs.getString(3));
					bbs.setBoardDate(rs.getString(4));
					bbs.setBoardContent(rs.getString(5));
					bbs.setBoardAvailable(rs.getInt(6));
					list.add(bbs);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
		
		//페이징 처리 메소드
		public boolean nextPage(int pageNumber) {
			String sql = "select * from bbs where boardId < ? and boardAvailable = 1";
			try {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					return true;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	
}
