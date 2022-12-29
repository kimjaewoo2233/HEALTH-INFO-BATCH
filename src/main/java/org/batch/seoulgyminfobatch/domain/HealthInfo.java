package org.batch.seoulgyminfobatch.domain;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class HealthInfo {
    //체력단련장업,   종류
    // AII For U GYM,   상호
    // 서울특별시 성동구 금호동4가 800 대우(아)상가 201호 , 지번주소
    // "서울특별시 성동구 독서당로 272, 도로명주소
    // 상가동 201호 (금호동4가, 금호동 대우아파트)" 자세히
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String brandName;

    private String landNumber;

    private String roadNumber;



}
