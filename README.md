# JCloudCrypt

[![Build Status](https://travis-ci.com/jdhonea/JCloudCrypt.svg?branch=master)](https://travis-ci.com/jdhonea/JCloudCrypt) [![codecov](https://codecov.io/gh/jdhonea/JCloudCrypt/branch/master/graph/badge.svg)](https://codecov.io/gh/jdhonea/JCloudCrypt) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Description:

Designed with user privacy in mind, JCloudCrypt is a simple, portable command-line utility to encrypt files. Created especially for users using cloud storage services such as Drive, Dropbox, and OneDrive, JCloudCrypt can also be used to secure sensitive data locally. JCloudCrypt encrypts your files using AES-256 and Argon2 password hashing algorithm.

## Installation:

### Linux:

Mark JCloudCrypt as executable  
`chmod +x JCloudCrypt`  
and then move to /usr/bin  
`sudo mv JCloudCrypt /usr/bin`

## Usage:

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
