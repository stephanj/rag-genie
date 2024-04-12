# Development process

As we are now few developers (and who knows, maybe many more one day), and we use the [feature branching](https://confluence.atlassian.com/bitbucket/workflow-for-git-feature-branching-814201830.html) workflow.

## Create an issue

On Gitlab start by creating an issue.
This issue gets a number (e.g. `42`).

### `glab` CLI

We advise installing and configure the `glab` command line tool to easily manage issues from your shell.

Few examples:

* List all open issues assigned to you:

```
glab issue ls -a=@me
```

* Create a new issue:

```
glab issue create -t "Improve the CONTRIBUTORS guide" -d "Describe how to use glab CLI, Conventional Commits, ..." -a @me -l Documentation
```

* Browse an issue:

You can open it in your browser with:

```
glab issue view 1333 --web
```

* Comment an issue:

```
glab issue note 1333 -m "Don't forget about closing issues with the commit message"
```

* And close it:

```
glab issue close 1333
```

## Create a new local branch

Use a separate branch for each issue you work on.
Create a new branch with the number of the issue number and a short context (e.g. `issue-42-something-to-do`).

```
(master) $ git checkout -b issue-42-something-to-do
```

## Update code, add, commit

Once in your local branch, add and commit code as many times as you want:

```
(issue-42-something-to-do) $ git add .
(issue-42-something-to-do) $ git commit -m "chore(#42): blah blah"
(issue-42-something-to-do) $ ...
```

### Commit messages convention

We use the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) convention for commit messages.

The commit message should be structured as follows:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

The commit contains the following structural elements, to communicate intent to the
consumers of your library:

1. **fix:** a commit of the _type_ `fix` patches a bug in your codebase (this correlates with [`PATCH`](http://semver.org/#summary) in Semantic Versioning).
1. **feat:** a commit of the _type_ `feat` introduces a new feature to the codebase (this correlates with [`MINOR`](http://semver.org/#summary) in Semantic Versioning).
1. **BREAKING CHANGE:** a commit that has a footer `BREAKING CHANGE:`, or appends a `!` after the type/scope, introduces a breaking API change (correlating with [`MAJOR`](http://semver.org/#summary) in Semantic Versioning).
   A BREAKING CHANGE can be part of commits of any _type_.
1. _types_ other than `fix:` and `feat:` are allowed, for example [@commitlint/config-conventional](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional) (based on the [Angular convention](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines)) recommends `build:`, `chore:`,
   `ci:`, `docs:`, `style:`, `refactor:`, `perf:`, `test:`, and others.
1. _footers_ other than `BREAKING CHANGE: <description>` may be provided and follow a convention similar to
   [git trailer format](https://git-scm.com/docs/git-interpret-trailers).

We always use the scope to link the commit to the issue number. Example: 

```
docs(#1333): Improve the CONTRIBUTORS guide

* Invites to glab CLI to manage issues easily from the shell
* Add a short context in the branch name to better recognise the issue
* Use Conventional Commits to write commit messages

Closes #1333
```

Using the `Closes #1333` keyword [will automatically close the issue](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically) `1333` when the pull request is merged.

## Test

Of course, you should test your code before pushing ;o)
Genie comes with a `prod` profile that should be used before pushing.
It makes sure the Angular code follows all the type checking.

```
(issue-42-something-to-do) $ ./mvnw clean verify -Pprod
```

## Push changes

Once the tests pass, push the branch to the upstream

```
(issue-42-something-to-do) $ git push origin --set-upstream
```

## Merge the pull request

On GitLab go to `Merge Requests` and `Create merge request`.
Leave the title of the merge request and in the description add the keyword `Closes #42` this will automatically close the issue `42`.
Submit the merge request and click on `Merge`.

## Go back to master

Come back to your local branch, check out the master and pull your change.

```
(issue-42-something-to-do) $ git checkout master
(master) $ git pull
```

