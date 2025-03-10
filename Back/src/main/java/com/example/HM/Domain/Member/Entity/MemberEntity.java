package com.example.HM.Domain.Member.Entity;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    public static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setEmail(memberDTO.getEmail());
        memberEntity.setPassword(new BCryptPasswordEncoder().encode(memberDTO.getPassword()));

        // ✅ 이름이 null이면 기본값 설정
        if (memberDTO.getName() == null || memberDTO.getName().trim().isEmpty()) {
            memberEntity.setName("사용자"); // 기본값 설정 (예: "사용자")
        } else {
            memberEntity.setName(memberDTO.getName());
        }

        return memberEntity;
    }
}
