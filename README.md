# 프로젝트 소개

- 카카오 API 를 이용한 도서 구매 REST API 입니다.
- Spring Boot + MyBatis 로 구현했습니다.
- 개발 기간 : 25.05.13 ~ 25.05.20
- 개발 인원 : 1명

# 기술 스택

- Language : Java
- JDK : 17
- Framework : Spring 6.2.5, SpringBoot 3.4.4
- Library :  Spring Security
- BuildTool : Gradle
- DB : MySQL

# ERD
<img src="https://github.com/user-attachments/assets/093fae6a-9bc7-4da6-be31-f7ce38272791"/>
<img src="https://github.com/user-attachments/assets/448e50ea-12ea-473b-890a-e3d71882e729"/>


# API 설계
<img src="https://github.com/user-attachments/assets/61f0071f-ca23-482d-b99d-c5b31d0c8577"/>
<img src="https://github.com/user-attachments/assets/5cd6b073-0e26-4a02-baf8-5744e72b32f6"/>
<img src="https://github.com/user-attachments/assets/a5b0ccc0-2c4f-4e5f-b192-7b7303e1a7a7"/>
<img src="https://github.com/user-attachments/assets/ca703ad4-1363-47cc-877c-b0fefc4d3e03"/>
<img src="https://github.com/user-attachments/assets/6b0f1886-c9e7-433a-9d04-5614fadf083f"/>
<img src="https://github.com/user-attachments/assets/6411a4b5-399e-4bda-939e-95d0e7dc05c7"/>
<img src="https://github.com/user-attachments/assets/1600162f-4b57-4be3-b516-2d5ac00d32c5"/>

# 프로젝트 구조
<img src="https://github.com/user-attachments/assets/7496a76d-190f-4152-adae-bc9496f26a20"/>

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
Mockito + Junit5 을 통해서 단위테스트로 진행했습니다.


<details>
<summary>테스트</summary>

- 회원(User)<br>
  <img src="https://github.com/user-attachments/assets/84d41d85-9b11-4cbb-8d27-dc00c0eddabb"/>


- 책(Book)<br>
  <img src="https://github.com/user-attachments/assets/e9a03ef6-ac03-4c48-889c-2870c74cce2a"/>

- 장바구니(Cart)<br>
  <img src="https://github.com/user-attachments/assets/ba33ff20-18ec-42a2-bb5b-589adeab665b"/>

- 주문(Order)<br>
  <img src="https://github.com/user-attachments/assets/2aa7db97-8cc3-4631-a7bd-e214768eb44a"/>

- 결제(Pay)<br>
  <img src="https://github.com/user-attachments/assets/90820a01-332b-47fe-8f54-0fa902697399"/>

- 리뷰(Review)<br>
  <img src="https://github.com/user-attachments/assets/21f0514e-89da-4c6e-b72e-ac189e473e80"/>

  
- 별점(Rating)<br>
  <img src="https://github.com/user-attachments/assets/d6876f50-5328-4d45-b426-1d5f531eef17"/>



</details>




