[![Build Status](https://travis-ci.com/mP1/j2cl-java-util-Base64.svg?branch=master)](https://travis-ci.com/mP1/j2cl-java-util-Base64.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/j2cl-java-util-Base64/badge.svg?branch=master)](https://coveralls.io/github/mP1/j2cl-java-util-Base64?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/j2cl-java-util-Base64.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/j2cl-java-util-Base64/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/j2cl-java-util-Base64.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/j2cl-java-util-Base64/alerts/)



#  j2cl java.util.Base64

This project aims to provide a mostly complete `java.util.Base64` implementation under the Apache2 license.



### Missing functionality

Some methods from both `Decoder` and `Encoder` may not be implemented, such as the public methods with signaturesincluding `ByteBuffer`, `InputStream` and `OutputStream`.



### Transpiling

The `j2cl-maven-plugin` will shade the source during the transpile phase, so `walkingkooka.j2cl.java.util.Base64`
is available in javascript as `java.util.Base64`. 



## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/j2cl-java-util-Base64.git
```
