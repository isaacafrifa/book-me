package iam.bookme.client;

import iam.bookme.dto.UserDto;
import iam.bookme.dto.UserRequestDto;
import iam.bookme.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${user-feign.name}", url = "${user-feign.url}", configuration = FeignConfiguration.class)
public interface UserClient {

    Logger LOG = LoggerFactory.getLogger(UserClient.class);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/users/email", consumes = "application/json")
    @CircuitBreaker(name = "user_client", fallbackMethod = "userServiceFallback")
    @Retry(name = "user_client")
    UserDto getUserFromUserServiceByEmail(@RequestParam String userEmail);

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/users")
    @CircuitBreaker(name = "user_client", fallbackMethod = "userServiceFallback")
    @Retry(name = "user_client")
    UserDto createUserInUserService(UserRequestDto userRequestDto);

    default UserDto userServiceFallback(Throwable throwable) {
        LOG.warn("User Service is currently unavailable due to: {}", throwable.getMessage());
        throw new ServiceUnavailableException("User service is currently unavailable");
    }
}
