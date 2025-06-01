package book.Book.mapper;

import book.Book.pay.domain.Pay;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PayMapper {


    // 결제 생성
    @Insert("INSERT INTO pay (user_id, order_id, card_company, card_number, pay_price, pay_status, created_at) " +
            "VALUES (#{pay.userId}, #{pay.order.id}, #{pay.cardCompany}, #{pay.cardNumber}, #{pay.payPrice}, #{pay.payStatus}, #{pay.createdAt})")
    void savePay(@Param("pay") Pay pay);

    // 결제 유효성 검사 있으면 1이상 없으면 0
    @Select("SELECT COUNT(*) FROM pay WHERE order_id = #{orderId} AND user_id = #{userId} AND pay_status = 'COMPLETE'")
    int countCompletePay(@Param("orderId") Long orderId, @Param("userId") Long userId);

    // 결제 단건 조회
    Pay findById(Long id);

    // 결제 전체 조회
    List<Pay> findAll();

    // 특정 회원의 결제 전체 조회
    List<Pay> findByUserId(Long userId);

    @Update("UPDATE pay SET pay_status = #{pay.payStatus} WHERE id = #{pay.id}")
    void updatePay(Pay pay);
}
