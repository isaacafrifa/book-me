package iam.bookme.client;

import iam.bookme.dto.UserDto;
import iam.bookme.dto.UserRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${user-feign.name}", url = "${user-feign.url}", configuration = FeignConfiguration.class)
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/users/email", consumes = "application/json")
    UserDto getUserFromUserServiceByEmail(@RequestParam String userEmail);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/users")
    UserDto createUserInUserService(UserRequestDto userRequestDto);
}
