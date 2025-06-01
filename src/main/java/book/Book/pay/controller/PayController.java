package book.Book.pay.controller;

import book.Book.pay.dto.PayResponse;
import book.Book.pay.service.PayService;
import book.Book.pay.dto.PayCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    // 결제 생성
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String processPayment(@RequestBody PayCreateRequest request) {
        payService.processPay(request);
        return "결제 처리가 완료되었습니다";
    }

    // 결제 단건 조회
    @GetMapping("/{payId}")
    @ResponseStatus(HttpStatus.OK)
    public PayResponse payFindOne(@PathVariable("payId") Long payId) {
        return payService.findOnePay(payId);
    }

    // 결제 전체 조회
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<PayResponse> payFindAll() {
        return payService.findAllPay();
    }

    // 로그인중인 회원의 결제한 내역들 전체조회
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PayResponse> payFindAllByUser() {
        return payService.findAllUserPay();
    }

    // 결제 취소
    @PutMapping("/{payId}")
    @ResponseStatus(HttpStatus.OK)
    public String payCancel(@PathVariable("payId") Long payId) {
        payService.cancelPay(payId);
        return "결제 취소가 완료됐습니다.";

    }
}


