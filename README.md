# CallVault - 자동 통화 녹음 앱

## 비기능 요구사항

- **성능**: 녹음 시작/종료 지연 1초 이내, 녹음 목록 1000건 이상도 2초 내 표시
- **신뢰성**: 녹음 실패율 1% 미만, 파일 손상 시 자동 복구/알림
- **보안**: 파일 암호화(선택), 앱 잠금, 외부 앱 접근 차단
- **호환성**: Android 8~최신 버전, 다양한 제조사 기기 테스트
- **유지보수성**: ViewModel-Repository-UI 분리, 테스트 코드 작성
- **가용성**: 녹음 서비스 99% 이상 동작, 예외 발생 시 사용자 안내
- **사용성**: 3클릭 이내 주요 기능 접근, 직관적 아이콘/텍스트, 다크모드 지원

## 테스트 전략

- **단위 테스트**: Repository, ViewModel, 파일 입출력 등 핵심 로직 단위 테스트
- **UI 테스트**: Compose UI 주요 화면(목록, 상세, 설정, 권한 안내 등) 테스트
- **통합 테스트**: 녹음-저장-목록-재생-삭제 전체 플로우 테스트
- **권한/에러 처리**: 권한 미허용, 저장소 부족, 녹음 실패 등 예외 상황 테스트

## 주요 구조
- ViewModel-Repository-UI(Compose) 분리
- 포그라운드 서비스+알림
- 녹음 파일 관리(목록, 삭제, 공유, 재생, 상세)
- 설정/권한 안내/상세 등 최신 UX 적용