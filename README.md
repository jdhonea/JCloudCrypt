# JCloudCrypt

[![Build Status](https://travis-ci.com/jdhonea/JCloudCrypt.svg?branch=master)](https://travis-ci.com/jdhonea/JCloudCrypt) [![codecov](https://codecov.io/gh/jdhonea/JCloudCrypt/branch/master/graph/badge.svg)](https://codecov.io/gh/jdhonea/JCloudCrypt) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)  
A simple, portable, command-line utility to encrypt files. Designed with user privacy in mind, especially when using cloud storage services such as Drive, Dropbox, and OneDrive. Encrypts using AES-256 and Argon2 password hashing.

####Usage:
usage: JCloudCrypt [OPTION] -e \<FILE>
usage: JCloudCrypt -d \<FILE>
-d,--decrypt \<FILE> File to be decrypted. Options will be read
from file header.
-e,--encrypt \<FILE> File to be encrypted
-h,--help Prints this message
-m,--memCost \<SIZE> Password hashing memory cost in MB (default = 64)
-p,--parallelism \<NUMBER> Number of lanes and threads to be used for
password hashing (default = 4)
-r Randomize filename
-t,--timeCost \<NUMBER> Number of passes through memory (default = 10)

`JCloudCrypt -e /path/to/file -r -t 10`
`JCloudCrypt -d /path/to/file`
