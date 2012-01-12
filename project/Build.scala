import sbt._
import java.util.jar._
import java.io.File
import scala.util.matching.Regex

import sbt._
import Keys._
import Scope.{GlobalScope, ThisScope}

// import com.github.siasia._
// import WebPlugin._
// import PluginKeys._


// for explanations of :=, <<+, <<=, !@#$, etc...
// https://github.com/harrah/xsbt/wiki/Getting-Started-More-About-Settings


object BuildSettings {
  val buildOrganization = "cc.acs"
  val buildScalaVersion = "2.9.1"
  val buildVersion = "0.1-SNAPSHOT"

  val buildSettings = Defaults.defaultSettings ++
  Seq (
    organization := buildOrganization,
    scalaVersion := buildScalaVersion,
    version := buildVersion,
    parallelExecution := true,
    retrieveManaged := true,
    autoCompilerPlugins := true,
    // resolvers += ScalaToolsSnapshots,
    externalResolvers <<= resolvers map { rs =>
      Resolver.withDefaultResolvers(rs)},
    // unmanagedJars in Compile <<= baseDirectory map { base => (base / "lib" ** "*.jar").classpath },
    moduleConfigurations ++= Resolvers.moduleConfigurations,
    javacOptions ++= Seq("-Xlint:unchecked"),
    publishTo := Some(Resolvers.IESLSnapshotRepo),
    publishArtifact in (Compile, packageDoc) := false,
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    scalacOptions ++= Seq(
      "-deprecation",
      // "-verbose",
      "-unchecked",
      "-Xcheckinit",
      "-encoding", "utf8", 
      "-explaintypes",
      // "-uniqid",
      // "-Xgenerate-phase-graph", "scalac-phase-graph",
      "-Xlint",
      // "-Xprint:all",
      // "-Xprint-icode",
      // "-Xprint-pos",
      "-Xprint-types"
      // "-Xshow-class <class>",
      // "-Xshow-class", "cc.acs.IntReaderX"
      // "-Xshow-phases",
    ),
    shellPrompt := ShellPrompt.buildShellPrompt
  )

  // -deprecation               Emit warning and location for usages of deprecated APIs.
  // -encoding <encoding>       Specify character encoding used by source files.
  // -explaintypes              Explain type errors in more detail.
  // -extdirs <path>            Override location of installed extensions.
  // -g:<level>                 Set level of generated debugging info. (none,source,line,vars,notailcalls) default:vars
  // -help                      Print a synopsis of standard options
  // -javabootclasspath <path>  Override java boot classpath.
  // -javaextdirs <path>        Override java extdirs classpath.
  // -no-specialization         Ignore @specialize annotations.
  // -nobootcp                  Do not use the boot classpath for the scala jars.
  // -nowarn                    Generate no warnings.
  // -optimise                  Generates faster bytecode by applying optimisations to the program
  // -print                     Print program with Scala-specific features removed.
  // -sourcepath <path>         Specify location(s) of source files.
  // -target:<target>           Target platform for object files. (jvm-1.5,msil) default:jvm-1.5
  // -toolcp <path>             Add to the runner classpath.
  // -unchecked                 Enable detailed unchecked (erasure) warnings.
  // -uniqid                    Uniquely tag all identifiers in debugging output.
  // -usejavacp                 Utilize the java.class.path in classpath resolution.
  // -verbose                   Output messages about what the compiler is doing.

  // -Xcheck-null                   Warn upon selection of nullable reference.
  // -Xcheckinit                    Wrap field accessors to throw an exception on uninitialized access.
  // -Xdisable-assertions           Generate no assertions or assumptions.
  // -Xelide-below <n>              Calls to @elidable methods are omitted if method priority is lower than argument
  // -Xexperimental                 Enable experimental extensions.
  // -Xfatal-warnings               Fail the compilation if there are any warnings.
  // -Xfuture                       Turn on future language features.
  // -Xgenerate-phase-graph <file>  Generate the phase graphs (outputs .dot files) to fileX.dot.
  // -Xlint                         Enable recommended additional warnings.
  // -Xlog-implicits                Show more detail on why some implicits are not applicable.
  // -Xmax-classfile-name <n>       Maximum filename length for generated classes
  // -Xmigration                    Warn about constructs whose behavior may have changed between 2.7 and 2.8.
  // -Xno-forwarders                Do not generate static forwarders in mirror classes.
  // -Xno-uescape                   Disable handling of \\u unicode escapes.
  // -Xnojline                      Do not use JLine for editing.
  // -Xplugin:<file>                Load one or more plugins from files.
  // -Xplugin-disable:<plugin>      Disable the given plugin(s).
  // -Xplugin-list                  Print a synopsis of loaded plugins.
  // -Xplugin-require:<plugin>      Abort unless the given plugin(s) are available.
  // -Xpluginsdir <path>            Path to search compiler plugins.
  // -Xprint:<phase>                Print out program after <phase>.
  // -Xprint-icode                  Log internal icode to *.icode files.
  // -Xprint-pos                    Print tree positions, as offsets.
  // -Xprint-types                  Print tree types (debugging option).
  // -Xprompt                       Display a prompt after each error (debugging option).
  // -Xresident                     Compiler stays resident: read source filenames from standard input.
  // -Xscript <object>              Treat the source file as a script and wrap it in a main method.
  // -Xshow-class <class>           Show internal representation of class.
  // -Xshow-object <object>         Show internal representation of object.
  // -Xshow-phases                  Print a synopsis of compiler phases.
  // -Xsource-reader <classname>    Specify a custom method for reading source files.
  // -Xsourcedir <directory>        (Requires -target:msil) Mirror source folder structure in output directory.
  // -Xverify                       Verify generic signatures in generated bytecode.


