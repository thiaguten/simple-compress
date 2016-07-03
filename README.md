[![Build Status](https://travis-ci.org/thiaguten/simple-compress.svg)](https://travis-ci.org/thiaguten/simple-compress)
[![Coverage Status](https://coveralls.io/repos/github/thiaguten/simple-compress/badge.svg?branch=master)](https://coveralls.io/github/thiaguten/simple-compress?branch=master)

Simple .tar, .zip, .tgz compress implementation that makes the life easier based on commons-compress.

```java
Archive archive = ArchiveType.ZIP.getStrategy();
archive.compress(Paths...);

Archive archive = ArchiveType.of("application/zip").getStrategy();
archive.compress(Paths...);
...
```