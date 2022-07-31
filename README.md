# toyProject
- RestAPI 방식으로 구현한 개인 프로젝트

## 프로젝트 소개
- 핵심 기능은 콘텐츠 등록 기능입니다.
- 파일 업로드와 댓글 기능을 구현하였습니다.
- service계층에 대한 단위 테스트와 webLayer 계층에서는 통합테스트를 진행하였습니다.<BR>
  또한 , 응답 값마다 다음 상태로 이동할 수 있게 하기 위하여 링크를 추가하였으며, <BR>
  응답 값의 정볼르 담아줄 profile을 위해, Spring RestDocs를 도입하여 테스트와 문서화를 같이 진행하였습니다.
  
- 목록 조회의 경우, QueryDsl을 이용하여 동적 쿼리로 검색 기능을 추가하였습니다.<BR>
  특히 정렬 조건을 동적으로 구현하기 위하여, OrderSpecifier를 사용하여 이용할 수 있도록 처리하였습니다.
  
- Exception 처리 같은 경우, ResponseEntityExceptionHandler을 상속받아, mvc의 전반적인 예외를 처리하게끔 하였으며,<br>
  bean Validation 위반의 경우, Exception을 custom하여 처리하였습니다.
  
  ## 사용 기술
  - Java , Junit5 , MockMvc
  - Spring Mvc , Spring Rest Docs , Spring Data Jpa , QueryDsl
  - H2
