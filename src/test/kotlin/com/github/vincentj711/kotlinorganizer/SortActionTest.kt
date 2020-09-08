package com.github.vincentj711.kotlinorganizer

import org.junit.jupiter.api.Test

class SortActionTest {
  private val testFile1 = """
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

  private val testFile2 = """
    fun f() {
    }
    // this is variable d
    var d = "asdfasfd"
    object P {
    }
    fun g() {
    }
    class R {
    }
    interface H {
    }
    /** javadoc for typealias S */
    typealias S = Boolean
    /** here is a javadoc for interface I */
    interface I {
      /*
         this is interface I
       */
      // and interface I's function j
      fun j()
    }
    enum class K(
        val description: String = ""
    ) {
      L,
      /** this is K.M enum */
      M;
      fun z() {
      }
      fun n() {
        z()
      }
    }
    const val a = 10
    object O {
    }
    val c = "asdf"
    typealias T = String
    class Q(val temp: Int) {
      fun qa() {
      }
      val qj = 20
      fun qb() {
      }
      enum class Qc() {
      }
      object Qd {
      }
      constructor(tmp: Int, name: String) : this(tmp) {
        // ctor 0
      }
      interface Qe {
      }
      init {
        // init block 1
      }
      object Qf {
      }
      enum class Qg {
      }
      class Qh {
      }
      constructor(tmp: Int, enabled: Boolean) : this(tmp) {
        // ctor 1
      }
      val qi = 10
      companion object {
        fun hh() {
        }
        fun gg() {
        }
        val ff = "ASdf"
      }
      init {
        // init block 0
      }
    }
    /** this is variable b */
    val b = 20
    fun e() {
    }
  """

  @Test
  fun alphabeticalFnOrderTest() {
    SortActionTestCase(Config(
        fnOrderStrategy = Config.FnOrderStrategy.ALPHABETICAL
    ), testFile1, """
      class A {
          val b = 10
          val d = 12
          fun a() {
            f()
            g()
          }
          fun c() {
            a()
            e()
            c()
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

  @Test
  fun preserveFnOrderTest() {
    SortActionTestCase(Config(
        fnOrderStrategy = Config.FnOrderStrategy.PRESERVE
    ), testFile1, """
      class A {
          val b = 10
          val d = 12
          fun a() {
            f()
            g()
          }
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
      }
    """).execute()
  }

  @Test
  fun dfsTest() {
    SortActionTestCase(Config(
        fnOrderStrategy = Config.FnOrderStrategy.DFS
    ), testFile1, """
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
    ), testFile1, """
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

  @Test
  fun largeTest() {
    println(Config().groupOrdering)
    SortActionTestCase(Config(
        fnOrderStrategy = Config.FnOrderStrategy.ALPHABETICAL
    ), testFile2, """
      /** javadoc for typealias S */
      typealias S = Boolean
      typealias T = String

      // this is variable d
      var d = "asdfasfd"
      const val a = 10
      val c = "asdf"
      /** this is variable b */
      val b = 20
      
      fun e() {
      }
      fun f() {
      }
      fun g() {
      }
      
      interface H {
      }
      /** here is a javadoc for interface I */
      interface I {
          /*
               this is interface I
             */
          // and interface I's function j
          fun j()
      }
      
      enum class K(
        val description: String = ""
      ) {
          L,
          /** this is K.M enum */
          M;
      
          fun n() {
            z()
          }
          fun z() {
          }
      }
      
      object P {
      }
      
      object O {
      }
      
      class R {
      }
      
      class Q(val temp: Int) {
          companion object {
              val ff = "ASdf"
              fun gg() {
              }
              fun hh() {
              }
          }
      
          val qj = 20
      
          constructor(tmp: Int, name: String) : this(tmp) {
            // ctor 0
          }
      
          init {
            // init block 1
          }
      
          constructor(tmp: Int, enabled: Boolean) : this(tmp) {
            // ctor 1
          }
      
          val qi = 10
      
          init {
            // init block 0
          }
      
          fun qa() {
          }
          fun qb() {
          }

          interface Qe {
          }
      
          enum class Qc() {
          } 
          enum class Qg {
          }
      
          object Qd {
          }
          object Qf {
          }
      
          class Qh {
          }
      }
    """).execute()
  }
}