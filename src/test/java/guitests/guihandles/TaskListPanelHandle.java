package guitests.guihandles;

import guitests.GuiRobot;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import seedu.Tdoo.TestApp;
import seedu.Tdoo.commons.core.LogsCenter;
import seedu.Tdoo.model.task.ReadOnlyTask;
import seedu.Tdoo.model.task.Todo;
import seedu.Tdoo.testutil.TestUtil;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Provides a handle for the panel containing the task list.
 */
// @@author A0132157M reused
public class TaskListPanelHandle extends GuiHandle {

	public static final int NOT_FOUND = -1;
	public static final String CARD_PANE_ID = "#name";

	private static final String task_LIST_VIEW_ID = "#todoListView";

	public TaskListPanelHandle(GuiRobot guiRobot, Stage primaryStage) {
		super(guiRobot, primaryStage, TestApp.APP_TITLE);
	}

	public List<ReadOnlyTask> getSelectedtasks() {
		ListView<ReadOnlyTask> taskList = getListView();
		return taskList.getSelectionModel().getSelectedItems();
	}

	public ListView<ReadOnlyTask> getListView() {
		return (ListView<ReadOnlyTask>) getNode(task_LIST_VIEW_ID);

	}

	/**
	 * Returns true if the list is showing the task details correctly and in
	 * correct order.
	 * 
	 * @param tasks
	 *            A list of task in the correct order.
	 */
	public boolean isListMatching(ReadOnlyTask... tasks) {
		return this.isListMatching(0, tasks); // something wrong, always return
												// false!!!
	}

	/**
	 * Clicks on the ListView.
	 */
	public void clickOnListView() {
		Point2D point = TestUtil.getScreenMidPoint(getListView());
		guiRobot.clickOn(point.getX(), point.getY());
	}

	/**
	 * Returns true if the {@code tasks} appear as the sub list (in that order)
	 * at position {@code startPosition}.
	 */
	public boolean containsInOrder(int startPosition, ReadOnlyTask... tasks) {
		List<ReadOnlyTask> tasksInList = getListView().getItems();

		// Return false if the list in panel is too short to contain the given
		// list
		if (startPosition + tasks.length > tasksInList.size()) {
			return false;
		}

		// Return false if any of the tasks doesn't match
		for (int i = 0; i < tasks.length; i++) {
			if (!tasksInList.get(startPosition + i).getName().name.equals(tasks[i].getName().name)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns true if the list is showing the task details correctly and in
	 * correct order.
	 * 
	 * @param startPosition
	 *            The starting position of the sub list.
	 * @param tasks
	 *            A list of task in the correct order.
	 */
	public boolean isListMatching(int startPosition, ReadOnlyTask... tasks) throws IllegalArgumentException {
		if (tasks.length + startPosition != getListView().getItems().size()) {
			throw new IllegalArgumentException("List size mismatched\n" + "Expected " + tasks.length + " "
					+ (getListView().getItems().size()) + " tasks");
		}
		assertTrue(this.containsInOrder(startPosition, tasks));
		for (int i = 0; i < tasks.length; i++) {
			final int scrollTo = i + startPosition;
			guiRobot.interact(() -> getListView().scrollTo(scrollTo));
			guiRobot.sleep(200);
			if (!TestUtil.compareCardAndTask(getTaskCardHandle(startPosition + i), tasks[i])) {
				return false;
			}
		}
		return true;
	}

	// @@author A0132157M
	public TaskCardHandle navigateTotask(String name) {
		LogsCenter.getLogger(TaskListPanelHandle.class).info("task.length add command: " + name.toString());

		guiRobot.sleep(500); // Allow a bit of time for the list to be updated
		final Optional<ReadOnlyTask> task = getListView().getItems().stream().filter(p -> p.getName().name.equals(name))
				.findAny();
		LogsCenter.getLogger(TaskListPanelHandle.class).info("task: " + task.toString());

		if (!task.isPresent()) {
			throw new IllegalStateException("Todo not found: " + name);
		}

		return navigateTotask(task.get());
	}

	/**
	 * Navigates the listview to display and select the task.
	 */
	public TaskCardHandle navigateTotask(ReadOnlyTask task) {
		int index = gettaskIndex(task); // SOmething wrong. Always return 0

		guiRobot.interact(() -> {
			getListView().scrollTo(index);
			guiRobot.sleep(150);
			getListView().getSelectionModel().select(index);
		});
		guiRobot.sleep(100);
		return getTaskCardHandle(task);
	}

	/**
	 * Returns the position of the task given, {@code NOT_FOUND} if not found in
	 * the list.
	 */
	public int gettaskIndex(ReadOnlyTask targettask) {
		List<ReadOnlyTask> tasksInList = getListView().getItems();
		for (int i = 0; i < tasksInList.size(); i++) {
			if (tasksInList.get(i).getName().equals(targettask.getName())) {
				return i;
			}
		}
		return NOT_FOUND;
	}

	/**
	 * Gets a task from the list by index
	 */
	public ReadOnlyTask gettask(int index) {
		return getListView().getItems().get(index);
	}

	public TaskCardHandle getTaskCardHandle(int task) {
		return getTaskCardHandle(new Todo(getListView().getItems().get(task)));
	}

	public TaskCardHandle getTaskCardHandle(ReadOnlyTask task) {
		Set<Node> nodes = getAllCardNodes();
		Optional<Node> taskCardNode = nodes.stream()
				.filter(n -> new TaskCardHandle(guiRobot, primaryStage, n).isSametask(task)).findFirst();
		if (taskCardNode.isPresent()) {
			return new TaskCardHandle(guiRobot, primaryStage, taskCardNode.get());
		} else {
			return null;
		}
	}

	protected Set<Node> getAllCardNodes() {
		return guiRobot.lookup(CARD_PANE_ID).queryAll();
	}

	public int getNumberOfTasks() {
		return getListView().getItems().size();
	}
}
