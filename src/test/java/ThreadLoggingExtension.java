import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit extension to log the current thread name before each test method.
 */
public class ThreadLoggingExtension implements BeforeEachCallback {

    /**
     * This method is called before each test method is executed.
     * It logs the name of the current thread to the console.
     *
     * @param context the context for the extension, which provides information about the test method being executed
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        System.out.println("Running test on thread: " + Thread.currentThread().getName());
    }
}