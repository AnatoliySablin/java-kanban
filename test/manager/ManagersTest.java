package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefaultShouldReturnNotNull() {
        final TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Возвращает Null");
    }

    @Test
    void getDefaultHistoryShouldReturnNotNull() {
        final HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Возвращает Null");
    }
}