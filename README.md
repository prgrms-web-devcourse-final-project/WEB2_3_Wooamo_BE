## Info
4차 프로젝트 2팀 "우리는 아무것도 모른다"의 스터디 관리 플랫폼 STUV 입니다.

## Commit Message Convention
| Tag | Description |
| --- | --- |
| init | 프로젝트 시작에 대한 커밋 |
| fix | 버그를 수정한 경우 |
| feature | 새로운 기능을 추가한 경우 |
| docs | 문서 추가, 수정 삭제 |
| test | 테스트 코드 추가, 수정, 삭제 |
| style | 코드 형식 변경 |
| refactor | 코드 리팩토링 |
| perf | 성능개선 |
| build | 빌드 관련 변경사항 |
| ci | ci 관련 설정 수정 |
| chore | 기타 변경 사항 |
```
ex)
feat: About the team 기능 추가
- ~을 변경
- ~을 (이유) 변경
```

## Branch Naming Convention
- [tag]/domain/#number-기능
```
ex)
feature/user/#1-social-login
```

## 패키지 구조
```
domain
⎿ domain1
  ⎿ controller
  ⎿ dto
  ⎿ entity
  ⎿ exception
  ⎿ repository
  ⎿ service
		
⎿ domain2
  ⎿ controller
  ⎿ dto
  ⎿ entity
  ⎿ exception
  ⎿ repository
  ⎿ service

global
⎿ config
⎿ ...
```
