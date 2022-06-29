package it.unimib;

import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import it.unimib.core.ExceptMain;
import it.unimib.failure_locator.TestCasesExecutor;
import it.unimib.generator.RepairTargetGenerator;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;

import java.util.List;
import java.util.Map;

public class App {

    private static final String PROGRAM_SOURCE_CODE_PATH = "/Users/davide/Development/APR/library/src/main/java";
    private static final String PROGRAM_CLASSES_PATH = "/Users/davide/Development/APR/library/target/classes";
    private static final String TEST_SOURCE_CODE_PATH = "/Users/davide/Development/APR/library/src/test/java";
    private static final String TEST_CLASSES_PATH = "/Users/davide/Development/APR/library/target/test-classes";
    private static final String PROJECT_PATH = "/Users/davide/Development/APR/library/";

    public static void main (String[] args) {
        getRepairTargetList();
        getFailureInfoWithRepairTargets();
        getSuspiciousLocationsWithFlacoco();
    }

    public static void getRepairTargetList() {
        List<RepairTarget> repairTargetList =
                ExceptMain.startAnalysisWithLocalizationAPI(PROGRAM_SOURCE_CODE_PATH,
                         PROGRAM_CLASSES_PATH, TEST_SOURCE_CODE_PATH, TEST_CLASSES_PATH, null);

        System.out.println("Repair Target objects merged with locations found with Ochiai");
        repairTargetList.forEach(System.out::println);
    }

    public static void getFailureInfoWithRepairTargets() {

        TestCasesExecutor testCasesExecutor = new TestCasesExecutor(PROGRAM_SOURCE_CODE_PATH,
                PROGRAM_CLASSES_PATH, TEST_SOURCE_CODE_PATH, TEST_CLASSES_PATH, null);
        List<FailureInfo> failuresInformationList = testCasesExecutor.getFailuresInformation();

        Map<FailureInfo, List<RepairTarget>> map =
                RepairTargetGenerator.getRepairTargets(failuresInformationList, PROGRAM_SOURCE_CODE_PATH);

        map.forEach(((failureInfo, repairTargets) -> {
            System.out.println("Failed test case: " +
                    failureInfo.getFailingTestClass() + " " + failureInfo.getFailingTestMethod());
            repairTargets.forEach(System.out::println);
        }));
    }

    public static void getSuspiciousLocationsWithFlacoco() {
        FlacocoConfig config = new FlacocoConfig();
        config.setProjectPath(PROJECT_PATH);
        config.setComputeSpoonResults(true);

        Flacoco flacoco = new Flacoco(config);
        FlacocoResult result = flacoco.run();

        result.getFailingTests().forEach(testMethod -> {
            System.out.println("Test class: " + testMethod.getFullyQualifiedClassName() +
                    ", Test method: " + testMethod.getFullyQualifiedMethodName());
        });

        result.getDefaultSuspiciousnessMap().forEach(((location, suspiciousness) -> {
            System.out.println("Class name: " + location.getClassName() +
                    ", Line number: " + location.getLineNumber() +
                    ", Suspiciousness score: " + suspiciousness.getScore());
        }));
    }
}
