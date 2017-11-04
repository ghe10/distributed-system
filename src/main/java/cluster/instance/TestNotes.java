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
 *
 *
 *
 *  *******************************************************************************************************************
 *
 *  Mockito
 *
 *  Mock对象只能调用stubbed方法，调用不了它真实的方法。但Mockito可以监视一个真实的对象，这时对它进行方法调用时它将调用真实的方法，同时
     *  *  也可以stubbing这个对象的方法让它返回我们的期望值。另外不论是否是真实的方法调用都可以进行verify验证。和创建mock对象一样，对于final类
     *  、匿名类和Java的基本类型是无法进行spy的。
 *
    @Test
    public void spyTest2() {

        List list = new LinkedList();
        List spy = spy(list);

        //optionally, you can stub out some methods:
        when(spy.size()).thenReturn(100);

        //using the spy calls real methods
        spy.add("one");
        spy.add("two");

        //prints "one" - the first element of a list
        System.out.println(spy.get(0));

        //size() method was stubbed - 100 is printed
        System.out.println(spy.size());

        //optionally, you can verify
        verify(spy).add("one");
        verify(spy).add("two");
        }

        *
 *  Note : spy is watching a real object and real function calls. When the function is invoked, we can use a when
 *  to take some planned action, but actual function will be called. If the actual function call cause an exception
 *  or sth, then the test will fail because that exception
 *
 *  //Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
    when(spy.get(0)).thenReturn("foo");

    //You have to use doReturn() for stubbing
    doReturn("foo").when(spy).get(0);

 *
 *
 * Test with new in mockito
 * when(new File(anyString())).thenReturn(file); // not working
 *
     * powermock
 *  whenNew(File.class).withAnyArguments().thenReturn(file);
    whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStream);
 *
 * These will call real constructor, but returns sth planned
 */
public class TestNotes {
}