  // import com.typesafe.sbtaspectj.AspectjPlugin
  // import com.typesafe.sbtaspectj.AspectjPlugin.{ Aspectj, inputs, aspectFilter }
  //
  // val aspectJSettings = AspectjPlugin.settings ++ Seq(
  //   resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  //   inputs in Aspectj <<= update map { report =>
  //     report.matching(moduleFilter(organization = "se.scalablesolutions.akka", name = "akka-actor"))
  //                                   },
  //   aspectFilter in Aspectj := {
  //     (jar, aspects) => {
  //       if (jar.name.contains("akka-actor")) aspects filter (_.name.startsWith("Actor"))
  //       else Seq.empty[File]
  //     }
  //   },
  //   fullClasspath in Test <<= AspectjPlugin.useInstrumentedJars(Test),
  //   fullClasspath in Runtime <<= AspectjPlugin.useInstrumentedJars(Runtime)
  // )

}

object ShellPrompt {

  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }

  val current = """\*\s+([^\s]+)""".r

  def gitBranches = ("git branch --no-color" lines_! devnull mkString)
  def hgBranch = ("hg branch" lines_! devnull mkString)

  val buildShellPrompt = {
    (state: State) => {
      val currBranch = hgBranch
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (currBranch, currProject, BuildSettings.buildVersion)
    }
  }

}

object Resolvers {
  val IESLRepo                = "IESL Repo" at "http://iesl.cs.umass.edu:8081/nexus/content/repositories/releases"
  val IESLSnapshotRepo        = "IESL Snapshot Repo" at "http://iesl.cs.umass.edu:8081/nexus/content/repositories/snapshots"

  val moduleConfigurations = Seq(

  )
}

object Dependencies {

  val slf4j               = "org.slf4j"               % "slf4j-api" % "1.6.1"
  val logbackClassic      = "ch.qos.logback"          % "logback-classic"     % "0.9.24"
  val logbackCore         = "ch.qos.logback"          % "logback-core"        % "0.9.24"
  val scalaTest           = "org.scalatest"           %% "scalatest" % "1.6.1" % "test"
  val scalaCheck          = "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"
  val specs               = "org.scala-tools.testing" %% "specs" % "1.6.9"
  val specs2              = "org.specs2"              %% "specs2" % "1.4"
  val commonsIo           = "commons-io"              % "commons-io" % "2.0.1"
  val luceneAnalyzers     = "org.apache.lucene"       % "lucene-analyzers"    % "3.2.0"
  val luceneBenchmark     = "org.apache.lucene"       % "lucene-benchmark"    % "3.2.0"
  val luceneCore          = "org.apache.lucene"       % "lucene-core"         % "3.2.0"
  val luceneDemo          = "org.apache.lucene"       % "lucene-demo"         % "3.2.0"
  val luceneGrouping      = "org.apache.lucene"       % "lucene-grouping"     % "3.2.0"
  val luceneHighlighter   = "org.apache.lucene"       % "lucene-highlighter"  % "3.2.0"
  val luceneIcu           = "org.apache.lucene"       % "lucene-icu"          % "3.2.0"
  val luceneInstantiated  = "org.apache.lucene"       % "lucene-instantiated" % "3.2.0"
  val luceneMemory        = "org.apache.lucene"       % "lucene-memory"       % "3.2.0"
  val luceneMisc          = "org.apache.lucene"       % "lucene-misc"         % "3.2.0"
  val luceneParent        = "org.apache.lucene"       % "lucene-parent"       % "3.2.0"
  val luceneQueries       = "org.apache.lucene"       % "lucene-queries"      % "3.2.0"
  val luceneQueryparser   = "org.apache.lucene"       % "lucene-queryparser"  % "3.2.0"
  val luceneRemote        = "org.apache.lucene"       % "lucene-remote"       % "3.2.0"
  val luceneSmartcn       = "org.apache.lucene"       % "lucene-smartcn"      % "3.2.0"
  val luceneSpatial       = "org.apache.lucene"       % "lucene-spatial"      % "3.2.0"
  val luceneSpellchecker  = "org.apache.lucene"       % "lucene-spellchecker" % "3.2.0"
  val luceneStempel       = "org.apache.lucene"       % "lucene-stempel"      % "3.2.0"
  val luceneWordnet       = "org.apache.lucene"       % "lucene-wordnet"      % "3.2.0"
  val liftWebkit          = "net.liftweb"             %% "lift-webkit"  % "2.4-M4" % "compile->default"
  val liftMapper          = "net.liftweb"             %% "lift-mapper"  % "2.4-M4" % "compile->default"
  val liftWizard          = "net.liftweb"             %% "lift-wizard"  % "2.4-M4" % "compile->default"
  val liftMongoDB         = "net.liftweb"             %% "lift-mongodb"% "2.4-M4" % "compile->default"
  val scalazCore          = "org.scalaz"              %% "scalaz-core"          % "6.0.3"
  val scalaj              = "org.scalaj"              %% "scalaj-collection"    % "1.2"
  val factorie            = "cc.factorie"             % "factorie"              % "0.10.1-SNAPSHOT"
  val junit4              = "junit"                   %  "junit"                % "4.4"
  val antiXML             = "com.codecommit"          %%  "anti-xml"          % "0.2"
  val neo4jVersion        = "1.5.M02"
  val neo4j               = "org.neo4j"               % "neo4j" % neo4jVersion
  val neo4jAdvanced       = "org.neo4j"               % "neo4j-advanced" % neo4jVersion
  val neo4jShell          = "org.neo4j"               % "neo4j-shell" % neo4jVersion
  val commonsIO           = "commons-io"              % "commons-io" % "1.3.2"
  val jettisonJson        = "org.codehaus.jettison"   % "jettison" % "1.3"
  val jdom                = "org.jdom"                % "jdom"                % "1.1"
  val jaxen               = "jaxen"                   % "jaxen"               % "1.1.1"
  val javaxServlet        = "javax.servlet"           % "servlet-api" % "2.5" % "provided"
  val jettyVersion        = "8.0.0.M3"
  val jetty               = "org.eclipse.jetty"       % "jetty-webapp" % jettyVersion % "container"

