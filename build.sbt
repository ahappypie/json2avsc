version := "0.0.1"

scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "json2avsc",
    idePackagePrefix := Some("com.github.ahappypie")
  )

libraryDependencies ++= Seq(
  "info.picocli" % "picocli" % "4.6.3",
  "info.picocli" % "picocli-codegen" % "4.6.3" % "provided",
  "com.sksamuel.avro4s" %% "avro4s-json" % "4.1.0"
)

lazy val processAnnotations = taskKey[Unit]("Process annotations")

processAnnotations := {
  val log = streams.value.log

  log.info("Processing annotations ...")

  val classpath = ((products in Compile).value ++ ((dependencyClasspath in Compile).value.files)) mkString ":"
  val destinationDirectory = (classDirectory in Compile).value
  val processor = "picocli.codegen.aot.graalvm.processor.NativeImageConfigGeneratorProcessor"
  val classesToProcess = Seq("com.github.ahappypie.JSONToAvroConverter") mkString " "

  val command = s"javac -cp $classpath -proc:only -processor $processor -XprintRounds -d $destinationDirectory $classesToProcess"

  failIfNonZeroExitStatus(command, "Failed to process annotations.", log)

  log.info("Done processing annotations.")
}

def failIfNonZeroExitStatus(command: String, message: => String, log: Logger) = {
  import scala.sys.process._
  val result = command !

  if (result != 0) {
    log.error(message)
    sys.error("Failed running command: " + command)
  }
}

packageBin in Compile := (packageBin in Compile dependsOn (processAnnotations in Compile)).value

graalVMNativeImageCommand := "/usr/local/opt/sdkman-cli/libexec/candidates/java/22.2.r17-grl/bin/native-image"

enablePlugins(GraalVMNativeImagePlugin)