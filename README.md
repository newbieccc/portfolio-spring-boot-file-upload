# portfolio-spring-boot-file-upload

Spring Boot 기반 파일 업로드 · CSV 처리 미니 포트폴리오 프로젝트입니다.

본 프로젝트는 개인 학습 및 포트폴리오 목적의 미니 프로젝트이며, 실제 회사 데이터나 운영 환경 정보는 포함하지 않았습니다.

---

## 1. 프로젝트 소개

공공기관, 업무시스템, 사내 운영 시스템에서 자주 접할 수 있는  
**파일 업로드 → 데이터 검증 → 처리 → 처리 상태 관리 → 오류 기록** 흐름을  
작은 REST API 형태로 구현한 프로젝트입니다.

단순 파일 업로드 기능만 구현하는 것이 아니라, 운영 중인 시스템에서 중요한 다음 요소를 함께 반영하는 것을 목표로 했습니다.

- 업로드 이력 관리
- 처리 상태 관리
- 실패 시 오류 메시지 기록
- 요청 추적용 traceId
- Swagger 기반 API 실행 및 테스트
- H2 Database 기반 로컬 재현 가능 환경

---

## 2. 개발 목적

업무시스템 개발·운영, 솔루션 구축·유지보수 성격의 포지션을 준비하면서  
실무에서 자주 발생할 수 있는 파일 처리 흐름을 Spring Boot 구조로 직접 구현해보는 것을 목표로 했습니다.

주요 학습 및 구현 목표는 다음과 같습니다.

- CSV 파일 업로드
- CSV 데이터 검증
- 처리 결과 저장
- 처리 상태 및 오류 기록
- Swagger 기반 API 문서화
- 로컬 환경에서 재현 가능한 테스트 구조 구성

---

## 3. 기술 스택

- Java 21
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Boot Actuator
- springdoc-openapi
- Gradle
- Git / GitHub

> Spring Boot 버전은 실제 `build.gradle` 또는 `build.gradle.kts`에 설정된 버전과 동일하게 작성했습니다.

---

## 4. 현재 구현 범위

### 4-1. 파일 업로드 API

- `POST /api/files`
- CSV 파일 업로드
- 로컬 `./storage` 경로에 파일 저장
- `file_job` 테이블에 업로드 이력 생성
- 최초 처리 상태는 `UPLOADED`로 저장

### 4-2. CSV 처리 API

- `POST /api/files/{id}/process`
- 업로드된 CSV 파일을 읽고 검증 후 DB 저장
- 처리 상태를 아래와 같이 관리
  - `PROCESSING`
  - `SUCCESS`
  - `FAILED`

### 4-3. CSV 검증 규칙

CSV 헤더는 아래 형식을 기준으로 검증합니다.

```csv
bizDate,itemCode,qty
```

검증 규칙은 다음과 같습니다.

- `bizDate`: 날짜 형식으로 파싱 가능해야 함
- `itemCode`: 공백 불가
- `qty`: 정수이며 0 이상이어야 함

### 4-4. 처리 결과 저장

- 검증 통과 시 `processed_row` 테이블에 데이터 저장
- 재처리 시 기존 `processed_row` 데이터를 삭제 후 재적재하는 단순 정책 적용

### 4-5. 오류 처리 및 운영 관점 요소

- 공통 에러 응답 포맷 적용
  - `code`
  - `message`
  - `traceId`
  - `details`
- 전역 예외 처리기 적용
- traceId 기반 요청 추적
- 처리 실패 시 `file_job.error_message`에 실패 원인 기록

---

## 5. DB 구조

### file_job

업로드 파일 1건의 상태와 이력을 관리하는 테이블입니다.

주요 컬럼:

- `id`
- `original_file_name`
- `stored_path`
- `status`
- `error_message`
- `created_at`
- `processed_at`

### processed_row

CSV 처리 결과 데이터를 저장하는 테이블입니다.

주요 컬럼:

- `id`
- `file_job_id`
- `biz_date`
- `item_code`
- `qty`

---

## 6. 실행 방법

### 6-1. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 6-2. 접속 URL

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI Docs:

```text
http://localhost:8080/v3/api-docs
```

H2 Console:

```text
http://localhost:8080/h2-console
```

---

## 7. 테스트용 CSV 예시

### 정상 CSV

```csv
bizDate,itemCode,qty
2026-03-11,ITEM001,10
2026-03-11,ITEM002,0
```

### 실패 CSV

```csv
bizDate,itemCode,qty
2026-03-XX,ITEM001,10
```

---

## 8. 현재까지 확인한 동작

- 정상 CSV 업로드 가능
- 업로드 후 `file_job.status = UPLOADED` 저장 확인
- 처리 API 호출 후 정상 CSV는 `SUCCESS` 상태로 변경
- 잘못된 날짜 형식의 CSV는 `FAILED` 상태로 변경
- 처리 실패 원인은 `file_job.error_message`에 기록
- 처리 성공 시 `processed_row` 테이블에 CSV 데이터 저장
- Swagger UI를 통한 API 실행 가능

---

## 9. 학습 및 구현 과정에서 확인한 점

- Swagger에서 파일 업로드 UI가 보이도록 하려면 `multipart/form-data` 설정이 중요함
- Spring Boot와 springdoc-openapi는 버전 호환성 확인이 필요함
- 파일 처리 기능은 단순 저장보다 검증, 상태 관리, 실패 기록이 중요함
- 실행 오류 발생 시 증상 분리, 로그 확인, 의존성/버전 확인 순서로 접근하는 것이 효과적임
- 운영 관점에서는 실패 원인을 남기고 재현 가능한 테스트 흐름을 만드는 것이 중요함

---

## 10. 추가 개선 예정

- `GET /api/files/{id}` 상태 및 오류 조회 API 추가
- `GET /api/reports?date=YYYY-MM-DD` 처리 결과 조회 API 추가
- 예시 요청/응답 문서 보완
- CSV 헤더, BOM, 인코딩 관련 예외 처리 보완
- 단위 테스트 및 통합 테스트 추가

---

## 11. 실무 경험과의 연결점

실무에서 경험한 파일 처리 자동화, 데이터 검증, 오류 기록, 운영 안정화 흐름을 Spring Boot 기반 미니 프로젝트로 재구성했습니다.

반도체 제조 전산실에서 파일 처리 자동화와 Oracle SQL 집계 → 파일 생성 → SFTP 전송 배치 운영을 경험하며, 운영 시스템에서는 단순 기능 구현뿐 아니라 처리 상태, 실패 원인, 재처리 가능성이 중요하다는 점을 체감했습니다.

이 프로젝트에서는 해당 경험을 바탕으로 파일 업로드, 검증, 처리 상태 관리, 오류 메시지 기록을 Spring Boot REST API 구조로 구현했습니다.

---

## 12. 한 줄 요약

Spring Boot 기반으로 파일 업로드, CSV 검증, 처리 상태 관리, 오류 기록 흐름을 구현한 미니 포트폴리오 프로젝트입니다.