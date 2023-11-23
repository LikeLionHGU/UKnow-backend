package hgu.likelion.uknow.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberRequest {
    private String id;
    private String session;
}
