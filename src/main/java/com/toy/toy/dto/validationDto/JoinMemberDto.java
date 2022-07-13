package com.toy.toy.dto.validationDto;


import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class JoinMemberDto implements ValidationDto {

    @NotBlank
    @Size(min = 2 , max = 4)
    private String username;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(min = 8 , max = 20)
    private String userId;


    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$")
    @Size(min = 8,max = 16)
    private String password;

    private String password2;

    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")
    @NotBlank
    private String email;

    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")
    @NotBlank
    private String tel;
    @NotNull
    @AssertTrue
    private Boolean isIdCheck;



    //JoinMemberDto -> Member
    public Member changeEntity(){
        return Member.builder()
                .username(this.username)
                .userId(this.userId)
                .password(this.password)
                .email(Objects.requireNonNullElse(this.email,"등록 안함"))
                .tel(Objects.requireNonNullElse(this.tel,"등록 안함"))
                .memberGrade(MemberGrade.NORMAL)
                .build();
    }



}
