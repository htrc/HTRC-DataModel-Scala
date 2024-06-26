[![Scala CI](https://github.com/htrc/HTRC-DataModel-Scala/actions/workflows/ci.yml/badge.svg)](https://github.com/htrc/HTRC-DataModel-Scala/actions/workflows/ci.yml)
[![codecov](https://codecov.io/github/htrc/HTRC-DataModel-Scala/branch/develop/graph/badge.svg?token=8V4H7VMNYP)](https://codecov.io/github/htrc/HTRC-DataModel-Scala)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/htrc/HTRC-DataModel-Scala?include_prereleases&sort=semver)](https://github.com/htrc/HTRC-DataModel-Scala/releases/latest)

# HTRC-DataModel-Scala
Contains object models representing volumes, pages, ids and pairtree items in Scala.

# Build
`sbt package`

then find the result in `target/scala-2.13/` folder.

# Usage
## SBT
`libraryDependencies += "org.hathitrust.htrc" %% "data-model" % VERSION`

## Maven

### Scala 2.12
```
<dependency>
  <groupId>org.hathitrust.htrc</groupId>
  <artifactId>data-model_2.12</artifactId>
  <version>VERSION</version>
</dependency>
```

### Scala 2.13
```
<dependency>
  <groupId>org.hathitrust.htrc</groupId>
  <artifactId>data-model_2.13</artifactId>
  <version>VERSION</version>
</dependency>
```

