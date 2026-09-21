package util;

public class TestResult {
    String name;
    String status;
    long duration;
    String error;
    String screenshot;

    public TestResult(String name, String status, long duration, String error, String screenshot) {
        this.name = name;
        this.status = status;
        this.duration = duration;
        this.error = error;
        this.screenshot = screenshot;
    }
}
