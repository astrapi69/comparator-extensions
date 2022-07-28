# Getting Started

### Rename your gradle project after cloning template project

Go to the unit test class 'io.github.astrapi69.InitialTemplateTest' and set your description for your repository in the
disabled unit test method testRenameToConcreteProject. Then enable the unit test method testRenameToConcreteProject by
comment or delete the annotation @Disabled and run it after. This will rename your gradle project. Now you can delete
all unrelated files and test dependencies that you do not need.

Also delete the section '# Template from this project' in the README.md

### Setting Secrets

When you clone from this repository you have to consider to set the secrets for the sonatype username and password.

In the project gradle.properties we have two properties:
(Note that the project gradle.properties is public and can see everyone)

```
projectRepositoriesUserNameKey=ossrhUsername
projectRepositoriesPasswordKey=ossrhPassword
```

The value of this properties keys are properties keys from your local file '~/.gradle/gradle.properties' were you keep
your username and your secret password that have to be kept secret. So in your local file '~/.gradle/gradle.properties'
looks for instance like this:

```
ossrhUsername=YourSonatypeUsername
ossrhPassword=YourSecretSonatypePassword
```
(Note that you have to replace 'YourSonatypeUsername' and 'YourSecretSonatypePassword' with your corresponding username
and password)

Your local build is now builds successful. But the build in your actions fail because you do not have set the secrets
in your repository.

For setting secrets for your repository you can consider the following sections:

* [Creating encrypted secrets for a repository](https://docs.github.com/en/actions/security-guides/encrypted-secrets#creating-encrypted-secrets-for-a-repository)
* [Creating encrypted secrets for an organization](https://docs.github.com/en/actions/security-guides/encrypted-secrets#creating-encrypted-secrets-for-an-organization)

Beware in step where you set the name you have to set for 'ossrhUsername' the value: 'ossrhUsername' and not 'OSSRHUSERNAME'
The same procedure for the secret 'ossrhPassword'

Note that for organizations you only need to set the secrets once.


The following source code is the complete gradle.yml in the repository folder '.github/workflows'

```
name: Java CI with Gradle

on:
  push:
    branches: [ develop ]
  pull_request:
    branches: [ develop ]

jobs:
  build:

    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 11
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'temurin'
    - name: Build with Gradle
      uses: gradle/gradle-build-action@bc3340afc5e3cc44f2321809ac090d731c13c514
      with:
        arguments: build
      env:
        ossrhUsername: ${{secrets.OSSRHUSERNAME}}
        ossrhPassword: ${{secrets.OSSRHPASSWORD}}
    - uses: codecov/codecov-action@v2
      with:
        token: ${{ secrets.CODECOV_TOKEN }} # not required for public repos

```

So you have to set two secrets: OSSRHUSERNAME, OSSRHPASSWORD. And one optional if your repository is not public
CODECOV_TOKEN where you save your codecov token value.

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
