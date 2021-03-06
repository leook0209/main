package seedu.Tdoo;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import seedu.Tdoo.commons.core.Config;
import seedu.Tdoo.commons.core.EventsCenter;
import seedu.Tdoo.commons.core.LogsCenter;
import seedu.Tdoo.commons.core.Version;
import seedu.Tdoo.commons.events.storage.StorageLocationChangedEvent;
import seedu.Tdoo.commons.events.ui.ExitAppRequestEvent;
import seedu.Tdoo.commons.exceptions.DataConversionException;
import seedu.Tdoo.commons.util.ConfigUtil;
import seedu.Tdoo.commons.util.StringUtil;
import seedu.Tdoo.logic.Logic;
import seedu.Tdoo.logic.LogicManager;
import seedu.Tdoo.model.Model;
import seedu.Tdoo.model.ModelManager;
import seedu.Tdoo.model.ReadOnlyTaskList;
import seedu.Tdoo.model.TaskList;
import seedu.Tdoo.model.UserPrefs;
import seedu.Tdoo.storage.Storage;
import seedu.Tdoo.storage.StorageManager;
import seedu.Tdoo.ui.Ui;
import seedu.Tdoo.ui.UiManager;

/**
 * The main entry point to the application.
 */
public class MainApp extends Application {
	private static final Logger logger = LogsCenter.getLogger(MainApp.class);

	public static final Version VERSION = new Version(1, 0, 0, true);

	protected Ui ui;
	protected Logic logic;
	protected Storage storage;
	protected Model model;
	protected Config config;
	protected UserPrefs userPrefs;

	private Stage primaryStage;

	public MainApp() {
	}

	@Override
	public void init() throws Exception {
		logger.info("=============================[ Initializing TodoList ]===========================");
		super.init();

		config = initConfig(getApplicationParameter("config"));
		storage = new StorageManager(config);

		userPrefs = initPrefs(config);

		initLogging(config);

		model = initModelManager(storage, userPrefs);

		logic = new LogicManager(model, storage);

		ui = new UiManager(logic, config, userPrefs);

		initEventsCenter();
	}

	private String getApplicationParameter(String parameterName) {
		Map<String, String> applicationParameters = getParameters().getNamed();
		return applicationParameters.get(parameterName);
	}

	// @@author A0144061U-reused
	private Model initModelManager(Storage storage, UserPrefs userPrefs) {
		Optional<ReadOnlyTaskList> TodoListOptional;
		ReadOnlyTaskList initialTodoListData;
		Optional<ReadOnlyTaskList> EventListOptional;
		ReadOnlyTaskList initialEventListData;
		Optional<ReadOnlyTaskList> DeadlineListOptional;
		ReadOnlyTaskList initialDeadlineListData;

		try {
			TodoListOptional = storage.readTodoList();
			if (!TodoListOptional.isPresent()) {
				logger.info("Data file not found. Will be starting with an empty TodoList");
			}
			initialTodoListData = TodoListOptional.orElse(new TaskList());

			EventListOptional = storage.readEventList();
			if (!EventListOptional.isPresent()) {
				logger.info("Data file not found. Will be starting with an empty EventList");
			}
			initialEventListData = EventListOptional.orElse(new TaskList());

			DeadlineListOptional = storage.readDeadlineList();
			if (!DeadlineListOptional.isPresent()) {
				logger.info("Data file not found. Will be starting with an empty DeadlineList");
			}
			initialDeadlineListData = DeadlineListOptional.orElse(new TaskList());

		} catch (DataConversionException e) {
			logger.warning("Data file not in the correct format. Will be starting with an empty TodoList");
			initialTodoListData = new TaskList();
			initialEventListData = new TaskList();
			initialDeadlineListData = new TaskList();
		} catch (IOException e) {
			logger.warning("Problem while reading from the file. . Will be starting with an empty TodoList");
			initialTodoListData = new TaskList();
			initialEventListData = new TaskList();
			initialDeadlineListData = new TaskList();
		}

		return new ModelManager(initialTodoListData, initialEventListData, initialDeadlineListData, userPrefs);
	}
	// @@author

