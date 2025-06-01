package book.Book.mapper;

import book.Book.user.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    // loginId로 회원 찾기
    User findByLoginId(String loginId);

    // 유효성 검사
    @Select("SELECT COUNT(*) FROM user WHERE login_id = #{loginId}")
    int validateUserCount(String loginId);

    // 회원 가입
    @Insert("""
            INSERT INTO user (login_id, password, username, email, address, zipcode, mileage, total_amount, role, created_at)
            VALUES (#{loginId}, #{password}, #{username}, #{email}, #{address}, #{zipcode}, #{mileage}, #{totalAmount}, #{role}, #{createdAt})
            """)
    void signupUser(User user);

    // 회원 삭제
    @Delete("DELETE FROM user WHERE id = #{id}")
    void deleteUser(Long id);

    // 회원 단건 조회
    User findById(Long id);

    // 회원 전체 조회
    @Select("SELECT * FROM user")
    List<User> findAll();

    // 회원 수정
    void updateUser(User user);
}
