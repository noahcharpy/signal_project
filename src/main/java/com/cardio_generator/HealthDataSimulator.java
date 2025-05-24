package com.cardio_generator;

import com.alerts.*;
import com.alerts.factories.*;
import com.alerts.strategies.*;
import com.cardio_generator.generators.*;
import com.cardio_generator.outputs.*;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Singleton class that manages the full lifecycle of health data simulation.
 */
public class HealthDataSimulator {

    /** Singleton instance of the simulator. */
    private static final HealthDataSimulator INSTANCE = new HealthDataSimulator();

    /** Default number of patients. */
    private int patientCount = 50;

    private OutputStrategy outputStrategy = new ConsoleOutputStrategy();
    private ScheduledExecutorService scheduler;
    private final Random random = new Random();

    /**
     * Private constructor to prevent external instantiation.
     */
    private HealthDataSimulator() {}

    /**
     * Returns the singleton instance.
     */
    public static HealthDataSimulator getInstance() {
        return INSTANCE;
    }

    /**
     * Starts the simulation system with command-line arguments.
     */
    public void start(String[] args) throws IOException {
        parseArguments(args);
        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);

        DataStorage storage = DataStorage.getInstance();
        AlertFactory factory = new DefaultAlertFactory();
        AlertTrigger trigger = new ConsoleAlertTrigger();

        Map<String, AlertStrategy> strategies = new HashMap<>();
        strategies.put("BloodPressure", new BloodPressureStrategy());
        strategies.put("Saturation", new OxygenSaturationStrategy());
        strategies.put("ECG", new ECGStrategy());
        strategies.put("HeartRate", new HeartRateStrategy());

        AlertService alertService = new AlertService(factory, strategies, trigger);

        scheduleTasksForPatients(patientIds, storage, alertService);
    }

    /**
     * Parses CLI arguments like patient count or output target.
     */
    private void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid count. Using default: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String output = args[++i];
                        if (output.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (output.startsWith("file:")) {
                            Path path = Paths.get(output.substring(5));
                            if (!Files.exists(path)) Files.createDirectories(path);
                            outputStrategy = new FileOutputStrategy(path.toString());
                        } else if (output.startsWith("websocket:")) {
                            outputStrategy = new WebSocketOutputStrategy(Integer.parseInt(output.substring(10)));
                        } else if (output.startsWith("tcp:")) {
                            outputStrategy = new TcpOutputStrategy(Integer.parseInt(output.substring(4)));
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
            }
        }
    }

    /**
     * Creates a list of patient IDs from 1 to count.
     */
    private List<Integer> initializePatientIds(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ids.add(i);
        }
        return ids;
    }

    /**
     * Schedules the periodic tasks that simulate health data and evaluate alerts.
     */
    private void scheduleTasksForPatients(List<Integer> patientIds, DataStorage storage, AlertService alertService) {
        ECGDataGenerator ecgGen = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator satGen = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bpGen = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator blGen = new BloodLevelsDataGenerator(patientCount);
        HeartRateDataGenerator hrGen = new HeartRateDataGenerator();

        for (int id : patientIds) {
            scheduleTask(() -> ecgGen.generate(id, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> satGen.generate(id, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bpGen.generate(id, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> blGen.generate(id, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> hrGen.generate(id, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> {
                Patient p = storage.getPatient(id);
                if (p != null) alertService.evaluate(p);
            }, 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Schedules a recurring task with a slight initial delay.
     */
    private void scheduleTask(Runnable task, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, unit);
    }
}
