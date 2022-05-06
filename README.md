# JWT-Authorization

### 개요
회원가입 및 회원인증을 위한 JWT Token 발급 서비스 입니다.

### 명세

| URI                           | CRUD | 동작             |
|-------------------------------|------|----------------|
| /api/member/join              | POST | 회원가입           |
| /api/member/login             | POST  | 로그인 성공 시 토큰 반환 |



<details>
<summary><strong>[POST] /api/member/join 상세</strong></summary>

### Params
NOT USE

### Request-Header

NOT USE

### Request-Body

| Name       | Type | Description |
|------------| ---- |-------------|
| `name`     | `String` | 회원 이름       |
| `password` | `String` | 회원 비밀번호     |


```
{
    "name" : "kim",
    "password" : "q1w2e3",
}
```

### Response-Body

| Name         | Type   | Description |
|--------------|--------|-------------|
| `_id`        | `Long` | 회원 번호       |


#### 성공

> HTTP status codes 200(OK)


</details>

<br/>

<details>
<summary><strong>[POST] /api/member/login 상세</strong></summary>

### Params
NOT USE

### Request-Header

NOT USE

### Request-Body

| Name       | Type | Description |
|------------| ---- |-------------|
| `name`     | `String` | 회원 이름       |
| `password` | `String` | 회원 비밀번호     |

```
{
    "name" : "kim",
    "password" : "q1w2e3",
}
```

### Response-Body

| Name        | Type     | Description |
|-------------|----------|-------------|
| `JWT Token` | `String` | 회원 번호       |

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJraW0iLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNjUxNzM2NTI0LCJleHAiOjE2NTE3MzgzMjR9.MA_joQvYSe_aeEoJm-6P93HpiThjvT9k7z3sOJnVzWw
```

#### 성공

> HTTP status codes 200(OK)

#### 실패

> 권한 인증 오류:
> HTTP status codes 401(Unauthorized)

</details>
