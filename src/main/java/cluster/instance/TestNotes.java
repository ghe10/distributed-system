package cluster.instance;

/**
 * real object should be created for the tested class
 * step 1 : Junit
 * run with suit
 * import org.junit.runner.RunWith;
 * import org.junit.runners.Suite;
 * @RunWith(Suite.class)
 * @Suite.SuiteClasses({
 *  TestJunit1.class,
 *  TestJunit2.class
 * })
 * public class JunitTestSuite {
 * }
 *  Result result = JUnitCore.runClasses(JunitTestSuite.class);
 *
 *  一个含有 @Ignore 注释的测试方法将不会被执行。
 *  如果一个测试类有 @Ignore 注释，则它的测试方法将不会执行。
 *
 *   @Test(timeout=1000)
 *
 *   @Test(expected = ArithmeticException.class)
 *
 *  用 @RunWith(Parameterized.class) 来注释 test 类。
 *  创建一个由 @Parameters 注释的公共的静态方法，它返回一个对象的集合(数组)来作为测试数据集合。
 *  创建一个公共的构造函数，它接受和一行测试数据相等同的东西。
 *  为每一列测试数据创建一个实例变量。
 *  用实例变量作为测试数据的来源来创建你的测试用例。
 *
 *
import org.junit.Test;
import org.junit.Before;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PrimeNumberCheckerTest {
    private Integer inputNumber;
    private Boolean expectedResult;
    private PrimeNumberChecker primeNumberChecker;

    @Before
    public void initialize() {
        primeNumberChecker = new PrimeNumberChecker();
    }

    // Each parameter should be placed as an argument here
    // Every time runner triggers, it will pass the arguments
    // from parameters we defined in primeNumbers() method
    public PrimeNumberCheckerTest(Integer inputNumber,
                                  Boolean expectedResult) {
        this.inputNumber = inputNumber;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] {
                { 2, true },
                { 6, false },
                { 19, true },
                { 22, false },
                { 23, true }
        });
    }

    // This test will run 4 times since we have 5 parameters defined
    @Test
    public void testPrimeNumberChecker() {
        System.out.println("Parameterized Number is : " + inputNumber);
        assertEquals(expectedResult,
                primeNumberChecker.validate(inputNumber));
    }
}

 *  apache ant : what is that
*/
public class TestNotes {
}
