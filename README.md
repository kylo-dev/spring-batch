# Comento - 백엔드 직무 부트 캠프

## 1️⃣ 데이터 마이그레이션 요구사항

- **비용 효율화** : 급속도로 늘어나고 있는 데이터를 별도의 데이터베이스에 두어 비용 효율화
- **성능** : 마이그레이션 이후, 성능에 Degradation이 없어야 함
- **비즈니스** : 마케팅, 추후 데이터 관리 측면에서 효율적이어야 함
- **장애 대응** : 채팅 데이터로 인한 데이터베이스 장애 발생 시 전체 영향이 가지 않아야 함

### 고려 사항별 분석

- 비용 효율화
    - **chat_room, chat_join, chat_like 테이블은 데이터 증가 속도가 높지 않아 고려 대상에서 제외**
- 성능
    - 채팅 메시지를 전달할 때 채팅의 좋아요 유무를 같이 보내줘야 함
    - chat, chat_like 테이블이 분리되면 메시지 별로 like 유무 검사가 필요해 성능 저하가 우려가 있음
    - **성능 이점을 위해 chat, chat_like 테이블은 동일한 데이터베이스에서 관리**
- 비즈니스
    - chat_room 데이터는 추후 비즈니스에 활용될 수 있음 (채팅방 홍보)
    - chat_join 데이터를 활용해 유저가 관심 있는 채팅방의 정보를 수집할 수 있음 (채팅방 추천)
    - **chat_room, chat_join은 비즈니스에 지속적으로 필요하므로 기존 데이터베이스에서 관리**
- 장애 대응
    - 메시지 트래픽이 급속도로 증가해 장애가 발생해 채팅 메시지를 주고받을 수 없는 경우에도 다른 기능은 정상적으로 동작해야 함
    - **비즈니스에 중요한 데이터 chat_room, chat_join, user는 chat과 다른 데이터로 분리해서 채팅 데이터베이스 장애로 인한 영향 최소화**

## 2️⃣ 예상되는 우려점

- 성능
    - chat_room 테이블에서 Join 없이 공지 Chat을 조회해야 함
        - chat의 PK를 이용해서 가져오므로 큰 부하가 없을 것이라 예상
    - chat 메시지 리스트를 전달할 때 매번 sender 정보를 조회해야 함
        - user의 pk를 이용해서 가져오는 것이 때문에 큰 부하가 없을 것이라 예상
     
## 3️⃣ 데이터베이스 테이블 분리

<img width="633" alt="image" src="https://github.com/user-attachments/assets/2f31bb82-9a25-4a42-9e02-9862f1e585cc">

### 채팅 서비스 테이블 관계 분석

**`1. User - ContentLike - Content`**

(Content를 제목과 내용이 있는 게시글로 분석하였습니다.)

- 한 User는 여러 개의 Content를 작성할 수 있다. ***(1:N)***
    - 하나의 Content는 한 User가 작성한 것이다. ***(N:1)***
- 한 User는 여러 개의 Content에 Like(좋아요) 할 수 있다. ***(M:N)***
    - 하나의 Content에 여러 User가 Like(좋아요)를 누를 수 있다. ***(M:N)***
    - ContentLike를 통해 일대다 - 다대일 관계를 분리하였음

**`2. User - ChatLike - Chat`**

- 한 User는 여러 개의 Chat을 작성할 수 있다. ***(1:N)***
    - 하나의 Chat은 한 User가 작성한 것이다. ***(N:1)***
- 한 User는 여러 개의 Chat에 Like(좋아요)를 할 수 있다. ***(M:N)***
    - 한 Chat에 여러 User가 Like(좋아요)를 누를 수 있다. ***(M:N)***

**`3. User - ChatJoin - ChatRoom`**

- 한 User는 여러 ChatRoom에 Join 할 수 있다. ***(M:N)***
    - 하나의 ChatRoom에 여러 User가 Join 될 수 있다. ***(M:N)***
    - ContentJoin를 통해 일대다 - 다대일 관계를 분리하였음

**`4. Chat - ChatRoom`  *🌟 2 개의 관계가 있음 🌟***

- 여러 Chat은 한 ChatRoom에 작성된다. ***(N:1)***
    - 한 ChatRoom에 여러 Chat이 작성된다. ***(1:N)***
- 하나의 Chat은 한 ChatRoom의 공지 글이 될 수 있다. ***(1:1)***

📌 Chat과 ChatRoom은 서로 Fk를 가지고 있으며, 2 개의 관계를 처리합니다.

## 4️⃣ Spring Batch를 통한 데이터베이스 마이그레이션

_**블로그 포스팅**_

[Spring Batch 이해하기](https://kylo8.tistory.com/entry/Spring-Spring-Batch-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-Job-Step-Chunk)

[데이터 마이그레이션 트러블 슈팅](https://kylo8.tistory.com/entry/Spring-Spring-Batch-50-JobBuilder-StepBuilder-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%A7%88%EC%9D%B4%EA%B7%B8%EB%A0%88%EC%9D%B4%EC%85%98-ID-%EC%B6%A9%EB%8F%8C-%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85)

<details>
  <summary>Yaml 파일</summary>
  ```yaml
spring:
  batch:
    jdbc:
      initialize-schema: always
    job:
      name: chatJob
  datasource:
    chat-db: # 이관할 데이터베이스
      jdbc-url: jdbc:postgresql://localhost:5432/comento
      username: admin
      password: qwer1234
      driver-class-name: org.postgresql.Driver
      hibernate: update
      hikari:
        connection-timeout: 3000
        validation-timeout: 3000
        minimum-idle: 5
        max-lifetime: 240000
        maximum-pool-size: 20
    core-db: # 기존 데이터베이스
      jdbc-url: jdbc:postgresql://****.amazonaws.com:5432/****
      username: ****
      password: ****
      driver-class-name: org.postgresql.Driver
      hibernate: none
      hikari:
        connection-timeout: 3000
        validation-timeout: 3000
        minimum-idle: 5
        max-lifetime: 240000
        maximum-pool-size: 20
  jpa:
    database: postgresql
    generate-ddl: false
    open-in-view: false
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        enable_lazy_load_no_trans: true
        format_sql: true
```
</details>
