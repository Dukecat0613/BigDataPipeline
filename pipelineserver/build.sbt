import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import ScalateKeys._

val ScalatraVersion = "2.5.1"

ScalatraPlugin.scalatraSettings

scalateSettings

organization := "HangWu"

name := "PipelineServer"

version := "1.0.0"

scalaVersion := "2.11.4"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
  "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "BigDataPipeline" % "BigDataPipeline" % "1.0.0" from "file:////Users/Dukecat/scala/BigDataPipeline/BigDataPipeline/target/BigDataPipeline-1.0.0.jar",
  "GoogleFinanceJavaApi" % "GoogleFinanceJavaApi" % "1.0.0" from "file:////Users/Dukecat/scala/BigDataPipeline/GoogleFinanceJavaApi/target/GoogleFinanceJavaApi-1.0.0.jar",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.1",
  "com.sun.jersey" % "jersey-servlet" % "1.19.3",
  "com.sun.jersey" % "jersey-server" % "1.19.3",
  "com.sun.jersey" % "jersey-core" % "1.19.3",
  "org.fusesource.scalamd" % "scalamd" % "1.5"
)

scalateTemplateConfig in Compile := {
  val base = (sourceDirectory in Compile).value
  Seq(
    TemplateConfig(
      base / "webapp" / "WEB-INF" / "templates",
      Seq.empty,  /* default imports should be added here */
      Seq(
        Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
      ),  /* add extra bindings here */
      Some("templates")
    )
  )
}

enablePlugins(JettyPlugin)
containerPort in Jetty := 9527