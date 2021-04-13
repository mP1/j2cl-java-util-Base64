[![Build Status](https://github.com/mP1/j2cl-java-util-Base64/workflows/build.yaml/badge.svg)](https://github.com/mP1/j2cl-java-util-Base64/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/j2cl-java-util-Base64/badge.svg?branch=master)](https://coveralls.io/github/mP1/j2cl-java-util-Base64?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/j2cl-java-util-Base64.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/j2cl-java-util-Base64/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/j2cl-java-util-Base64.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/j2cl-java-util-Base64/alerts/)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)



#  j2cl java.util.Base64

This project aims to provide a mostly complete `java.util.Base64` implementation under the Apache2 license.



`java.util.Base64.Decoder`
- wrap(InputStream is) missing
- decode(ByteBuffer) missing



`java.util.Base64.Encoder`
- wrap(OutputStream) missing
- encode(ByteBuffer) missing



### Transpiling

The `j2cl-maven-plugin` will shade the source during the transpile phase, so `walkingkooka.j2cl.java.util.Base64`
is available in javascript as `java.util.Base64`. 



