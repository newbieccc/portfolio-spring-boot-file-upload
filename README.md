# portfolio-spring-boot-file-upload

Spring Boot 기반의 파일 업로드 · CSV 처리 미니 포트폴리오 프로젝트입니다.

## 1. 프로젝트 소개

공공/기관/업무시스템에서 자주 볼 수 있는  
**파일 업로드 → 검증 → 처리 → 상태 조회** 흐름을  
작은 REST API 형태로 구현하는 것을 목표로 했습니다.

단순 업로드 기능만이 아니라,  
다음과 같은 **운영 관점 요소**를 함께 반영하려고 했습니다.

- 업로드 이력 관리
- 처리 상태 관리
- 실패 시 오류 메시지 기록
- 요청 추적용 traceId
- Swagger 기반 실행/테스트 가능
- H2 기반 로컬 재현 가능

## 2. 개발 목적

- 업무시스템 개발·운영 / 솔루션 구축·유지보수 성격의 포지션을 준비하면서,
- 실무에서 자주 접할 수 있는 다음 흐름을 직접 구현해보는 것을 목표로 했습니다.
    - CSV 파일 업로드
    - 데이터 검증
    - 처리 결과 저장
    - 상태 및 오류 조회
    - Swagger 문서화

## 3. 기술 스택

- Java 21
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Boot Actuator
- springdoc-openapi (Swagger)

## 4. 현재 구현 범위

### 4-1. 파일 업로드 API

- `POST /api/files`
- CSV 파일을 업로드하면 로컬 `./storage` 경로에 저장
- `file_job` 테이블에 업로드 이력 생성
- 최초 상태는 `UPLOADED`

### 4-2. CSV 처리 API

- `POST /api/files/{id}/process`
- 업로드된 CSV 파일을 읽고 검증 후 DB 저장
- 처리 상태를 아래와 같이 관리
    - `PROCESSING`
    - `SUCCESS`
    - `FAILED`

### 4-3. CSV 검증 규칙

- 헤더: `bizDate,itemCode,qty`
- `bizDate`: 날짜 파싱 가능해야 함
- `itemCode`: 공백 불가
- `qty`: 정수이며 0 이상

### 4-4. 처리 결과 저장

- 검증 통과 시 `processed_row` 테이블에 데이터 저장
- 재처리 시 기존 `processed_row` 삭제 후 재적재하는 단순 정책 적용

### 4-5. 오류 처리 / 운영 요소

- 공통 에러 응답 포맷 적용
    - `code`
    - `message`
    - `traceId`
    - `details`
- 전역 예외 처리기 적용
- traceId 기반 요청 추적
- 처리 실패 시 `file_job.error_message` 기록

## 5. DB 구조

### file_job

업로드 파일 1건의 상태/이력을 관리하는 테이블

주요 컬럼:

- `id`
- `original_file_name`
- `stored_path`
- `status`
- `error_message`
- `created_at`
- `processed_at`

### processed_row

CSV 처리 결과 데이터를 저장하는 테이블

주요 컬럼:

- `id`
- `file_job_id`
- `biz_date`
- `item_code`
- `qty`

## 6. 실행 방법

### 6-1. 실행

    ./gradlew bootRun

### 6-2. 접속 URL

- Swagger UI  
  `http://localhost:8080/swagger-ui/index.html`

- OpenAPI Docs  
  `http://localhost:8080/v3/api-docs`

- H2 Console  
  `http://localhost:8080/h2-console`

## 7. 테스트용 CSV 예시

### 정상 CSV

```csv
bizDate,itemCode,qty
2026-03-11,ITEM001,10
2026-03-11,ITEM002,0
```

### 실패 CSV

```
bizDate,itemCode,qty
2026-03-XX,ITEM001,10
```

## 8. 현재까지 확인한 동작

- 정상 CSV 업로드 가능
- 업로드 후 `file_job.status = UPLOADED`
- 처리 API 호출 후 정상 CSV는 `SUCCESS`
- 잘못된 날짜 형식의 CSV는 `FAILED`
- 실패 원인은 `error_message`에 기록됨
- 처리 성공 시 `processed_row`에 데이터 저장됨

## 9. 학습하면서 확인한 점

- Swagger에서 파일 업로드 UI가 보이도록 하려면 `multipart/form-data` 설정이 중요함
- Spring Boot와 Swagger(springdoc)는 버전 호환성이 중요함
- 실행 오류 발생 시
    - 증상 분리
    - 로그 확인
    - 의존성/버전 확인
- 순서로 접근하는 것이 중요함

## 10. 다음 구현 예정

- GET /api/files/{id} 상태/오류 조회 API
- GET /api/reports?date=YYYY-MM-DD 처리 결과 조회 API
- README 보완 및 예시 응답 추가
- CSV 헤더/BOM/인코딩 관련 보완 검토

## 11. 한 줄 요약

- 파일 업로드부터 처리 상태 관리, 실패 기록까지 포함한
- Spring Boot 기반 CSV 처리 미니 포트폴리오를 학습 및 구현 중입니다.
