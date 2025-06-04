# Teamify

### :memo: 프로젝트 소개

* 같은 학교 학생들을 위한 스터디 및 프로젝트 팀원 모집/신청 플랫폼입니다.
* 학생들이 손쉽게 스터디나 프로젝트 팀을 구성하고 참여할 수 있도록 연결하는 것을 목표로 합니다.
### 🌏 주소
<https://teamify.today>

***

### 🔍 프로젝트 아키텍처
![Image](https://github.com/user-attachments/assets/d195f296-ed3e-4ce1-9a44-950a01d8ecfe)

*** 

### 💾 ERD 설계도
![Image](https://github.com/user-attachments/assets/fdccc88b-9804-47fe-adf7-b9791898fb15)

***

### ⚒ 기술 스택
<img src="https://img.shields.io/badge/java-fc3535?style=for-the-badge&logo=java&logoColor=white">  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">  ![JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white)  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">  <img src="https://img.shields.io/badge/h2database-09476B?style=for-the-badge&logo=h2database&logoColor=white">

![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)  ![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazon-ec2&logoColor=white)  ![Amazon RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazon-rds&logoColor=white)  <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">

***

### ✨ 주요 기능

| 기능 분류     | 기능 명칭 (액션)             | 상세 설명                                                                                                                               | 주요 사용자     |
| :---------- | :------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------- | :-------------- |
| **공고 관리** | 📝 **공고 작성**             | 스터디/프로젝트 팀원 모집을 위한 상세 내용(목표, 기간, 기술 스택 등)을<br>포함한 공고를 작성하고 게시합니다.                                                    | 모집자          |
|             | 🔍 **공고 조회 (목록/상세)**   | 게시된 모든 스터디/프로젝트 모집 공고를 목록 형태로 확인하거나,<br>특정 공고의 상세 내용을 열람합니다. (필요시 검색/필터링 기능 추가 언급)                               | 모든 사용자     |
|             | ✏️ **공고 수정**             | 자신이 게시한 모집 공고의 내용을 변경합니다.                                                                                                    | 모집자          |
|             | 🗑️ **공고 삭제**             | 자신이 게시한 모집 공고를 삭제합니다.                                                                                                    | 모집자          |
|             | ✅ **모집 마감 처리**          | 팀원 모집이 완료되었을 때, 공고를 '마감' 상태로 변경하여<br>더 이상 지원을 받지 않도록 설정합니다.                                                              | 모집자          |
| **참여/소통** | 🙋 **스터디/프로젝트 지원**    | 관심 있는 스터디/프로젝트 모집 공고에<br>참여 의사를 전달하며 지원합니다.                                                                              | 지원자          |
|             | 👍👎 **지원자 관리 (승인/거절)** | (공고 게시자) 접수된 지원자의 정보를 확인하고,<br>팀 합류 여부를 결정하여 승인 또는 거절 처리합니다.                                                                | 모집자          |
|             | ⭐ **찜하기 (관심 공고)**      | 관심 있는 모집 공고를 개인적으로 저장하여<br>추후 쉽게 다시 찾아볼 수 있도록 합니다.                                                                           | 모든 사용자     |
|             | 💬 **댓글 소통**             | 모집 공고에 대해 궁금한 점을 질문하거나 의견을 나누는 등<br>사용자들이 자유롭게 소통할 수 있는 기능을 제공합니다.                                                         | 모든 사용자     |
