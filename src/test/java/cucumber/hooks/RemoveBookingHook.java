package cucumber.hooks;

import cucumber.context.TestContext;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.After;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class RemoveBookingHook implements BeanFactoryAware {


    private final TestContext testContext;
    private final BookingRepository bookingRepository;
    private final CacheManager cacheManager;

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
    }

    @After(value = "@RemoveBookings", order = 100)
    public void rollbackTransaction() {
        log.info("Removing created bookings...");

        testContext.getBookingsToDelete().stream()
                        .map(bookingRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(booking -> {
                            log.info("Deleting booking from database{}", booking.getBookingId());
                            bookingRepository.deleteById(booking.getBookingId());
                        });
        // Clear the cache if caching is used in the code notably the Repository
        cacheManager.getCacheNames()
                    .forEach(cacheName -> Optional.ofNullable(cacheManager.getCache(cacheName))
                            .ifPresent(Cache::clear));
    }

}
