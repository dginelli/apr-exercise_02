package it.unimib;

import it.unimib.core.ExceptMain;
import it.unimib.failure_locator.TestCasesExecutor;
import it.unimib.generator.RepairTargetGenerator;
import it.unimib.model.FailureInfo;
import it.unimib.model.RepairTarget;

import java.util.List;
import java.util.Map;

public class App {
    public static void main (String[] args) {

        String programSourceCodePath = "/Users/davide/Development/APR/library/src/main/java";
        String programClassesPath = "/Users/davide/Development/APR/library/target/classes";
        String testsSourceCodePath = "/Users/davide/Development/APR/library/src/test/java";
        String testsClassesPath = "/Users/davide/Development/APR/library/target/test-classes";

        Map<FailureInfo, List<RepairTarget>> map =
                getFailureInfoWithRepairTargets(programSourceCodePath, programClassesPath,
                        testsSourceCodePath, testsClassesPath);

        map.forEach(((failureInfo, repairTargets) -> {
            System.out.println("Failed test case: " +
                    failureInfo.getFailingTestClass() + " " + failureInfo.getFailingTestMethod());
            repairTargets.forEach(System.out::println);
        }));

        List<RepairTarget> repairTargetList =
                getRepairTargetList(programSourceCodePath, programClassesPath, testsSourceCodePath, testsClassesPath);

        System.out.println("Repair Target objects merged with locations found with Ochiai");
        repairTargetList.forEach(System.out::println);
    }

    public static List<RepairTarget> getRepairTargetList(String programSourceCodePath, String programClassesPath,
                                                  String testsSourceCodePath, String testsClassesPath) {
         return ExceptMain.startAnalysisWithLocalizationAPI(programSourceCodePath,
                         programClassesPath, testsSourceCodePath, testsClassesPath, null);
    }

    public static Map<FailureInfo, List<RepairTarget>> getFailureInfoWithRepairTargets(String programSourceCodePath,
                                                                                String programClassesPath,
                                                                                String testsSourceCodePath,
                                                                                String testsClassesPath) {

        TestCasesExecutor testCasesExecutor = new TestCasesExecutor(programSourceCodePath, programClassesPath,
                testsSourceCodePath, testsClassesPath, null);
        List<FailureInfo> failuresInformationList = testCasesExecutor.getFailuresInformation();

        return RepairTargetGenerator.getRepairTargets(failuresInformationList, programSourceCodePath);
    }
}
