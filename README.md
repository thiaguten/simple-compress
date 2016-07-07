[![Build Status](https://travis-ci.org/thiaguten/simple-compress.svg)](https://travis-ci.org/thiaguten/simple-compress)
[![Coverage Status](https://coveralls.io/repos/github/thiaguten/simple-compress/badge.svg?branch=master)](https://coveralls.io/github/thiaguten/simple-compress?branch=master)
[![License](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://www.apache.org/licenses/LICENSE-2.0.html)


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