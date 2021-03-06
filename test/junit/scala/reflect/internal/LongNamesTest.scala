package scala.reflect.internal

import org.junit._

import scala.tools.testkit.VirtualCompiler
import scala.language.reflectiveCalls

class LongNamesTest {

  @Test def t11227(): Unit = {
    val compiler = new VirtualCompiler

    val longClassName = (0 to 512).map(_ => 'X').mkString

    val javaCode =
      s"""package pkg;
         |
         |public class Outer {
         |    public static class $longClassName {}
         |}
     """.stripMargin

    val scalaCode =
      s"""package pkg
         |
         |class Test {
         |  def test = new Outer.$longClassName().getClass.getName
         |}
     """.stripMargin

    compiler.compileJava("Outer.java" -> javaCode)

    compiler.compileScala("Test.scala" -> scalaCode)

    val testClass = compiler.classloader.loadClass("pkg.Test")

    val output = testClass.getDeclaredConstructor().newInstance().asInstanceOf[{ def test(): String }].test()
    Assert.assertEquals(s"pkg.Outer$$$longClassName", output)
  }
}
