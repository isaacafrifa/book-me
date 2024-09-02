package iam.bookme.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import iam.bookme.exception.ResourceNotFoundException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
//            case 404 -> new ResourceNotFoundException("User Not Found");
            default -> new Exception("Generic error");
        };
    }
}