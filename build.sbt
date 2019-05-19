name := "top-tracks-app"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.3.1",
  "org.apache.spark" %% "spark-sql" % "2.3.1",
  "com.typesafe" % "config" % "1.3.3",
  "com.holdenkarau" %% "spark-testing-base" % "2.3.1_0.10.0" % "test"
)

TaskKey[Unit]("topTracksApp") := (runMain in Compile).toTask(" com.analytics.by.vodzianova.TopTracksApp").value

fork in Test := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

//--------------------------------
//---- sbt-assembly settings -----
//--------------------------------

val mainClassString = "com.analytics.by.vodzianova.TopTracksApp"

mainClass in assembly := Some(mainClassString)

assemblyJarName := "top-tracks-app.jar"

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}

assemblyOption in assembly ~= { _.copy(cacheOutput = false) }

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter { c =>
    c.data.getName.startsWith("log4j")
    c.data.getName.startsWith("slf4j-") ||
      c.data.getName.startsWith("scala-library")
  }
}

publishArtifact in(Compile, packageDoc) := false

publishTo := Some(Resolver.file("file", new File("artifacts")))

cleanFiles += baseDirectory { base => base / "artifacts" }.value

//--------------------------------
//----- sbt-docker settings ------
//--------------------------------
enablePlugins(sbtdocker.DockerPlugin)

dockerfile in docker := {
  val baseDir = baseDirectory.value
  val artifact: File = assembly.value

  val sparkHome = "/usr/local"
  val imageAppBaseDir = "/top-tracks-app"
  val artifactTargetPath = s"$imageAppBaseDir/${artifact.name}"

  val dockerResourcesDir = baseDir / "docker-resources"
  val dockerResourcesTargetPath = s"$imageAppBaseDir/"

  new Dockerfile {
    from("semantive/spark")
    maintainer("Elena Vodzianova")
    env("APP_BASE", s"$imageAppBaseDir")
    env("APP_CLASS", mainClassString)
    env("SPARK_HOME", sparkHome)
    copy(artifact, artifactTargetPath)
    copy(dockerResourcesDir, dockerResourcesTargetPath)
    entryPoint(s"${dockerResourcesTargetPath}docker-entrypoint.sh")
  }
}
buildOptions in docker := BuildOptions(cache = false)

imageNames in docker := Seq(
  ImageName(
    namespace = Some(organization.value.toLowerCase),
    repository = name.value,
    tag = Some(sys.props.getOrElse("IMAGE_TAG", default = version.value))
  )
)