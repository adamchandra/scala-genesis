
resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.10"))

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbtaspectj" % "sbt-aspectj" % "0.4.4")

libraryDependencies += "com.github.gseitz.lensed" %% "plugin" % "0.5"

resolvers += "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"
    
addSbtPlugin("org.ensime" %% "ensime-sbt-cmd" % "0.0.7-SNAPSHOT")
