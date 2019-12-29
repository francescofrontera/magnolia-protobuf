version in ThisBuild := "0.1"

scalaVersion in ThisBuild := "2.13.1"

lazy val capMagnolia: Project = project
  .in(file("magnolia-protobuf"))
  .settings(
    name := "magnolia-protobuf",
    libraryDependencies ++= Dependencies.dependenciesMProject
  )
