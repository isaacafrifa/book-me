package iam.bookme.client;

import iam.bookme.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "${user-feign.name}", url = "${user-feign.url}", configuration = FeignConfiguration.class)
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/users/{userEmail}", consumes = "application/json")
    UserDto getUserByEmail(@PathVariable String userEmail);
}
