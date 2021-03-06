import com.typesafe.sbt.{GitBranchPrompt, GitVersioning}

showCurrentGitBranch

git.useGitDescribe := true

lazy val commonSettings = Seq(
  organization := "org.hathitrust.htrc",
  organizationName := "HathiTrust Research Center",
  organizationHomepage := Some(url("https://www.hathitrust.org/htrc")),
  scalaVersion := "2.12.13",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  externalResolvers := Seq(
    Resolver.defaultLocal,
    Resolver.mavenLocal,
    "HTRC Nexus Repository" at "https://nexus.htrc.illinois.edu/repository/maven-public",
  ),
  Compile / packageBin / packageOptions += Package.ManifestAttributes(
    ("Git-Sha", git.gitHeadCommit.value.getOrElse("N/A")),
    ("Git-Branch", git.gitCurrentBranch.value),
    ("Git-Version", git.gitDescribedVersion.value.getOrElse("N/A")),
    ("Git-Dirty", git.gitUncommittedChanges.value.toString),
    ("Build-Date", new java.util.Date().toString)
  ),
  wartremoverErrors ++= Warts.unsafe.diff(Seq(
    Wart.DefaultArguments,
    Wart.NonUnitStatements,
    Wart.Any,
    Wart.TryPartial
  )),
  publishTo := {
    val nexus = "https://nexus.htrc.illinois.edu/"
    if (isSnapshot.value)
      Some("HTRC Snapshots Repository" at nexus + "repository/snapshots")
    else
      Some("HTRC Releases Repository"  at nexus + "repository/releases")
  },
  // force to run 'test' before 'package' and 'publish' tasks
  publish := (publish dependsOn Test / test).value,
  Keys.`package` := (Compile / Keys.`package` dependsOn Test / test).value
)

lazy val ammoniteSettings = Seq(
  libraryDependencies +=
    {
      val version = scalaBinaryVersion.value match {
        case "2.10" => "1.0.3"
        case _ ⇒ "2.3.8-58-aa8b2ab1"
      }
      "com.lihaoyi" % "ammonite" % version % "test" cross CrossVersion.full
    },
  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
    Seq(file)
  }.taskValue,
  Test / run / fork := false
)

lazy val `data-model` = (project in file("."))
  .enablePlugins(GitVersioning, GitBranchPrompt)
  .settings(commonSettings)
  .settings(ammoniteSettings)
  .settings(
    name := "data-model",
    description := "Defines the data model for various HTRC components (volumes, pages, etc.)",
    licenses += "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    libraryDependencies ++= Seq(
      "org.hathitrust.htrc"           %% "running-headers"      % "2.0.2",
      "org.hathitrust.htrc"           %% "scala-utils"          % "2.11",
      "gov.loc"                       %  "pairtree"             % "1.1.2",
      "com.jsuereth"                  %% "scala-arm"            % "2.0",
      "org.scalacheck"                %% "scalacheck"           % "1.15.4"      % Test,
      "org.scalatest"                 %% "scalatest"            % "3.2.9"       % Test
    ),
    crossScalaVersions := Seq("2.12.13", "2.11.12")
  )
