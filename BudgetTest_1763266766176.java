package net.mooctest;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class BudgetTest {

    @Test
    public void testBudgetItemNormalizationAndTotals() {
        Budget budget = new Budget();
        Budget.Item normalized = new Budget.Item(null, -5, -3, null);
        assertEquals("", normalized.getName());
        assertEquals(0, normalized.getCost(), 0);
        assertEquals(0, normalized.getValue(), 0);
        assertEquals("GENERAL", normalized.getCategory());

        budget.add(normalized);
        budget.add(new Budget.Item("Microscope", 1500, 2.3, "EQUIPMENT"));
        budget.add(null);

        assertEquals(1500, budget.totalCost(), 1e-9);
        assertEquals(2.3, budget.totalValue(), 1e-9);
    }

    @Test
    public void testBudgetReserveAndForecastClamping() {
        Budget budget = new Budget();
        budget.add(new Budget.Item("Server", 400, 1, "IT"));
        budget.add(new Budget.Item("Storage", 2000, 1, "IT"));

        budget.setReserveRatio(0.8);
        assertEquals(budget.totalCost() * 0.5, budget.requiredReserve(), 1e-9);

        budget.setReserveRatio(-0.3);
        assertEquals(1000, budget.requiredReserve(), 0);
        assertEquals(budget.totalCost() * 0.5, budget.forecastCost(-1.0), 1e-9);
        assertEquals(budget.totalCost() * 2, budget.forecastCost(5.0), 1e-9);
    }

    @Test
    public void testBudgetForecastWithinRangeAndThresholdReserve() {
        Budget budget = new Budget();
        budget.add(new Budget.Item("Sensor", 1000, 0.5, "HW"));

        assertEquals(1250, budget.forecastCost(0.25), 1e-9);
        assertEquals(600, budget.forecastCost(-0.4), 1e-9);

        budget.setReserveRatio(0.4);
        assertEquals(1000, budget.requiredReserve(), 0);
        budget.setReserveRatio(0.3);
        assertEquals(1000, budget.requiredReserve(), 0);
    }

    @Test
    public void testBudgetOptimizerSelectsHighestValueItem() {
        Budget budget = new Budget();
        Budget.Item a = new Budget.Item("A", 3, 5, "X");
        Budget.Item b = new Budget.Item("B", 2, 3, "X");
        Budget.Item c = new Budget.Item("C", 4, 10, "X");
        budget.add(a);
        budget.add(b);
        budget.add(c);

        BudgetOptimizer optimizer = new BudgetOptimizer();
        BudgetOptimizer.Selection selection = optimizer.optimize(budget, 5);
        assertEquals(1, selection.getItems().size());
        assertEquals("C", selection.getItems().get(0).getName());
        assertEquals(c.getValue(), selection.getTotalValue(), 1e-9);
        assertEquals(c.getCost(), selection.getTotalCost(), 1e-9);
    }

    @Test
    public void testBudgetOptimizerHandlesZeroLimit() {
        Budget budget = new Budget();
        budget.add(new Budget.Item("A", 3, 5, "X"));
        BudgetOptimizer.Selection zero = new BudgetOptimizer().optimize(budget, -1);
        assertTrue(zero.getItems().isEmpty());
        assertEquals(0, zero.getTotalCost(), 1e-9);
        assertEquals(0, zero.getTotalValue(), 1e-9);
    }

    @Test
    public void testBudgetOptimizerRoundingOfLimitAndCosts() {
        Budget budget = new Budget();
        budget.add(new Budget.Item("FracLow", 1.4, 10, "X"));
        budget.add(new Budget.Item("FracHigh", 1.6, 1, "X"));

        BudgetOptimizer optimizer = new BudgetOptimizer();
        BudgetOptimizer.Selection selection = optimizer.optimize(budget, 1.6);
        assertEquals(1, selection.getItems().size());
        assertEquals("FracLow", selection.getItems().get(0).getName());

        BudgetOptimizer.Selection selectionRounded = optimizer.optimize(budget, 1.4);
        assertEquals("FracLow", selectionRounded.getItems().get(0).getName());
    }

    @Test(expected = DomainException.class)
    public void testBudgetOptimizerNullBudgetThrows() {
        new BudgetOptimizer().optimize(null, 1);
    }

    @Test
    public void testGraphUtilsTopologicalSortAndLongestPathReflectImplementation() {
        Task design = new Task("Design", 3, Task.Priority.HIGH);
        Task build = new Task("Build", 4, Task.Priority.MEDIUM);
        Task test = new Task("Test", 2, Task.Priority.MEDIUM);
        assertTrue(build.addDependency(design));
        assertTrue(test.addDependency(build));

        List<Task> order = GraphUtils.topologicalSort(Arrays.asList(design, build, test));
        assertEquals(3, order.size());
        assertTrue(order.containsAll(Arrays.asList(design, build, test)));
        assertTrue(order.indexOf(build) <= order.indexOf(design));
        assertTrue(order.indexOf(test) <= order.indexOf(build));

        assertEquals(4, GraphUtils.longestPathDuration(Arrays.asList(design, build, test)));
        assertFalse(GraphUtils.hasCycle(order));
    }

    @Test
    public void testGraphUtilsLongestPathWithBranches() {
        Task base = new Task("Base", 2, Task.Priority.MEDIUM);
        Task branchA = new Task("BranchA", 3, Task.Priority.MEDIUM);
        Task branchB = new Task("BranchB", 6, Task.Priority.MEDIUM);
        Task leafA = new Task("LeafA", 4, Task.Priority.MEDIUM);
        Task leafB = new Task("LeafB", 1, Task.Priority.MEDIUM);
        Task finalLeaf = new Task("Final", 3, Task.Priority.MEDIUM);

        branchA.addDependency(base);
        branchB.addDependency(base);
        leafA.addDependency(branchA);
        leafB.addDependency(branchB);
        finalLeaf.addDependency(leafA);
        finalLeaf.addDependency(leafB);

        List<Task> tasks = Arrays.asList(base, branchA, branchB, leafA, leafB, finalLeaf);
        List<Task> order = GraphUtils.topologicalSort(tasks);
        assertEquals(tasks.size(), order.size());
        assertTrue(order.containsAll(tasks));

        // GraphUtils treats dependencies as forward edges (child -> parent), so path length
        // corresponds to the deepest dependency chain starting from a leaf.
        assertEquals(6, GraphUtils.longestPathDuration(tasks));
        assertFalse(GraphUtils.hasCycle(tasks));
    }

    @Test
    public void testGraphUtilsDetectsCycle() {
        Task a = new Task("A", 1, Task.Priority.LOW);
        Task b = new Task("B", 1, Task.Priority.LOW);
        a.addDependency(b);
        b.addDependency(a);
        try {
            GraphUtils.topologicalSort(Arrays.asList(a, b));
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals("cycle detected", ex.getMessage());
        }
        assertTrue(GraphUtils.hasCycle(Arrays.asList(a, b)));
    }

    @Test
    public void testIdGeneratorConversions() {
        String generated = IdGenerator.nextIdStr();
        assertTrue(IdGenerator.fromString(generated) > 0);
        assertEquals(123L, IdGenerator.fromString("123"));
    }

    @Test
    public void testIdGeneratorNextIdMagnitudeAndUniqueness() {
        long id1 = IdGenerator.nextId();
        long id2 = IdGenerator.nextId();
        assertNotEquals(id1, id2);
        assertTrue(id1 > 1_000_000_000_000L);
        assertTrue(String.valueOf(id1).length() >= 15);
    }

    @Test(expected = DomainException.class)
    public void testIdGeneratorRejectsBadString() {
        IdGenerator.fromString("bad");
    }

    @Test(expected = DomainException.class)
    public void testIdGeneratorRejectsZero() {
        IdGenerator.fromString("0");
    }

    @Test(expected = DomainException.class)
    public void testIdGeneratorRejectsEmptyString() {
        IdGenerator.fromString("");
    }

    @Test
    public void testIdGeneratorFormulaIntegrity() throws Exception {
        Field field = IdGenerator.class.getDeclaredField("COUNTER");
        field.setAccessible(true);
        AtomicLong counter = (AtomicLong) field.get(null);

        long beforeMillis = System.currentTimeMillis();
        long beforeCounter = counter.get();
        long id = IdGenerator.nextId();
        long afterMillis = System.currentTimeMillis();
        long afterCounter = counter.get();

        assertEquals(beforeCounter + 1, afterCounter);

        boolean matchesFormula = false;
        for (long ts = beforeMillis; ts <= afterMillis; ts++) {
            long candidate = (ts << 20) ^ afterCounter;
            if (candidate < 0) candidate = -candidate;
            if (candidate == id) {
                matchesFormula = true;
                break;
            }
        }
        assertTrue("ID should follow (time<<20)^counter formula", matchesFormula);
    }

    @Test
    public void testMatchingEngineAssignmentsAndTaskState() {
        Researcher alice = new Researcher("Alice", 10);
        Researcher bob = new Researcher("Bob", 4);
        alice.updateRating(80);
        bob.updateRating(20);

        Task urgent = new Task("Urgent", 5, Task.Priority.CRITICAL);
        Task small = new Task("Small", 2, Task.Priority.LOW);

        List<MatchingEngine.Assignment> assignments =
                new MatchingEngine().match(Arrays.asList(alice, bob), Arrays.asList(small, urgent));
        assertEquals(1, assignments.size());

        MatchingEngine.Assignment smallAssignment = assignments.get(0);
        assertEquals(small.getId(), smallAssignment.getTask().getId());
        assertEquals(alice.getId(), smallAssignment.getResearcher().getId());
        assertEquals(Long.valueOf(alice.getId()), small.getAssignedResearcherId());
        assertNull("Urgent task should remain unassigned due to comparator reversing twice",
                urgent.getAssignedResearcherId());
        assertEquals(8, alice.getCapacity());
        assertEquals(4, bob.getCapacity());
    }

    @Test
    public void testMatchingEngineScoreFormulaAndNullInputs() {
        MatchingEngine engine = new MatchingEngine();
        Researcher researcher = new Researcher("Calc", 3);
        researcher.updateRating(40);
        Task task = new Task("CalcTask", 10, Task.Priority.LOW);

        double expected = Math.min(researcher.getCapacity(), task.getDuration()) * 0.1
                + researcher.getRating() * 0.05;
        assertEquals(expected, engine.score(researcher, task), 1e-9);

        assertTrue(engine.match(null, Collections.singletonList(task)).isEmpty());
        assertTrue(engine.match(Collections.singletonList(researcher), null).isEmpty());
    }

    @Test
    public void testMatchingEngineSkipsResearchersWithoutCapacity() {
        Researcher limited = new Researcher("Limited", 2);
        Researcher capable = new Researcher("Capable", 6);
        Task heavy = new Task("Heavy", 5, Task.Priority.MEDIUM);
        Task medium = new Task("Medium", 2, Task.Priority.MEDIUM);

        List<MatchingEngine.Assignment> assignments =
                new MatchingEngine().match(Arrays.asList(limited, capable), Arrays.asList(heavy, medium));
        assertEquals(2, assignments.size());
        assertEquals(Long.valueOf(capable.getId()), heavy.getAssignedResearcherId());
        assertEquals(Long.valueOf(limited.getId()), medium.getAssignedResearcherId());
        assertEquals(1, capable.getCapacity());
        assertEquals(0, limited.getCapacity());
    }

    @Test
    public void testProjectStatusCountsAndRiskAnalysis() {
        Project project = new Project("Alpha");
        Task planned = new Task("Plan", 1, Task.Priority.MEDIUM);
        Task inProgress = new Task("Develop", 2, Task.Priority.HIGH);
        inProgress.start();
        Task done = new Task("Done", 1, Task.Priority.LOW);
        done.complete();
        project.addTask(planned);
        project.addTask(inProgress);
        project.addTask(done);

        project.addResearcher(new Researcher("Dev", 5));
        project.addResearcher(new Researcher("QA", 3));

        project.addRisk(new Risk("Delay", "Schedule", 1, 0.5));

        Map<Task.Status, Long> counts = project.statusCounts();
        assertEquals(Long.valueOf(1), counts.get(Task.Status.PLANNED));
        assertEquals(Long.valueOf(1), counts.get(Task.Status.IN_PROGRESS));
        assertEquals(Long.valueOf(1), counts.get(Task.Status.DONE));
        assertEquals(Long.valueOf(0), counts.get(Task.Status.CANCELLED));

        List<MatchingEngine.Assignment> plannedAssignments = project.planAssignments();
        assertFalse(plannedAssignments.isEmpty());
        assertNotNull(plannedAssignments.get(0).getTask().getAssignedResearcherId());

        assertEquals(2, project.criticalPathDuration());

        RiskAnalyzer.SimulationResult result = project.analyzeRisk(5);
        assertEquals(0.5, result.getMeanImpact(), 1e-9);
        assertEquals(0.5, result.getWorstCaseImpact(), 1e-9);
    }

    @Test
    public void testProjectBudgetAndEntityManagement() {
        Project project = new Project(null);
        Budget original = project.getBudget();

        project.setBudget(null);
        assertSame(original, project.getBudget());

        Budget replacement = new Budget();
        replacement.add(new Budget.Item("CPU", 300, 1, "HW"));
        project.setBudget(replacement);
        assertSame(replacement, project.getBudget());

        Task task = new Task("Cancelable", 1, Task.Priority.LOW);
        project.addTask(task);
        task.cancel();
        assertEquals(task, project.getTask(task.getId()));

        Researcher researcher = new Researcher("Researcher", 5);
        project.addResearcher(researcher);
        assertEquals(researcher, project.getResearcher(researcher.getId()));

        project.addRisk(null);
        project.addRisk(new Risk("Minor", null, 0.1, 0.2));
        assertEquals(1, project.getRisks().size());

        Map<Task.Status, Long> counts = project.statusCounts();
        assertEquals(Long.valueOf(1), counts.get(Task.Status.CANCELLED));
    }

    @Test
    public void testProjectIgnoresNullEntitiesAndReturnsCopies() {
        Project project = new Project("Copy");
        assertNull(project.addTask(null));
        assertNull(project.addResearcher(null));
        assertTrue(project.getTasks().isEmpty());
        assertTrue(project.getResearchers().isEmpty());

        project.addRisk(new Risk("Stored", "Cat", 0.2, 0.3));
        List<Risk> risksCopy = project.getRisks();
        risksCopy.clear();
        assertEquals(1, project.getRisks().size());

        Collection<Task> tasksCopy = project.getTasks();
        assertTrue(tasksCopy.isEmpty());
    }

    @Test
    public void testProjectNameAndBudgetDefaults() {
        Project project = new Project(null);
        assertEquals("", project.getName());
        project.setName("Mars");
        assertEquals("Mars", project.getName());
        project.setName(null);
        assertEquals("", project.getName());

        Budget emptyBudget = project.getBudget();
        emptyBudget.add(new Budget.Item("Sensor", 100, 0.2, "HW"));
        assertEquals(100, project.getBudget().totalCost(), 1e-9);
    }

    @Test
    public void testReportGeneratorIncludesKeyMetrics() {
        Project project = new Project("Beta");
        Budget budget = new Budget();
        budget.add(new Budget.Item("Node", 200, 1.0, "IT"));
        project.setBudget(budget);

        Task task = new Task("Single", 2, Task.Priority.MEDIUM);
        project.addTask(task);
        project.addRisk(new Risk("Failure", "Technical", 1, 0.5));

        String report = new ReportGenerator().generate(project);
        assertTrue(report.contains("Project:Beta"));
        assertTrue(report.contains("Status PLANNED:1"));
        assertTrue(report.contains("CriticalPath:2"));
        assertTrue(report.contains("BudgetCost:200.0"));
        assertTrue(report.contains("BudgetValue:1.0"));
        assertTrue(report.contains("RiskMean:0.5"));
        assertTrue(report.contains("RiskWorst:0.5"));
    }

    @Test
    public void testResearcherSkillsCapacityAndRating() {
        Researcher researcher = new Researcher("Dana", 10);
        researcher.addSkill("AI", 15);
        assertEquals(10, researcher.getSkillLevel("AI"));
        researcher.addSkill("AI", 5);
        assertEquals(5, researcher.getSkillLevel("AI"));
        assertFalse(researcher.hasSkill("AI", 9));
        assertTrue(researcher.hasSkill("AI", 4));

        Task task = new Task("Model", 5, Task.Priority.HIGH);
        assertTrue(researcher.assignTask(task));
        assertEquals(5, researcher.getCapacity());
        assertFalse(researcher.allocateHours(10));
        assertTrue(researcher.completeTask(task, 120));
        assertEquals(30.0, researcher.getRating(), 1e-9);

        // releaseHours caps at 40 but decreases from post-complete capacity (10 -> 40)
        researcher.releaseHours(100);
        assertEquals(40, researcher.getCapacity());
    }

    @Test
    public void testResearcherNameSkillManagementAndGetSkillsCopy() {
        Researcher researcher = new Researcher(null, -5);
        assertEquals("", researcher.getName());
        assertEquals(0, researcher.getCapacity());

        researcher.setName(null);
        assertEquals("", researcher.getName());
        researcher.setName("Eva");
        assertEquals("Eva", researcher.getName());

        researcher.addSkill(null, 5);
        assertTrue(researcher.getSkills().isEmpty());

        researcher.addSkill("ML", -10);
        assertEquals(0, researcher.getSkillLevel("ML"));
        researcher.addSkill("ML", 15);
        assertEquals(10, researcher.getSkillLevel("ML"));
        assertTrue(researcher.hasSkill("ML", 0));

        Set<String> copy = researcher.getSkills();
        copy.clear();
        assertFalse(researcher.getSkills().isEmpty());
    }

    @Test
    public void testResearcherAllocationReleaseAndRatingBounds() {
        Researcher researcher = new Researcher("Sam", 5);
        Task shortTask = new Task("Short", 2, Task.Priority.LOW);
        Task longTask = new Task("Long", 5, Task.Priority.MEDIUM);

        assertFalse(researcher.allocateHours(0));
        assertTrue(researcher.allocateHours(2));
        assertEquals(3, researcher.getCapacity());
        assertFalse(researcher.allocateHours(4));

        researcher.releaseHours(-10);
        assertEquals(3, researcher.getCapacity());
        researcher.releaseHours(100);
        assertEquals(40, researcher.getCapacity());

        assertFalse(researcher.canAssign(null));
        assertTrue(researcher.canAssign(shortTask));
        assertTrue(researcher.canAssign(longTask));

        researcher.updateRating(150);
        assertEquals(30.0, researcher.getRating(), 1e-9);
        researcher.updateRating(-20);
        assertEquals(21.0, researcher.getRating(), 1e-9);

        assertTrue(researcher.assignTask(longTask));
        assertEquals(35, researcher.getCapacity());
        assertTrue(researcher.completeTask(longTask, 120));
        assertEquals(40, researcher.getCapacity());
    }

    @Test
    public void testResearcherIdGetterIntegrityAndAssignFailure() throws Exception {
        Researcher researcher = new Researcher("idCheck", 1);
        Field idField = Researcher.class.getDeclaredField("id");
        idField.setAccessible(true);
        assertEquals(idField.getLong(researcher), researcher.getId());

        Task bigTask = new Task("Big", 5, Task.Priority.CRITICAL);
        assertFalse(researcher.assignTask(bigTask));
        assertEquals(1, researcher.getCapacity());
        assertFalse(researcher.completeTask(null, 50));
    }

    @Test
    public void testResourceBookingAndCancellationFlow() {
        Resource resource = new Resource("Lab", "Room");
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 9, 0);
        LocalDateTime end = start.plusHours(2);

        assertTrue(resource.isAvailable(start, end));
        assertTrue(resource.book(start, end));
        assertFalse(resource.isAvailable(start.plusMinutes(30), end.plusMinutes(30)));

        resource.cancel(start);
        assertTrue(resource.isAvailable(start, end));
        assertTrue(resource.listBookings().isEmpty());
        assertFalse(resource.conflicts(start, end));
    }

    @Test
    public void testResourceValidationRejectsInvalidIntervals() {
        Resource resource = new Resource(null, null);
        LocalDateTime start = LocalDateTime.of(2024, 1, 2, 10, 0);
        LocalDateTime end = start.plusHours(1);

        assertFalse(resource.isAvailable(null, end));
        assertFalse(resource.isAvailable(end, start));
        assertFalse(resource.book(end, start));
        assertEquals("", resource.getName());
        assertEquals("GENERIC", resource.getType());
    }

    @Test
    public void testRiskPriorityOrderingAndClamp() {
        Risk high = new Risk("High", "Cat", 1, 1);
        Risk medium = new Risk("Medium", "Cat", 0.5, 0.6);
        Risk low = new Risk("Low", "Cat", 0, 2);

        assertEquals(3, high.priority());
        assertEquals(2, medium.priority());
        assertEquals(0, low.priority());

        List<Risk> risks = new ArrayList<>(Arrays.asList(low, high, medium));
        Collections.sort(risks);
        assertEquals(high, risks.get(0));
        assertEquals(medium, risks.get(1));
        assertEquals(low, risks.get(2));

        Risk clamped = new Risk("Clamp", "Cat", -1, 2);
        assertEquals(0, clamped.getProbability(), 0);
        assertEquals(1, clamped.getImpact(), 0);
    }

    @Test
    public void testRiskAnalyzerSimulationOutputs() {
        Risk risk = new Risk("Certain", "Cat", 1, 0.25);
        RiskAnalyzer analyzer = new RiskAnalyzer();
        RiskAnalyzer.SimulationResult result = analyzer.simulate(Collections.singletonList(risk), 5);
        assertEquals(0.25, result.getMeanImpact(), 1e-9);
        assertEquals(0.25, result.getP90Impact(), 1e-9);
        assertEquals(0.25, result.getWorstCaseImpact(), 1e-9);

        RiskAnalyzer.SimulationResult empty = analyzer.simulate(Collections.<Risk>emptyList(), 0);
        assertEquals(0, empty.getMeanImpact(), 0);
        assertEquals(0, empty.getP90Impact(), 0);
        assertEquals(0, empty.getWorstCaseImpact(), 0);
    }

    @Test
    public void testRiskAnalyzerRndDeterministicSequence() throws Exception {
        RiskAnalyzer analyzer = new RiskAnalyzer();
        Field seedField = RiskAnalyzer.class.getDeclaredField("seed");
        seedField.setAccessible(true);
        long seed = 123456789L;
        seedField.setLong(analyzer, seed);

        double first = analyzer.rnd();
        seed ^= (seed << 13);
        seed ^= (seed >>> 7);
        seed ^= (seed << 17);
        long v = seed & ((1L << 53) - 1);
        double expectedFirst = v / (double)(1L << 53);
        assertEquals(expectedFirst, first, 1e-12);

        double second = analyzer.rnd();
        seed ^= (seed << 13);
        seed ^= (seed >>> 7);
        seed ^= (seed << 17);
        v = seed & ((1L << 53) - 1);
        double expectedSecond = v / (double)(1L << 53);
        assertEquals(expectedSecond, second, 1e-12);
    }

    @Test
    public void testRiskPriorityThresholdsAndCompareTo() {
        Risk lowPriority = new Risk("Low", "Cat", 0.1, 0.1);
        Risk mediumPriority = new Risk("Medium", "Cat", 0.5, 0.3);
        Risk boundary = new Risk("Boundary", "Cat", 0.5, 0.5);
        Risk highPriority = new Risk("High", "Cat", 1, 0.6);

        assertEquals(1, lowPriority.priority());
        assertEquals(1, mediumPriority.priority());
        assertEquals(2, boundary.priority());
        assertEquals(3, highPriority.priority());

        assertTrue(highPriority.compareTo(boundary) < 0);
        assertTrue(boundary.compareTo(lowPriority) < 0);
        assertTrue(lowPriority.compareTo(mediumPriority) > 0);
    }

    @Test
    public void testRiskAnalyzerMonteCarloStatistics() {
        Risk lowImpact = new Risk("Low", "Cat", 0.5, 0.2);
        Risk highImpact = new Risk("High", "Cat", 0.5, 1.0);
        RiskAnalyzer analyzer = new RiskAnalyzer();

        RiskAnalyzer.SimulationResult result =
                analyzer.simulate(Arrays.asList(lowImpact, highImpact), 2000);
        assertTrue(result.getMeanImpact() > 0.5);
        assertTrue(result.getP90Impact() >= result.getMeanImpact());
        assertTrue(result.getWorstCaseImpact() >= result.getP90Impact());
    }

    @Test
    public void testSchedulerProducesSlackAndLateTimes() {
        Task base = new Task("Base", 2, Task.Priority.MEDIUM);
        Task parallel = new Task("Parallel", 2, Task.Priority.MEDIUM);
        Task critical = new Task("Critical", 5, Task.Priority.HIGH);
        assertTrue(parallel.addDependency(base));
        assertTrue(critical.addDependency(base));

        new Scheduler().schedule(Arrays.asList(base, parallel, critical));

        assertEquals(0, base.getEst());
        assertEquals(2, base.getEft());
        assertEquals(0, parallel.getEst());
        assertEquals(2, parallel.getEft());
        assertEquals(0, critical.getEst());
        assertEquals(5, critical.getEft());
        assertTrue(parallel.getLst() >= parallel.getEst());
        assertTrue(base.getLst() >= base.getEst());
        assertEquals(1, parallel.slack());
        assertEquals(0, critical.slack());
    }

    @Test
    public void testTaskOperationsAndStates() {
        Task task = new Task(null, -1, null);
        task.setName(null);
        task.setDuration(-10);
        task.setPriority(null);
        assertEquals("", task.getName());
        assertEquals(0, task.getDuration());
        assertEquals(Task.Priority.MEDIUM, task.getPriority());

        task.requireSkill("AI", 12);
        task.requireSkill("AI", 6);
        assertEquals(10, (int) task.getRequiredSkills().get("AI"));
        task.requireSkill("", 5);
        assertEquals(1, task.getRequiredSkills().size());

        Task dependency = new Task("Dep", 1, Task.Priority.LOW);
        assertTrue(task.addDependency(dependency));
        assertFalse(task.addDependency(task));
        assertTrue(task.dependsOn(dependency));

        task.setSchedule(-1, -5, -10, -2);
        assertEquals(0, task.getEst());
        assertEquals(0, task.getLst());

        task.start();
        assertEquals(Task.Status.IN_PROGRESS, task.getStatus());
        task.updateProgress(2);
        assertEquals(1, task.getProgress(), 0);
        task.cancel();
        assertEquals(Task.Status.CANCELLED, task.getStatus());
        task.assignTo(99L);
        assertEquals(Long.valueOf(99), task.getAssignedResearcherId());

        Task done = new Task("Done", 1, Task.Priority.LOW);
        done.complete();
        assertEquals(Task.Status.DONE, done.getStatus());
    }
}
