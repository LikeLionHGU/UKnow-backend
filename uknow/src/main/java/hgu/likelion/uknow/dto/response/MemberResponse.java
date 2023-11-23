package hgu.likelion.uknow.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberResponse {
    private String id;
    private String session;

    public static MemberResponse toResponse(String id, String session) {
        return MemberResponse.builder()
                .id(id)
                .session(session)
                .build();
    }

}
