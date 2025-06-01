package book.Book.pay.service;

import book.Book.pay.dto.PayCreateRequest;
import book.Book.pay.dto.PayResponse;

import java.util.List;

public interface PayService {

    // 결제
    void processPay(PayCreateRequest request);

    // 결제 내역 단건 조회
    PayResponse findOnePay(Long id);

    // 결제 내역 전체 조회
    List<PayResponse> findAllPay();

    // 로그인 중인 회원의 결제 내역 보기
    List<PayResponse> findAllUserPay();

    // 결제 취소 (성공일떄만)
    void cancelPay(Long id);
}