  val casbahVersion       = "2.1.5-1"
  def casbahLib(s:String) = "com.mongodb.casbah"      % ("casbah-" + s + "_2.9.1") % casbahVersion
  val casbahLibs          = "core commons query".split(" ") map (l => casbahLib(l))

  def scalac              = "org.scala-lang" % "scala-compiler"
  def scalalib            = "org.scala-lang" % "scala-library"

}


object Rexa2Build extends Build {

  val buildShellPrompt = ShellPrompt.buildShellPrompt

  import Resolvers._
  import Dependencies._
  import BuildSettings._

  val testingModules:Seq[sbt.ModuleID] =
    Seq(
      scalaTest,
      junit4,
      scalaCheck,
      specs % "provided->default"
    )

  val loggingModules:Seq[sbt.ModuleID] =
    Seq(
      slf4j,
      logbackClassic,
      logbackCore
    )

  val webModules = Seq(
    liftWebkit,
    liftMapper,
    liftWizard,
    liftMongoDB,
    jetty,
    javaxServlet
  )



  lazy val genesis:Project = Project(
    id = "scala-genesis",
    base = file("."),
    settings =
      buildSettings ++ Seq(
        libraryDependencies := testingModules ++ loggingModules ++ Seq(
          "cc.acs.commons" %% "acs-commons" % "0.1-SNAPSHOT",
          scalazCore
        )
      )
    // settings = buildSettings ++ Seq (libraryDependencies := commonDeps)
  ) dependsOn (lensedSamples)




  // A project-specific implementation of the mongo repl in scala
  lazy val mongoSRepl:Project = {
    val consoleInit = """
    |import cc.rexa2.MongoDBInteraction._
    |
    |println("Commence hacking...")
    """.stripMargin 

    Project(
      id = "mongo-srepl",
      base = file("mongo-srepl"),
      settings =
        buildSettings ++ Seq(
          libraryDependencies := testingModules ++ loggingModules ++ casbahLibs ++ Seq(
            scalazCore
          ),
          initialCommands := consoleInit
        )
    ) dependsOn (lensedSamples)
  }


  // Just some samples to play with
  lazy val lensedSamples:Project = Project(
    id = "lensed-samples",
    base = file("lensed-samples"),
    settings =
      buildSettings ++ Seq(
        libraryDependencies := testingModules ++ loggingModules ++ Seq(
          scalazCore
        ),
        scalacOptions <+= (packagedArtifact in Compile in plugin in packageBin) map (art => "-Xplugin:%s" format art._2.getAbsolutePath),
        scalacOptions += "-Xplugin-require:lensed"
      )
  ) dependsOn (plugin)


  // These two are shoehorned in here to avoid building/publishing pain
  lazy val annotation = Project(
    id = "lensed-annotation",
    base = file("lensed-annotation"),
    settings = buildSettings
  )

  lazy val plugin = Project(
    id = "lensed-plugin",
    base = file("lensed-plugin"),
    settings = buildSettings ++ Seq[Setting[_]](
      libraryDependencies += scalazCore,
      libraryDependencies <++= scalaVersion { sv =>
        scalac % sv ::
        scalalib % sv ::
        Nil
      }
    )
  ) dependsOn (annotation)

}





