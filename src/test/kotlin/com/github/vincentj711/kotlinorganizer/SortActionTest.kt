package com.github.vincentj711.kotlinorganizer

import org.junit.jupiter.api.Test

class SortActionTest {
  private val testFile = """
    class A {
      fun a() {
        f()
        g()
      }
      val b = 10
      fun c() {
        a()
        e()
        c()
      }
      fun g() {
      }
      fun f() {
      }
      fun h() {
      }
      fun e() {
        h()
      }
      val d = 12
    }
  """

  @Test
  fun dfsTest() {
    SortActionTestCase(Config(
        fnOrderStrategy = Config.FnOrderStrategy.DFS
    ), testFile, """
      class A {
          val b = 10
          val d = 12
          fun c() {
            a()
            e()
            c()
          }
          fun a() {
            f()
            g()
          }
          fun f() {
          }
          fun g() {
          }
          fun e() {
            h()
          }
          fun h() {
          }
      }
    """).execute()
  }

  @Test
  fun bfsTest() {
    SortActionTestCase(Config(
        fnOrderStrategy = Config.FnOrderStrategy.BFS
    ), testFile, """
      class A {
          val b = 10
          val d = 12
          fun c() {
            a()
            e()
            c()
          }
          fun a() {
            f()
            g()
          }
          fun e() {
            h()
          }
          fun f() {
          }
          fun g() {
          }
          fun h() {
          }
      }
    """).execute()
  }
}