# RAG Genie

## About

The RAG Genie, an LLM RAG prototype to test and evaluate your embeddings, chun splitting strategies using Q&A and evaluations. 

https://github.com/stephanj/rag-genie/assets/179457/e154a2ba-b031-4c62-adb4-fc87c7d448da

## Contribute

To contribute to this project, please read the [contribution guidelines](CONTRIBUTING.md).
Also make sure to read the [translation guidelines](TRANSLATION.md) if you want to contribute to the translations.

## Useful links

* [ChangeLog](CHANGELOG.md)
* [Github project](https://github.com/stephanj/rag-genie/)

## Configure your development environment

### Install Taskfile.dev

Taskfile is a task runner / build tool that aims to be simpler and easier to use than, for example, GNU Make.

If you're on macOS or Linux and have [Homebrew](https://brew.sh/) installed, just run:

    brew install go-task

Otherwise, you can follow the [installation instructions](https://taskfile.dev/installation/) for your platform.

You can list all available tasks with:

    > task

    task: Available tasks for this project:

    * clean:                                🧽 Clean generated code and binaries
    * default:                              📝 List all tasks
    * genie:backend:start:                  🏃 Start Genie Spring Boot backend
    * genie:backend:start:prod:             🏃 Start Genie Spring Boot backend in production mode
    * genie:build:                          🏗️ Build the app (tests are skipped) (aliases: build)
    * genie:build:prod:                     🏗 Build the app (tests are skipped) in production mode
    * genie:frontend:build:                 🏗️ Build Genie Node/Angular frontend
    * genie:frontend:start:                 🏃 Start Genie Node/Angular frontend
    * genie:frontend:sync:                  👀 Build Genie Node/Angular frontend in watch mode
    * genie:release:                        📦 Release a new Genie version (automatically selected)                      (aliases: release)
    * genie:release:alpha:                  📦 Release a new Genie pre-release alpha version                             (aliases: release:alpha)
    * genie:release:beta:                   📦 Release a new Genie pre-release beta version                              (aliases: release:beta)
    * genie:release:major:                  📦 Release a new Genie major version                                         (aliases: release:major)
    * genie:release:minor:                  📦 Release a new Genie minor version                                         (aliases: release:minor)
    * genie:release:patch:                  📦 Release a new Genie patch version                                         (aliases: release:patch)
    * genie:start:                          🏃 Start Genie                                                               (aliases: start)
    * genie:start:prod:                     🏃 Start Genie locally in production mode (triggers a full clean build)      (aliases: start:prod)
    * genie:test:                           ✅ Test the app                                                              (aliases: test)
    * clean:full:                           🧽 Clean generated code, binaries including Node and its modules
    * db:drop:                              🧽 Stop and remove all services data (PostgreSQL)
    * db:dump:                              ⬇️ Dump data from local PostgreSQL instance
    * db:import:                            ⬆️ Import data into local PostgreSQL instance
    * db:liquibase:clear-checksums:         🧽 Clear Liquibase checksums
    * db:liquibase:update:                  ⚙️ Update local database with Liquibase changes
    * db:liquibase:validate:                ☑️ Validate local database with Liquibase changes
    * db:restart:                           🔄 Restart PostgreSQL db
    * db:start:                             🏃 Start PostgreSQL db
    * db:status:                            🚦 PostgreSQL db status
    * db:stop:                              🛑 Stop PostgreSQL db
    * env:setup:                            🛠️ Setup the project environment
    * env:verify:                           ☑️ Verify the project environment setup

If you want to see the commands executed by a task, you can use the `--dry-run` option:

    task --dry genie:build

### Verify your environment

To verify your environment is compatible with the project, run the following command:

    task env:verify

### Setup your environment

You can manually setup your environment by following feedback from the `env:verify` task 
or you can run the following command to let us doing the setup:

    task env:setup

The script will install all required tools and dependencies and supports the following package managers:

* [ASDF](https://asdf-vm.com/) - preferred when available as it supports a large set of tools and versions
* [SDKMAN](https://sdkman.io/) - used when ASDF is not available to setup Java
* [Homebrew](https://brew.sh/) - used when others are not available. It will change the default version of the required tools on your system which is why ASDF or SDKMAN are preferred.

## Build and execute automated tests

The project uses [Apache Maven](https://maven.apache.org/) as build tool and [JUnit 5](https://junit.org/junit5/) as test framework.

To build the project without running the tests, run the following command:

    task build

The application is packaged as a [Spring Boot](https://spring.io/projects/spring-boot) executable jar file.
You can find the jar file in the `target` directory, it is compiled in `dev` mode.

To build it in production mode, run the following command:

    task genie:build:prod

To build the project and execute the tests, run the following command:

    task test

## Run Locally

### Environment variables

First create a `.env` file in the root of the project by copying the sample file as follows:

    copy .env.example .env

Update the content of `.env` with the correct values provided by a project administrator.

You can also use `.env` to override the default values from `.env.default`.

You can pass to the application any configuration parameter as described in [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config). 


### Start Genie

To start the application, run the following command:

    task start

The application is available at [http://localhost:8080](http://localhost:8080).

Launch in dev mode with hot reload enabled.
Any change in the code will be automatically reloaded.
Frontend code is automatically compiled using [Webpack](https://webpack.js.org/) and **[LiveReload](https://chromewebstore.google.com/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei?hl=fr) is used to reload the page when any change is detected (just install the browser extension and enable it on the website).**
We also advise installing the [Angular DevTools](https://chromewebstore.google.com/detail/angular-devtools/ienfalfjdbdpebioblfackkekamfmbnh) extension to debug the Angular application.

### Production mode

To build and launch the application in production mode, run the following command:

    task start:prod

### Manage the local database manually

You can start it with:

    task db:start

Restart it with:

    task db:restart

Stop it with:

    task db:stop

Check its status with:

    task db:status

Drop its content with:

    task db:drop

Dump its content with:

    task db:dump

### Liquibase Usage

Liquibase is used to manage the database schema.

You can update the database schema with the following command:

    task db:liquibase:update

You can also validate the database schema with the following command:

    task db:liquibase:validate

You can clear the checksums so liquibase can update from a fresh start with the following command:

    task db:liquibase:clear-checksums

By default, liquibase-maven-plugin is configured to target the local DB defined as

    <liquibase.url>jdbc:postgresql://localhost:5432/postgres</liquibase.url>
    <liquibase.username>postgres</liquibase.username>
    <liquibase.password>mysecretpassword</liquibase.password>

#### Liquibase table updates

Make sure to use changesets to add new columns or indexes

for example

     <changeSet id="20231019-1171" author="stephan">

        <addColumn tableName="genie_content">
            <column name="enable_tags" type="boolean" defaultValue="false">
                <constraints nullable="true" />
            </column>
        </addColumn>
    </changeSet>

### Frontend environment

To build individual the frontend (in production mode), run the following command:

    task genie:frontend:build

And you can launch the frontend in dev mode with:

    task genie:frontend:start

Then you need to start the backend with the following command:

    task genie:backend:start

Use `npm` and `npx` wrappers to manually run any command like `./npm run start` or `./npx webpack`.

## Release a new version

To release a new version, run the following command:

    task release

The release automation:

* updates the version in the `pom.xml` file,
* updates the version in the `package.json`/`package-lock.json` file,
* updates the CHANGELOG.md file,
* commits the changes,
* tags the commit,
* proposes to push the commit and the tag to the remote repository.

`task release` is selecting the next version (`X.Y.Z`) based on changes since the last release.

It is following the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) convention.

If there is a commit with a breaking change it will increase to the next major version (X) :

> **BREAKING CHANGE:** a commit that has a footer `BREAKING CHANGE:`, or appends a `!` after the type/scope, introduces a breaking API change (correlating with [**`MAJOR`**](http://semver.org/#summary) in Semantic Versioning). A BREAKING CHANGE can be part of commits of any _type_.

else if a commit contains a feature it will increase to the next feature version (Y):

> **feat:** a commit of the _type_ `feat` introduces a new feature to the codebase (this correlates with [**`MINOR`**](http://semver.org/#summary) in Semantic Versioning).

otherwise it will increase to the next patch version (Y):

> **fix:** a commit of the _type_ `fix` patches a bug in your codebase (this correlates with [**`PATCH`**](http://semver.org/#summary) in Semantic Versioning).
>
> types other than fix: and feat: are allowed, for example @commitlint/config-conventional (based on the Angular convention) recommends build:, chore:, ci:, docs:, style:, refactor:, perf:, test:, and others.

**Rollback a release:** After release you are asked to confirm to push the release commit and tag to gitlab. You can always refuse it and cancel the release by removing the release commit (`git reset --hard HEAD~1`) and the tag (`git tag --delete vX.Y.Z`)

You can also manually select the version to release with the following command:

    task release:patch # To force the creation of a patch version
    task release:minor # To force the creation of a minor version
    task release:major # To force the creation of a major version

    task release:alpha # To force the creation of a pre-release alpha version (X.Y.Z-alpha.N)
    task release:beta # To force the creation of a pre-release beta version (X.Y.Z-beta.N)

## Conventional Commits

We are using [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) for commit messages.

The commit message should be structured as follows:

    <type>[optional scope]: <description>

    [optional body]

    [optional footer(s)]

Common types are:

* **Feature (`feat`)**: A commit of this type introduces a new feature to the codebase. This correlates with a [**`MINOR`**](http://semver.org/#summary) version in Semantic Versioning.
* **Fix (`fix`)**: A commit of this type patches a bug in your codebase. This correlates with a [**`PATCH`**](http://semver.org/#summary) version in Semantic Versioning.
* **Documentation (`docs`)**: A commit of this type only affects documentation.
* **Refactor (`refactor`)**: A commit of this type involves code refactoring, which neither fixes a bug nor adds a feature.
* **Style (`style`)**: A commit of this type pertains to formatting, white-space, or other changes that do not affect the meaning of the code.
* **Chore (`chore`)**: A commit of this type includes changes that do not relate to a fix or feature and do not modify source or test files. For example, updating dependencies.
* **Performance (`perf`)**: A commit of this type enhances performance.
* **Test (`test`)**: A commit of this type either adds missing tests or corrects existing tests.
* **Build (`build`)**: A commit of this type affects the build system or external dependencies. Example scopes include gulp, broccoli, npm.
* **Continuous Integration (`ci`)**: A commit of this type affects the continuous integration system configuration.
* **Revert (`revert`)**: A commit of this type reverts a previous commit.

**BREAKING CHANGE**: a commit that has a footer `BREAKING CHANGE:`, or appends a `!` after the type/scope, introduces a breaking API change (correlating with [**`MAJOR`**](http://semver.org/#summary) in Semantic Versioning). A BREAKING CHANGE can be part of commits of any _type_.

Optional scopes can be anything specifying the place of the commit change. For example, it can be the reference of a ticket like `#1234`.

Example:

    feat(#1234): allow provided config object to extend other configs

    BREAKING CHANGE: `extends` key in config file is now used for extending other config files

    closes #1234
