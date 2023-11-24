package hgu.likelion.uknow.user.application.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJwtDto {
    private String token;

    private String studentId;

    private String name;

}