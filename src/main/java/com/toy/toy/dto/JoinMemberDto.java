package com.toy.toy.dto;


import com.toy.toy.entity.Member;
import com.toy.toy.entity.MemberGrade;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class JoinMemberDto {


    @NotBlank
    @Size(min = 2 , max = 4)
    private String username;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{8,20}")
    private String userId;

    @Pattern(regexp = "\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])[-]*[1-4]\\d{6}")
    private String ssn;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$")
    private String password;

    private String password2;

    @Email
    private String email;

    private String tel;

    private LocalDateTime createdDate;


    //JoinMemberDto -> Member
    public Member changeEntity(){
        return Member.builder()
                .username(this.username)
                .userId(this.userId)
                .ssn(this.ssn)
                .password(this.password)
                .email(this.email)
                .tel(this.tel)
                .memberGrade(MemberGrade.NORMAL)
                .build();
    }



}
