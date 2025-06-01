# 프로젝트 소개

- 카카오 API 를 이용한 도서 구매 REST API 입니다.
- Spring Boot + MyBatis 로 구현했습니다.
- 개발 기간 : 25.05.13 ~ 25.05.20
- 개발 인원 : 1명

# 기술 스택

- Language : Java
- JDK : 17
- Framework : Spring 6.2.5 , SpringBoot 3.4.4
- Library :  Spring Security
- BuildTool : Gradle
- DB : MySQL

# API 설계

# 프로젝트 구조


# 프로젝트 기능

<details>
<summary>회원(User)</summary>

- 내 정보 조회
- 회원 단건 조회
- 회원 전체 조회
- 회원 가입
- 로그인
- 회원 정보 수정
- 회원 탈퇴
- 회원 삭제

</details>

<details>
<summary>책(Book)</summary>

- 책 단건 조회
- 책 전체 조회
- 책 생성
- 책 수정
- 책 삭제

</details>

<details>
<summary>장바구니(Cart)</summary>

- 장바구니 전체 조회
- 장바구니 단건 조회
- 현재 내가 담은 장바구니 조회
- 장바구니 생성
- 장바구니 수정
- 장바구니 삭제

</details>

<details>
<summary>주문(Order)</summary>

- 주문 단건 조회
- 주문 전체 조회
- 현재 내가 주문한 주문 조회
- 주문 생성<br>
    + 회원의 장바구니에 존재하고 있는 책에서만 주문 생성이 가능하며, 주문생성이 완료 될 경우에 장바구니 정보는 삭제시킵니다.<br>
    + 장바구니 품목 개수보다 더 많이 주문 할 경우 예외가 발생합니다.<br>
    + 회원은 주문을 생성할떄 원하는 마일리지 만큼 사용할수있으며 가지고 있는 마일리지 보다 많은 마일리지를 입력하면 예외가 발생합니다<br>

- 주문 취소<br>
    + 회원은 주문 상태가 PENDING, COMPLETE 일때만 주문 취소가 가능하다<br>
    + FAIL, CANCEL 일때는 예외가 발생한다<br>

</details>

<details>
<summary>결제(Pay)</summary>

- 결제 단건 조회
- 결제 전체 조회
- 내가 결제한 결제 조회
- 결제 생성
- 결제 취소<br>
    + 결제 상태가 COMPLETE 일때만 결제 취소가 가능하다<br>
    + FAIL, CANCEL 일때는 예외가 발생한다<br>

</details>


<details>
<summary>리뷰(Review)</summary>

+회원은 구매한 책에 대해서 리뷰를 남길 수 있으며, 리뷰를 작성할떄 별점(Rating)이 1:1 로 생성된다<br>

- 리뷰 단건 조회
- 리뷰 전체 조회
- 내가 등록한 리뷰 조회
- 리뷰 생성
- 리뷰 수정
- 리뷰 삭제

</details>

<details>
<summary>별점(Rating)</summary>

- 책 별점 그룹 조회
- 책 평균 별점 조회
- 특정 사용자의 책 별점 조회

</details>

# 테스트 진행

# 느낀점

- 기존에는 ORM 방식인 JPA 에서 SQL Mapping 방식인 Mybatis 로 구현해봤는데 크게 다른 부분은 개발자가 직접 쿼리를 작성하고 스키마도 직접 만들고 변경해야한다는 것이다.<br>
  또, 빌드를 할떄도 MyBatis 는 JPA 와 다르게 1~2초면 바로 빌드가 됐으며 빠른 면모를 보여줬다
  단순한 CRUD 부분에서는 JPA 가 더 강점을 보이는 것 같지만,<br>
  MyBatis 는 테이블을 조인같이 복잡한 쿼리에서는 개발자가 직접 최적화 쿼리를 짜서 성능을 극대화 할수있을 것 같다



