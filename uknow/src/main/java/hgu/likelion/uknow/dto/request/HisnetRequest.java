package hgu.likelion.uknow.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HisnetRequest {
    private String id;
    private String password;

    public static HisnetRequest toAdd(String id, String password) {
        return HisnetRequest.builder()
                .id(id)
                .password(password)
                .build();
    }
}
