[![Build Status](https://travis-ci.org/thiaguten/simple-compress.svg)](https://travis-ci.org/thiaguten/simple-compress)
[![Coverage Status](https://coveralls.io/repos/github/thiaguten/simple-compress/badge.svg?branch=master)](https://coveralls.io/github/thiaguten/simple-compress?branch=master)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Dependency Status](https://www.versioneye.com/user/projects/577e7c485bb1390040177b3b/badge.svg)](https://www.versioneye.com/user/projects/577e7c485bb1390040177b3b)

Simple .tar, .zip, .tgz compress implementation that makes the life easier based on apache commons compress.

```java
Archive archive = ArchiveType.ZIP.getStrategy();
Path compress = archive.compress(path...);
Path decompress = archive.decompress(compress);

Archive archive = ArchiveType.of("application/zip").getStrategy();
Path compress = archive.compress(path...);
Path decompress = archive.decompress(compress);
...
```