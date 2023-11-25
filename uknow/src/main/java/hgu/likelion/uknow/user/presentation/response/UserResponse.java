package hgu.likelion.uknow.user.presentation.response;

import hgu.likelion.uknow.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private String id;

    private String name;

    private String session;

    private String token;

    public static UserResponse toResponse(String id, String session, String name, String token) {
        return UserResponse.builder()
                .id(id)
                .name(name)
                .token(token)
                .session(session)
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getStudentId())
                .name(user.getName())
                .build();
    }

}