	private void initLogging(Config config) {
		LogsCenter.init(config);
	}

	protected Config initConfig(String configFilePath) {
		Config initializedConfig;
		String configFilePathUsed;

		configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

		if (configFilePath != null) {
			logger.info("Custom Config file specified " + configFilePath);
			configFilePathUsed = configFilePath;
		}

		logger.info("Using config file : " + configFilePathUsed);

		try {
			Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
			initializedConfig = configOptional.orElse(new Config());
		} catch (DataConversionException e) {
			logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
					+ "Using default config properties");
			initializedConfig = new Config();
		}

		// Update config file in case it was missing to begin with or there are
		// new/unused fields
		try {
			ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
		} catch (IOException e) {
			logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
		}
		return initializedConfig;
	}

	protected UserPrefs initPrefs(Config config) {
		assert config != null;

		String prefsFilePath = config.getUserPrefsFilePath();
		logger.info("Using prefs file : " + prefsFilePath);

		UserPrefs initializedPrefs;
		try {
			Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
			initializedPrefs = prefsOptional.orElse(new UserPrefs());
		} catch (DataConversionException e) {
			logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
					+ "Using default user prefs");
			initializedPrefs = new UserPrefs();
		} catch (IOException e) {
			logger.warning("Problem while reading from the file. . Will be starting with an empty TodoList");
			initializedPrefs = new UserPrefs();
		}

		// Update prefs file in case it was missing to begin with or there are
		// new/unused fields
		try {
			storage.saveUserPrefs(initializedPrefs);
		} catch (IOException e) {
			logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
		}

		return initializedPrefs;
	}

	private void initEventsCenter() {
		EventsCenter.getInstance().registerHandler(this);
	}

	@Override
	public void start(Stage primaryStage) {
		logger.info("Starting TodoList " + MainApp.VERSION);
		setPrimaryStage(primaryStage);
		ui.start(primaryStage);
	}

	@Override
	public void stop() {
		logger.info("============================ [ Stopping Todo Book ] =============================");
		ui.stop();
		try {
			storage.saveUserPrefs(userPrefs);
		} catch (IOException e) {
			logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
		}
		Platform.exit();
		System.exit(0);
	}

	private void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	@Subscribe
	public void handleExitAppRequestEvent(ExitAppRequestEvent event) {
		logger.info(LogsCenter.getEventHandlingLogMessage(event));
		this.stop();
	}

	// @@author A0144061U
	/*
	 * Stop and restart the app with new storage file
	 */
	@Subscribe
	private void handleStorageLocationChangedEvent(StorageLocationChangedEvent event) throws Exception {
		logger.info(LogsCenter.getEventHandlingLogMessage(event));

		File from;
		File to;

		from = new File(config.getTodoListFilePath());
		to = new File(event.getNewDirectory() + "/TodoList.xml");
		from.renameTo(to);

		from = new File(config.getEventListFilePath());
		to = new File(event.getNewDirectory() + "/EventList.xml");
		from.renameTo(to);

		from = new File(config.getDeadlineListFilePath());
		to = new File(event.getNewDirectory() + "/DeadlineList.xml");
		from.renameTo(to);

		String newDirectory = event.getNewDirectory();
		Config changedConfig = new Config(newDirectory);
		ConfigUtil.saveConfig(changedConfig, Config.DEFAULT_CONFIG_FILE);

		// this.primaryStage.close();

		config = initConfig(getApplicationParameter("config"));
		storage.unsubscribe();
		storage = new StorageManager(config);

		userPrefs = initPrefs(config);

		initLogging(config);

		model = initModelManager(storage, userPrefs);

		logic = new LogicManager(model, storage);

		ui = new UiManager(logic, config, userPrefs);
	}

	public static void main(String[] args) {
		launch(args);
	}
	// @@author
}