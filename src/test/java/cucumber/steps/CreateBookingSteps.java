package cucumber.steps;

import cucumber.context.TestContext;
import cucumber.context.CucumberService;
import iam.bookme.dto.BookingDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CreateBookingSteps {

    private final CucumberService cucumberService;
    private final TestContext testContext;
    private static final Logger log = LoggerFactory.getLogger(CreateBookingSteps.class);

    @Given("a request with the following fields is to be posted")
    public void aRequestWithTheFollowingFieldsIsToBePosted(List<List<String>> items) throws Exception{
        log.info("Create booking request with the ff fields \n{}", items);
        Map<String, Object> bookingPayload = createBookingPayload(convertToMap(items));
        testContext.setCreateBookingRequestPayload(bookingPayload);
    }

    @Transactional
    @When("the endpoint {string} is called to post a booking")
    public void theEndpointIsCalledToPostABooking(String endpoint) {
        createBooking(endpoint);
    }


    private Map<String, Object> createBookingPayload(Map<String, Object> fieldsAndValues) throws Exception {
        Map<String, Object> payload = cucumberService.getBookingRequestDto(null);
        payload.putAll(fieldsAndValues);
        return payload;
    }

    private Map<String, Object> convertToMap(List<List<String>> values) {
        final Map<String, Object> map = new HashMap<>(values.size());
        for (List<String> field : values) {
            String fieldName = field.get(0).trim();
            String value = processValue(field.get(1));
            String[] path = fieldName.split("\\.");
            setValue(map, path, value);
        }
        return map;
    }

    private String processValue(String value) {
        if (value == null) return null;
        value = value.trim();
        return switch (value) {
            case "null" -> null;
            case "empty" -> "";
            default -> value;
        };
    }

    private void setValue(Map<String, Object> map, String[] path, String value) {
        Map<String, Object> current = map;
        for (int i = 0; i < path.length; i++) {
            String field = path[i];
            if (field.contains("]")) {
                handleArrayField(current, field, value, i == path.length - 1);
            } else {
                handleSimpleField(current, field, value, i == path.length - 1);
            }
            if (i < path.length - 1) {
                current = (Map<String, Object>) current.get(field.split("\\[")[0]);
            }
        }
    }

    private void handleArrayField(Map<String, Object> current, String field, String value, boolean isLastField) {
        int index = Integer.parseInt(field.split("\\[")[1].split("]")[0]);
        String fieldName = field.split("\\[")[0];
        List<Object> array = (List<Object>) current.computeIfAbsent(fieldName, k -> new ArrayList<>());

        while (array.size() <= index) {
            array.add(new HashMap<String, Object>());
        }

        if (isLastField) {
            array.set(index, value);
        }
    }

    private void handleSimpleField(Map<String, Object> current, String field, String value, boolean isLastField) {
        if (isLastField) {
            current.put(field, value);
        } else {
            current.computeIfAbsent(field, k -> new HashMap<String, Object>());
        }
    }


    public void createBooking(String endpoint){
        cucumberService.doAPIObjectCall(endpoint, HttpMethod.POST, BookingDto.class, testContext.getCreateBookingRequestPayload(),
                null);
        if (testContext.getHttpResponse()!= null && HttpStatus.CREATED.equals(testContext.getHttpResponse().getStatusCode())
        && testContext.getHttpResponse().getBody() instanceof BookingDto dto){
            testContext.getBookingsToDelete().add(dto.getBookingId());
            testContext.setActiveBookingId(dto.getBookingId());
            testContext.setBookingDto(dto);
        }
        else {
            testContext.setActiveBookingId(0L);
        }
    }

}
