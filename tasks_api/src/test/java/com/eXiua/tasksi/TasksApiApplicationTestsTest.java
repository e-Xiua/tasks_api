package com.eXiua.tasksi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;



class TasksApiApplicationTestsTest {

    @Test
    void contextLoads_doesNotThrowException() {
        TasksApiApplicationTests tests = new TasksApiApplicationTests();
        assertDoesNotThrow(tests::contextLoads, "contextLoads should not throw any exception");
    }
}