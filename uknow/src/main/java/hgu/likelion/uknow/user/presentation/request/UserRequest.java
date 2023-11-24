package hgu.likelion.uknow.user.presentation.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequest {
    private String id;
    private String session;
}
