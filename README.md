# MoveOn
PNU CSE Student Term Project

# Preview

<details>
<summary>초기화면</summary>
  <img src="https://github.com/BaeJuneHyuck/MoveOn/blob/master/gif/init.gif"" alt="hi" class="inline"/>
</details>

<details>
<summary>장소 등록하기</summary>
 <img src="https://github.com/BaeJuneHyuck/MoveOn/blob/master/gif/locationadd.gif"" alt="hi" class="inline"/>
</details>

<details>
<summary>검색으로 길찾기</summary>
 <img src="https://github.com/BaeJuneHyuck/MoveOn/blob/master/gif/navi2.gif"" alt="hi" class="inline"/>
</details>

<details>
<summary>마커 통한 길찾기</summary>
 <img src="https://github.com/BaeJuneHyuck/MoveOn/blob/master/gif/navi2.gif"" alt="hi" class="inline"/>
</details>


# Update
-------------------------------------------------------
0518
 * api key 오류 수정 (릴리즈 버전에서도 지도 사용가능)
 * ui 변경하기
 * 로그인시 닉네임 받아오기
 * 로그인 다이얼로그 구현 (activity를 dialog로)
 * 로그인 유지하기 기능 구현(shared preference)

 버그픽스
 * 로그인이 가상머신에서 안된다
 -> 가상머신 문제가 아니라 안드로이드 파이의 문제점
 -> 매니페스트 어플리케이션 부분에 android:usesCleartextTraffic="true"추가

-------------------------------------------------------
0519
 1) 로그인 성공시, 로그아웃시 
    메뉴의 text가 변경
    눌럿을시 로그인 유무로 동작결정하기 

 2) 검색기능 사용을위해 검색창 키보드에 버튼(확인) 추가
  ->이후 건물 DB를 서버에서 받아오고
  -> 원하는 내용을 찾아서
  -> 지도에 표시하기

 3) 위치 공유
  다른앱으로 지금위치를 텍슽로 만들어서 공우하기

 버그픽스
 *  apk 릴리즈시 지도가 안보인다
 -> 개인 key를 이용해 apk 만들면
 SHA1 이 달라진다. api key 관리에서 새로 추가해줘야함

-------------------------------------------------------
0520
 * 필요없는 액티비티 제거
-------------------------------------------------------
0521
 * 위치 등록기능 추가
-------------------------------------------------------
0525
 * 자동로그인 수정
-------------------------------------------------------
0602
 * 필터 기능 추가
 * 현재위치 5초마다 확인해서 업데이트 되도록 변경함
 * 일부 변수명 수정
-------------------------------------------------------
0620 최종발표
 * 필터 기능 개선, 다이얼로그 사용하여 체크박스 사용해도 닫히지 않음
 * 길찾기 기능 추가(마커/검색 통해서 사용가능, 부산대 내부만 제대로 작동함)
 * 건물 타입별 마커 그림 추가
 * 지도에 롱터치 리스너 추가하여 현재위치 아닌곳도 장소 추가 가능
 * 장소 추가시 즉시 확인가능
 ------------------------------------------------------
